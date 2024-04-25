package com.iot.parking.models;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="entries")
public class Entry {
	@Id
	@GeneratedValue
	private int id;
	@Column(name = "spot_id")
	private String spotId;
	@Column(name = "reservation_id")
	private int reservationId;
	@Column(name = "license_plate")
	private String licensePlate;
	@Column(name = "enter_time")
	private Date enterTime = new Date();
	@Column(name = "leave_time")
	private Date leaveTime = null;
	private Double cost = null;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSpotId() {
		return spotId;
	}
	public void setSpotId(String spotId) {
		this.spotId = spotId;
	}
	public int getReservationId() {
		return reservationId;
	}
	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	public Date getEnterTime() {
		return enterTime;
	}
	public void setEnterTime(Date enterTime) {
		this.enterTime = enterTime;
	}
	public Date getLeaveTime() {
		return leaveTime;
	}
	public void setLeaveTime(Date leaveTime) {
		this.leaveTime = leaveTime;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}

}
