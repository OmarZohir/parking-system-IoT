package com.iot.parking;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	//Starting the LwM2M server
    	ParkingLot parkingLot = new ParkingLot();
    	parkingLot.start();
    	
    	//Starting the web server
    	WebServer webServer = new WebServer(parkingLot);
    	webServer.start();
    }
}
