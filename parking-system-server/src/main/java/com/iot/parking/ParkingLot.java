package com.iot.parking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iot.parking.models.AddressableTextDisplay;
import com.iot.parking.models.Entry;
//import org.eclipse.leshan.server.demo.*;
import com.iot.parking.models.ParkingSpot;
import com.iot.parking.models.Rate;
import com.iot.parking.models.Reservation;
import com.iot.parking.models.VehicleCounter;
import com.iot.parking.utils.Conversion;
import com.iot.parking.utils.DatabaseUtil;
import com.iot.parking.utils.ParkingLotAction;

import org.eclipse.leshan.core.request.ObserveRequest;

import java.beans.*;

public class ParkingLot 
{
	final static Logger LOG = LoggerFactory.getLogger(ParkingLot.class);
	
	// Parking Lot properties
	public static final String LOTNAME = "P3";
	
	private Collection<Registration> allRegs = new ArrayList<>();
	private Collection<Observation> allObs = new ArrayList<>();
	private Collection<ParkingSpot> parkingSpots = new ArrayList<>();
	private Collection<VehicleCounter> vehicleCounters = new ArrayList<>();
	private Collection<AddressableTextDisplay> TextDisplays = new ArrayList<>();

	private LeshanServer lServer;
	
	//All event listeners
	private final List<ActionListener> listeners = new ArrayList<ActionListener>();
	
	//Get number of spots that are still free
	public int getAvailable() {
		return getSpotsWithState("Free").size();
	}
	
	//Get number of spots that are reserved
	public int getReserved() {
		return getSpotsWithState("Reserved").size();
	}
	
	//Get number of spots that are still occupied
	public int getOccupied() {
		return getSpotsWithState("Occupied").size();
	}

	//Return all active parking spots
	public Collection<ParkingSpot> getParkingSpots (){
		return parkingSpots;
	}
	
	//Add extra listener
	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	
	//Get free, occupied, or reserved spots
	//NOTE: the state passed must match exactly one of: "Free", "Reserved", or "Occupied", otherwise you will get an empty list 
	public ArrayList<ParkingSpot> getSpotsWithState(String state){
		ArrayList<ParkingSpot> Spots = new ArrayList<>();
		//Can be optimized if binary search is used instead of the for loop
		for (ParkingSpot p: parkingSpots) {
			if (p.getState().equals(state)) {
				Spots.add(p);
			}
		}	
		return Spots;
	}
	
	//Notifying all listeners of a particular action, including some data
	private void notifyListeners(String actionS, Object data) {
		ParkingLotAction action = new ParkingLotAction(actionS, data);
		ActionEvent event = new ActionEvent(this, 0, action.toJSON());
	    for (ActionListener l : listeners) {
	      l.actionPerformed(event);
	    }
	}

