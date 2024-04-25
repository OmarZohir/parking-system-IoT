package com.iot.parking.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ParkingLotAction{
	private Object data;
	private String action;
	public ParkingLotAction(String action, Object data){
		this.action = action;
		this.data = data;
	}
	
	public String toJSON() {
		GsonBuilder gsonBuilder = new GsonBuilder();   
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(this);
        return json;
	}
	
	public String getAction() {
		return action;
	}
	
	public String jsonData() {
		GsonBuilder gsonBuilder = new GsonBuilder();   
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(this.data);
        return json;
	}
}
