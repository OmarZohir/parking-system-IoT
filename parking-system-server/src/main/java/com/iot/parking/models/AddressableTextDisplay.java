package com.iot.parking.models;

import java.util.HashMap;
import java.util.Map;

public class AddressableTextDisplay {

	private String text;
	private VehicleCounter vehicleCounter; //Parking Spot the text display is associated with
	

	public static final Map<String, Integer> mapInstanceId = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
		{
		    put("objectId", 3341);
		    put("Text", 5527);
        }
    };
    
    public static final Integer objectId = mapInstanceId.get("objectId");
    

	public VehicleCounter getVehicleCounter() {
		return vehicleCounter;
	}

	public void setVehicleCounter(VehicleCounter vehicleCounter) {
		this.vehicleCounter = vehicleCounter;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}    
}