	private final RegistrationListener registrationListener = new RegistrationListener() {
		
		//Collection<Registration> AllRegs = new ArrayList<>();
		
		@Override
		public void registered(Registration registration, Registration previousReg,
				Collection<Observation> previousObsersations) {
			
			if (!allRegs.contains(registration))
			{
				// Show some info about the registration
				LOG.info("new device: " + registration.getEndpoint());
				allRegs.add(registration);
				LOG.info("Total Registrations count: " + allRegs.size());
				
				LOG.info(registration.getId());
				
				// Check if the endpoint has a parking spot or a vehicle counter or both, through 2 read requests
				// Maybe through a read request of specific resources that are available only on a PS or on a VC
				boolean HAS_PS = false;
				boolean HAS_VC = false;
				
				ReadResponse ReadPS = readValueFromObject(registration, ParkingSpot.mapInstanceId, "state");
				ReadResponse ReadVC = readValueFromObject(registration, VehicleCounter.mapInstanceId, "count");

				if (ReadPS != null && ReadPS.getContent() != null)
					HAS_PS = true;
				if (ReadVC != null && ReadVC.getContent() != null)
					HAS_VC = true;
				LOG.info("readPS: {}", ReadPS);
				
				// If it is a Parking spot, set its values, and save it onto the array list, and observe Parking spot resources
				if (HAS_PS) {
					ParkingSpot Spot = new ParkingSpot();
					Spot.setId(String.valueOf(parkingSpots.size())); 
					Spot.setEndpoint(registration.getEndpoint());
					Spot.setLotName(LOTNAME);
					//Set the vehicle ID and the parking Spot state
					ReadResponse ReadVehicleID = readValueFromObject(registration, ParkingSpot.mapInstanceId, "licensePlate");
					
					Spot.setLicensePlate(Conversion.ResponsetoString(ReadVehicleID));
					Spot.setState(Conversion.ResponsetoString(ReadPS));
					Spot.updateRate();
					parkingSpots.add(Spot);
					notifyListeners("spot_added", Spot);
					
					//Write Lot name to client side on Registration
					sendValueToObject(registration, ParkingSpot.mapInstanceId, "lotName", LOTNAME);
					
					//Observe the needed resources from the parking spot
					//On Registration, Observe the following resources
					observeValueFromObject(registration, ParkingSpot.mapInstanceId, "id"); 
					observeValueFromObject(registration, ParkingSpot.mapInstanceId, "state"); 
					observeValueFromObject(registration, ParkingSpot.mapInstanceId, "licensePlate");
				}
				
				// If it is a Vehicle Counter, set its values, and save it onto the array list, and observe Vehicle counter resources
				if (HAS_VC) {
					VehicleCounter VehicleCntr = new VehicleCounter();
					VehicleCntr.setId(String.valueOf(vehicleCounters.size()));
					VehicleCntr.setEndpoint(registration.getEndpoint());
					VehicleCntr.setLotName(LOTNAME);
					
					AddressableTextDisplay display = new AddressableTextDisplay(); 
					display.setVehicleCounter(VehicleCntr);
					TextDisplays.add(display);
					
					//setting LastPlate, Count and Direction on registration
					ReadResponse ReadLastPlate = readValueFromObject(registration, VehicleCounter.mapInstanceId, "lastPlate");
					ReadResponse ReadCount = readValueFromObject(registration, VehicleCounter.mapInstanceId, "count");
					ReadResponse ReadDirection = readValueFromObject(registration, VehicleCounter.mapInstanceId, "direction"); 
					
					VehicleCntr.setLastPlate(Conversion.ResponsetoString(ReadLastPlate));
					VehicleCntr.setCount(Conversion.ResponsetoInt(ReadCount));
					VehicleCntr.setDirection(Conversion.ResponsetoInt(ReadDirection));
					
					vehicleCounters.add(VehicleCntr);
					
					//Write Lot name to client side on Registration
					sendValueToObject(registration, VehicleCounter.mapInstanceId,"id",LOTNAME);
					
					//On Registration, Observe the Vehicle Counter Object resources
					observeValueFromObject(registration, VehicleCounter.mapInstanceId,"id");
					observeValueFromObject(registration, VehicleCounter.mapInstanceId, "count");
					observeValueFromObject(registration, VehicleCounter.mapInstanceId,"lastPlate");
					observeValueFromObject(registration, VehicleCounter.mapInstanceId, "direction");
				}
			}	
		}

		@Override
		public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
			//Log if the registration is updated.
			LOG.info("Registration Updated: " + updatedReg.getEndpoint());
		}

