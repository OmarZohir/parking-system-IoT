package com.iot.parking.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	private String username;
	private String password;
	private String name;
	private String role = "USER";
	
	private transient List<Reservation> reservations = new ArrayList<>();
	
	private transient AccessToken accessToken;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<Reservation> getReservations() {
		return reservations;
	}
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	public AccessToken getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	public void makeAccessToken() {
        accessToken = new AccessToken();
        accessToken.setUser(this);
    }
 
    public void removeAccessToken(AccessToken accessToken) {
    	accessToken.setUser(null);
    	accessToken = null;
    }
	
	public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setUser(this);
    }
 
    public void removeReservation(Reservation reservation) {
    	reservations.remove(reservation);
    	reservation.setUser(null);
    }
}
