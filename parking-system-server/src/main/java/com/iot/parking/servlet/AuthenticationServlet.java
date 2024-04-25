package com.iot.parking.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iot.parking.models.Reservation;
import com.iot.parking.models.User;
import com.iot.parking.utils.AuthenticatableServlet;
import com.iot.parking.utils.DatabaseUtil;

public class AuthenticationServlet extends AuthenticatableServlet{
	private static final Logger LOG = LoggerFactory.getLogger(SpotServlet.class);
	
	private static final long serialVersionUID = 1L;
	
	//Set which paths need to be authenticated
	protected static List<String> authPaths = new ArrayList<>(
		    Arrays.asList(
		    		"/me"
		    		)
			);
	
	private Gson gson;
	
	public AuthenticationServlet() {
        GsonBuilder gsonBuilder = new GsonBuilder();   
        this.gson = gsonBuilder.create();
	}
	
	@Override
	protected List<String> getPaths() {
		return authPaths;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getPathInfo().equals("/me")) {
			User user = (User) request.getAttribute("user");
			if(user != null) {
				user.setAccessToken(null);
				user.setReservations(null);
				user.setPassword(null);
				String json = this.gson.toJson(user);
	            response.setContentType("application/json");
	            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
	            response.setStatus(HttpServletResponse.SC_OK);
			}
			else {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		else if (request.getPathInfo().equals("/logout")) {
			User user = (User) request.getAttribute("user");
			List<Reservation> reservations = DatabaseUtil.getReservationsByUser(user);
            String json = this.gson.toJson(reservations);
            response.setContentType("application/json");
            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getPathInfo().equals("/login")) {
			StringBuffer jb = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null) {
					jb.append(line);
				}
			}
			catch (Exception e) { /*report an error*/ }
			User user = this.gson.fromJson(jb.toString(), User.class);
			User dbUser = DatabaseUtil.getUserByUsername(user.getUsername());
			if(dbUser != null) {
				try {
					if(validatePassword(user.getPassword(), dbUser.getPassword())) {
						dbUser.makeAccessToken();
						DatabaseUtil.saveAccessToken(dbUser);
						dbUser.setPassword(null);
						LOG.info("User is authenticated");
						String json = this.gson.toJson(dbUser.getAccessToken());
						response.setContentType("application/json");
						response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
			            response.setStatus(HttpServletResponse.SC_OK);
			            return; 
					}
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
			LOG.info("User is not authenticated");
			response.setContentType("application/json");
			String json = "{\"error\": \"Credentials incorrect.\"}"; 
            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		else if (request.getPathInfo().equals("/register")) {
			StringBuffer jb = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null) {
					jb.append(line);
				}
			} 
			catch (Exception e) { /*report an error*/ }
				User user = this.gson.fromJson(jb.toString(), User.class);
				//check if user is already registered
				User dbUser = DatabaseUtil.getUserByUsername(user.getUsername());
				if(dbUser == null) {
					try {
						//Hash the password before storing in the database
						String hashedPassword = getHash(user.getPassword());
						user.setPassword(hashedPassword);
						DatabaseUtil.saveUser(user);
						user.makeAccessToken();
						DatabaseUtil.saveAccessToken(dbUser);
						response.setContentType("application/json");
						String json = this.gson.toJson(user.getAccessToken());
						response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
			            response.setStatus(HttpServletResponse.SC_OK);
			            return; 
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				response.setContentType("application/json");
				String json = "{\"error\": \"User already exists.\"}"; 
	            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
	            response.setStatus(HttpServletResponse.SC_OK);
				return;
		}
		response.setContentType("application/json");
		String json = "{\"error\": \"Cannot find endpoint.\"}"; 
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;
    }
	
	private static String getHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();
         
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }
     
	private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
		if(originalPassword == null || storedPassword == null) {return false;}
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
         
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
         
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
	
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
     
    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
    
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
