package com.iot.parking.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.iot.parking.models.User;
 
public class Authentication {
	final Logger LOG = LoggerFactory.getLogger(Authentication.class);
 
    public static User checkLoggedIn(String token) {
    	User user = DatabaseUtil.getUserByToken(token);
    	return user;
    }
    public boolean login(String username, String password) {
    	return true;
    }
    public boolean logout(String token) {
    	return true;
    }
}
