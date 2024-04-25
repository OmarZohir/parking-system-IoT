package com.iot.parking.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VehicleCounter implements LwM2MObject {
	
	private String id;
	private String lotName;
 	private String lastPlate;
	private int count;
	private String endpoint;
	private int direction; // O: Exit, 1: Enter
	
	public static final Map<String, Integer> mapInstanceId = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
		{
			put("objectId", 32801);
		    put("id", 32700);
            put("count", 32703);
            put("lastPlate", 32704);
            put("direction", 32705);
            put("lotName", 32706);
        }
    };
    
    public static final Integer objectId = mapInstanceId.get("objectId");
	
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
	public String getLastPlate() {
		return lastPlate;
	}
	public void setLastPlate(String lastPlate) {
		this.lastPlate = lastPlate;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public static final String getPropertyById(Integer id) {
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
