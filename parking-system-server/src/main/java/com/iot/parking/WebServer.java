package com.iot.parking;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iot.parking.servlet.AuthenticationServlet;
import com.iot.parking.servlet.EventServlet;
import com.iot.parking.servlet.ReservationServlet;
import com.iot.parking.servlet.SpotServlet;



public class WebServer {
	final Logger LOG = LoggerFactory.getLogger(WebServer.class);
	
	private Server server;
	private ParkingLot parkingLot;
	
	WebServer(ParkingLot parkingLot) {
		this.parkingLot = parkingLot;
	}

    public void start() throws Exception {
    	//Create the Jetty server
        int webPort = 8082;
        InetSocketAddress jettyAddr = new InetSocketAddress(webPort);
    	server = new Server(jettyAddr);
    	
    	WebAppContext root = new WebAppContext();
    	root.setContextPath("/");
    	root.setResourceBase(WebServer.class.getClassLoader().getResource("webapp").toExternalForm());
    	root.setParentLoaderPriority(true);
    	
    	//Registering all the servlets
    	AuthenticationServlet authenticationServlet = new AuthenticationServlet();
		ServletHolder authenticationServletHolder = new ServletHolder(authenticationServlet);
		root.addServlet(authenticationServletHolder, "/api/auth/*");

    	SpotServlet spotServlet = new SpotServlet(parkingLot);
        ServletHolder spotServletHolder = new ServletHolder(spotServlet);
        root.addServlet(spotServletHolder, "/api/spots/*");
        
    	ReservationServlet reservationServlet = new ReservationServlet(parkingLot);
        ServletHolder reservationServletHolder = new ServletHolder(reservationServlet);
        root.addServlet(reservationServletHolder, "/api/reservations/*");
        
       
        EventServlet eventServlet = new EventServlet(parkingLot);
        ServletHolder eventServletHolder = new ServletHolder(eventServlet);
        eventServletHolder.setAsyncSupported(true);
        root.addServlet(eventServletHolder, "/api/events/*");
        
        server.setHandler(root);
        server.start();
        
        LOG.info("Web server started at {}.", server.getURI());
         
    }
}
