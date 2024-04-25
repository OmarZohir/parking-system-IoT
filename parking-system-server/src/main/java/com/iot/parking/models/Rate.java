package com.iot.parking.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rates")
public class Rate {
	@Id
	@GeneratedValue
	private int id;
	@Column(name = "spot_id")
	private String spotId;
	@Column(name = "price_per_minute")
	private Double pricePerMinute;
	
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
	public Double getPricePerMinute() {
		return pricePerMinute;
	}
	public void setPricePerMinute(Double pricePerMinute) {
		this.pricePerMinute = pricePerMinute;
	}

	
	
}
