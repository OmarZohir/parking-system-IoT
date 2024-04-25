package com.iot.parking.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iot.parking.ParkingLot;
import com.iot.parking.models.ParkingSpot;

public class SpotServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	private Gson gson;
	
	private ParkingLot parkingLot;
	
	public SpotServlet(ParkingLot parkingLot) {
        GsonBuilder gsonBuilder = new GsonBuilder();   
        this.gson = gsonBuilder.create();
        this.parkingLot = parkingLot;
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getPathInfo() == null) {
			Collection<ParkingSpot> spots = parkingLot.getParkingSpots();
            String json = this.gson.toJson(spots);
            response.setContentType("application/json");
            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
    }
	
	
}
