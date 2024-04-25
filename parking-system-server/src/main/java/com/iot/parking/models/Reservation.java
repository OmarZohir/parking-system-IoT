package com.iot.parking.models;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="reservations")
public class Reservation {
	@Id
	@GeneratedValue
	private Long id;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "spot_id")
	private String spotId;
	@Column(name = "license_plate")
	private String licensePlate;
	private String status = "PENDING";
	@Column(name = "created_at")
	private Date createdAt;
	
	private transient User user;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public String getSpotId() {
		return spotId;
	}

	public void setSpotId(String spotId) {
		this.spotId = spotId;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if(user != null) {
			this.userId = user.getId();
		}
		else {
			this.userId = null;
		}
	}
	
}
