package com.iot.parking.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iot.parking.ParkingLot;
import com.iot.parking.models.Entry;
import com.iot.parking.models.ParkingSpot;
import com.iot.parking.models.Rate;
import com.iot.parking.models.Reservation;
import com.iot.parking.models.User;
import com.iot.parking.utils.AuthenticatableServlet;
import com.iot.parking.utils.DatabaseUtil;

public class ReservationServlet extends AuthenticatableServlet {
	private static final Logger LOG = LoggerFactory.getLogger(ReservationServlet.class);
	
	private static final long serialVersionUID = 1L;
	
	private static  List<String> authPaths = new ArrayList<>(
		    Arrays.asList(
		    		"*"
		    		)
			);
	
	@Override
	protected List<String> getPaths() {
		return authPaths;
	}

	private Gson gson;
	
	private ParkingLot parkingLot;
	
	public ReservationServlet(ParkingLot parkingLot) {
        GsonBuilder gsonBuilder = new GsonBuilder();   
        this.gson = gsonBuilder.create();
        this.parkingLot = parkingLot;
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getPathInfo() == null) {
			User user = (User) request.getAttribute("user");
			List<Reservation> reservations;
			if (user.getRole().equals("ADMIN")) {
				reservations = DatabaseUtil.getReservations();
			}
			else {
				reservations = DatabaseUtil.getReservationsByUser(user);
			}
			
            String json = this.gson.toJson(reservations);
            response.setContentType("application/json");
            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
		else if (request.getPathInfo().contains("/entries")) {
			User user = (User) request.getAttribute("user");
			if (user.getRole().equals("ADMIN")) {
				List<Entry> entries = DatabaseUtil.getEntries();
	            String json = this.gson.toJson(entries);
	            response.setContentType("application/json");
	            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
	            response.setStatus(HttpServletResponse.SC_OK);
	            return;
			}
			else {
				response.setContentType("application/json");
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            return;
			}
        }
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
		} 
		catch (Exception e) { /*report an error*/ }
		if (request.getPathInfo() == null) {
			Reservation reservation = this.gson.fromJson(jb.toString(), Reservation.class);
			LOG.info("SpotId reservation {} {}", reservation.getSpotId(), reservation.getId());
			User user = (User) request.getAttribute("user");
			reservation.setUser(user);
			reservation = makeReservation(reservation);
			if(reservation != null) {
				reservation.setUser(null);
				String json = this.gson.toJson(reservation);
				response.setContentType("application/json");
				response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			String json = "{\"cause1\": \"Spot was already taken.\"}"; 
			response.setContentType("application/json");
			response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			return;
        }
		else if (request.getPathInfo().contains("/rate")) {
			User user = (User) request.getAttribute("user");
			Rate rate = this.gson.fromJson(jb.toString(), Rate.class);
			if (user.getRole().equals("ADMIN")) {
				Rate rateDb = DatabaseUtil.getRate(rate.getSpotId());
				rateDb.setPricePerMinute(rate.getPricePerMinute());
				DatabaseUtil.saveRate(rateDb);
				this.parkingLot.spotRateUpdated(rate);
	            String json = this.gson.toJson(rateDb);
	            response.setContentType("application/json");
	            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
	            response.setStatus(HttpServletResponse.SC_OK);
	            return;
			}
			else {
				response.setContentType("application/json");
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            return;
			}
		}
    }
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 String[] path = request.getPathInfo().split("/");
		 LOG.info("Length of path {} {}", request.getPathInfo(), path.length);
		 LOG.info("Length of path {} {}", path[0], path[1]);
		 Long reservationId = Long.parseLong(path[1]);
		 DatabaseUtil.deleteReservation(reservationId);
		 response.setContentType("application/json");
		 response.setStatus(HttpServletResponse.SC_OK);
	}
	
	private Reservation makeReservation(Reservation reservation) {
		LOG.info("Making reservation for spot {}", reservation.getSpotId());
		if(parkingLot.checkIfSpotIsFree(reservation.getSpotId())) {
			LOG.info("Client spot is free");
			//Extra check if there is no reservation still pending with the same license plate (cannot reserve two spots).
			if (DatabaseUtil.checkReservation(reservation.getLicensePlate(), "PENDING") == null) {
			reservation = DatabaseUtil.makeReservation(reservation);
			ParkingSpot reservedSpot = new ParkingSpot();
			reservedSpot.setId(reservation.getSpotId());
			reservedSpot.setState("Reserved");
			parkingLot.updateSpotById(reservedSpot);
			return reservation;
			}
		}
		return null;
	}
}