		@Override
		public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
				Registration newReg) {
			for(Observation observation: observations) {
				int ObjID = observation.getPath().getObjectId(); 
				//If observation was for a parking spot remove the parking spot.
				if(ObjID == ParkingSpot.objectId) {
					Iterator<ParkingSpot> spotIterator = parkingSpots.iterator();
					while(spotIterator.hasNext()) {
						ParkingSpot spot = spotIterator.next();
						if(spot.getEndpoint() == registration.getEndpoint()) {
							notifyListeners("spot_removed", spot);
							spotIterator.remove();
						} 
					}
				}
				//If observation was for a vehicle counter remove the vehicle counter.
				else if(ObjID == VehicleCounter.objectId) {
					Iterator<VehicleCounter> counterIterator = vehicleCounters.iterator();
					while(counterIterator.hasNext()) {
						VehicleCounter counter = counterIterator.next();
						if(counter.getEndpoint() == registration.getEndpoint()) {
							notifyListeners("counter_removed", counter);
							counterIterator.remove();
						} 
					}
				}
			}
			
			LOG.info("Endpoint unregistered: " + registration.getEndpoint());
			//Remove the registration from the list.
			allRegs.remove(registration);
		}
		
	};
	
	
	private final ObservationListener observationListener = new ObservationListener() {
		@Override
		public void newObservation(Observation observation, Registration registration) {
			LOG.info("Observing resource " + observation.getPath() + " from client"  + observation.getId());
			//add to list of all observation if not exists
			if (!allObs.contains(observation))
				allObs.add(observation);
		}

		@Override
		public void cancelled(Observation observation) {
			LOG.info("Observation for resource " + observation.getPath() + " cancelled");
			allObs.remove(observation);
		}

		@Override
		//Show updates with what resources being changed
		public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
			
			int ObjID = observation.getPath().getObjectId(); 
			int RSCID = observation.getPath().getResourceId();
			String stringVal = null;
			int intVal = -1;
			
			LOG.info("Observe response: " + response.getCoapResponse());
			//Make a distinction between string types and integer types.
			if (RSCID != VehicleCounter.mapInstanceId.get("count") && RSCID != VehicleCounter.mapInstanceId.get("direction")){
				stringVal = Conversion.LwM2mResourceValtoString((LwM2mResource)(response.getContent()));
			}
			else {
				intVal = Conversion.LwM2mResourceValtoInt((LwM2mResource)(response.getContent()));
			}
			
			//Check if the update was a parking spot or vehicle counter
			if (ObjID == ParkingSpot.objectId) {
				//Search in the list of Parking spots for a the endpoint
				//Override the equals class in your Parking spot implementation
				for(ParkingSpot spot : parkingSpots){
			        if(spot.getEndpoint() == registration.getEndpoint()) {
			        	String property = ParkingSpot.getPropertyById(RSCID);
						switch(property) {
							case ("id"):
								spot.setId(stringVal);
								break;
							case ("state"):
								spot.setState(stringVal);
								break;
							case ("licensePlate"):
								spot.setLicensePlate(stringVal);
								break;
						}
						notifyListeners("spot_updated", spot);
			        }
				}
			}
			else if (ObjID == VehicleCounter.objectId){
				for(VehicleCounter v : vehicleCounters){
			        if(v.getEndpoint() == registration.getEndpoint()) {
			        	String property = VehicleCounter.getPropertyById(RSCID);
						switch(property) {
							case ("id"):
								v.setId(stringVal);
								break;
							case ("count"):
								v.setCount(intVal);
								break;
							case ("direction"):
								v.setDirection(intVal);
								break;
							case ("lastPlate"):
								v.setLastPlate(stringVal);
								//When we scan a license plate we need to take some action
								licensePlateScanned(v);
								break;
						}
						notifyListeners("counter_updated", v);
			        }
				}
			}
		}

	

		@Override
		public void onError(Observation observation, Registration registration, Exception error) {
			LOG.info("Observation error with code: " + error.getMessage());
		}
	};
	
	public Registration getRegistrationByEndpoint(String endpoint) {
		for(Registration registration : allRegs) {
	        if(registration.getEndpoint().equals(endpoint)) {
	            return registration;
	        }
	    }
	    return null;
	}
	
	public ParkingSpot getSpotById(String spotId) {
		for(ParkingSpot spot : parkingSpots) {
	        if(spot.getId().equals(spotId)) {
	            return spot;
	        }
	    }
	    return null;
	}
	
	public VehicleCounter getCounterById(String counterId) {
		for(VehicleCounter counter : vehicleCounters) {
	        if(counter.getId().equals(counterId)) {
	            return counter;
	        }
	    }
	    return null;
	}
	
	public boolean checkIfSpotIsFree(String spotId) {
		ParkingSpot spot = getSpotById(spotId);
		if(spot == null) return false;
		LOG.info("Spot state is {}", spot.getState());
		return (spot.getState().equals("Free"));
	}
	
	public void licensePlateScanned(VehicleCounter counter) {
		String message;
		//Check if the vehicle is leaving or entering the parking lot.
		if (counter.getDirection() == 0) {
			message = vehicleLeaving(counter.getLastPlate());
		}
		else {
			message = vehicleEntering(counter.getLastPlate());
		}
		//When we need to show a message send it to the addressable text display.
		if(message != null) {
			Registration registration = getRegistrationByEndpoint(counter.getEndpoint());
			sendValueToObject(registration, AddressableTextDisplay.mapInstanceId, "Text", message);
		}
		
		//Update all counter in the parking lot
		//This is not working since the property is read only.
		String nrOfVehicles = String.valueOf(getOccupied());
		for(VehicleCounter v : vehicleCounters){
			Registration registration = getRegistrationByEndpoint(v.getEndpoint());
			sendValueToObject(registration, VehicleCounter.mapInstanceId, "count", nrOfVehicles);
		}
	}
	
	public String vehicleEntering(String licensePlate) {
		LOG.info("Vehicle {} is entering", licensePlate);
		Reservation reservation = DatabaseUtil.checkReservation(licensePlate, null);
		ParkingSpot spot = new ParkingSpot(); //Spot that is going to be occupied
		Entry entry = new Entry();
		//Check if a reservation was made that still has to come.
		if (reservation != null && !reservation.getStatus().equals("DONE")) {
			//Set the spot according to the reservation
			entry.setSpotId(reservation.getSpotId());
			spot.setId(reservation.getSpotId());
			//Put the reservation on ACTIVE
			reservation.setStatus("ACTIVE");
			DatabaseUtil.saveReservation(reservation);
			notifyListeners("reservation_updated", reservation);
		}
		else {
			//When there is no reservation check if there is a spot available.
			Collection<ParkingSpot> availableSpots = this.getSpotsWithState("Free");
			if(availableSpots.size() == 0) {
				LOG.info("No spots available");
				return "No spots available";
			}
			//Assign the spot.
			spot = availableSpots.iterator().next();
			entry.setSpotId(spot.getId());
		}
		entry.setLicensePlate(licensePlate);
		notifyListeners("entry_added", entry);
		spot.setLicensePlate(licensePlate);
		spot.setState("Occupied");
		updateSpotById(spot);
		//Make message for the text display
		String str = String.format("Vehicle %s has occupied the spot %s", licensePlate, spot.getId());
		DatabaseUtil.saveEntry(entry);
		return str;
	}
	
	public String vehicleLeaving(String licensePlate) {
		//Get the entry from the database
		Entry entry = DatabaseUtil.getActiveEntry(licensePlate);
		if (entry == null) {
			LOG.info("Vehicle should not be in here");
			return "Vehicle was not in the parking lot";
		}
		//Check if the entry was attached to a reservation
		Reservation reservation = DatabaseUtil.checkReservation(licensePlate, null);
		ParkingSpot spot = this.getSpotById(entry.getSpotId());
		if (reservation != null && !reservation.getStatus().equals("DONE")) {
			//Put the reservation on done
			reservation.setStatus("DONE");
			DatabaseUtil.saveReservation(reservation);
			notifyListeners("reservation_updated", reservation);
			LOG.info("Reservation was made");
		}
		//Update the spot, so it is free again
		spot.setState("Free");
		spot.setLicensePlate(null);
		updateSpotById(spot);
		//Set the entry to finished
		entry.setLeaveTime(new Date());
		//Calculating the cost of the parking based on the time spent. 
		Duration duration = Duration.between(entry.getEnterTime().toInstant(), entry.getLeaveTime().toInstant());
		long diff = Math.abs(duration.toMinutes());
		Double cost = diff * spot.getRate()/100;
		entry.setCost(cost);
		DatabaseUtil.saveEntry(entry);
		notifyListeners("entry_updated", entry);
		String str = String.format("You have to pay %.2f", cost);
		return str;
	}
	
	public ParkingSpot updateSpotById(ParkingSpot newSpot) {
		ParkingSpot currentSpot = getSpotById(newSpot.getId());
		Registration registration = getRegistrationByEndpoint(currentSpot.getEndpoint());
		try {
			//Use the Introspector to update the values that are not null.
			for (PropertyDescriptor pd : Introspector.getBeanInfo(newSpot.getClass()).getPropertyDescriptors()) {
				//Check if there is a read method and there is a write method, and that it is not the id property or the constructor.
				  if (pd.getReadMethod() != null && pd.getWriteMethod() != null && !"class".equals(pd.getName()) && !pd.getName().equals("id")) {
					  Object value =  pd.getReadMethod().invoke(newSpot);
					  LOG.info("Getting {}: {}", pd.getName(), value);
					  if(value != null ) {
						  LOG.info("Write method: {}",  pd.getWriteMethod());
						  pd.getWriteMethod().invoke(currentSpot, value);
						  //Also update the parking spot client.
						  sendValueToObject(registration, ParkingSpot.mapInstanceId, pd.getName(), value);						  
					  }
				  }
			  }
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Notify all web clients that the spot has been updated.
		notifyListeners("spot_updated", currentSpot);
		return currentSpot;
	}
	
	public void spotRateUpdated(Rate rate) {
		//Update the spots with there new rate.
		ParkingSpot spot = new ParkingSpot();
		spot.setId(rate.getSpotId());
		spot.setRate(rate.getPricePerMinute());
		this.updateSpotById(spot);
	}
	
	//Get a particular value from a particular LwM2M client
	public ReadResponse readValueFromObject(Registration registration, Map<String, Integer> map, String property){
		Integer objectId = map.get("objectId");
		Integer propertyId = map.get(property);
		LOG.info("object: {}, property: {}, propertyId: {}", objectId, property, propertyId);
		if(objectId != null && propertyId != null) {
			try {
				ReadResponse response = lServer.send(registration, new ReadRequest(objectId,0,propertyId));
				return response;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	//Write a particular value to a particular LwM2M client
	public WriteResponse sendValueToObject(Registration registration, Map<String, Integer> mapInstanceId, String property, Object value){
		Integer objectId = mapInstanceId.get("objectId");
		Integer propertyId = mapInstanceId.get(property);
			if(objectId != null && propertyId != null) {
			WriteResponse response;
			try {
				response = lServer.send(registration, new WriteRequest(objectId,0,propertyId, (String) value));
				return response;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	//Enable the observation for a particular value from a particular LwM2M client.
	public ObserveResponse observeValueFromObject(Registration registration, Map<String, Integer> mapInstanceId, String property) {
		Integer objectId = mapInstanceId.get("objectId");
		Integer propertyId = mapInstanceId.get(property);
		LOG.info("Observing {}, {} - {}", objectId, propertyId, property);
		if(objectId != null && propertyId != null) {
			try {
				ObserveResponse response = lServer.send(registration, new ObserveRequest(objectId,0, propertyId));
				return response;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	

	public void start() {
		
		System.out.println("Starting server");
		LeshanServerBuilder builder = new LeshanServerBuilder();
    	lServer = builder.build();
    	
    	lServer.start();
    	
    	lServer.getRegistrationService().addListener(registrationListener);
    	lServer.getObservationService().addListener(observationListener);
	}
   
}
