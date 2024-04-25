package com.iot.parking.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.iot.parking.utils.DatabaseUtil;

public class ParkingSpot implements LwM2MObject{	
	private String id;
	private String state;
	private String lotName;
	private String licensePlate;
	private String endpoint;
	private Double rate = 1.0;
	//TODO: ADD X and Y coordinates, from the Multiple Axis Joystick
	
	public static final Map<String, Integer> mapInstanceId = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
		{
		    put("objectId", 32800);
		    put("id", 32700);
            put("state", 32701);
            put("licensePlate", 32702);
            put("lotName", 32706);
        }
    };
    
    public static final Integer objectId = mapInstanceId.get("objectId");
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state)
	{
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLotName() {
		return lotName;
	}

	public void setLotName(String lotName) {
		this.lotName = lotName;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void updateRate() {
		if(id != null) {
			Rate rateDb = DatabaseUtil.getRate(id);
			Double newRate;
			if(rateDb != null) {
				newRate = rateDb.getPricePerMinute();
				if(newRate != null) {
					this.rate = newRate;
				}
			}
		}
	}
	
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	public static final String getPropertyById(int id) {
		Iterator<Map.Entry<String,Integer>> iter = mapInstanceId.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<String,Integer> entry = iter.next();
		    if (entry.getValue().equals(id)) {
		       return entry.getKey();
		    }
		}
		return null;
	}
	
}
