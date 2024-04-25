package com.iot.parking.models;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.leshan.core.util.RandomStringUtils;

@Entity
@Table(name="access_tokens")
public class AccessToken implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	private String token;
	@Column(name="user_id")
	private Long userId;

	private transient User user;
	
	public AccessToken() {
		String generatedString = RandomStringUtils.random(15, true, true);
		this.token = generatedString;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
		if(user != null) {
			this.userId = user.getId();
		}
	}

}
