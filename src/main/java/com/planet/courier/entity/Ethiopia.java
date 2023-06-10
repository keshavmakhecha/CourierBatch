package com.planet.courier.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Ethiopia {
	@Id
	private Integer id;
	private String email;
	private String phoneNumber;
	private String parcelWeight;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getParcelWeight() {
		return parcelWeight;
	}
	public void setParcelWeight(String parcelWeight) {
		this.parcelWeight = parcelWeight;
	}
	@Override
	public String toString() {
		return "Ethiopia [id=" + id + ", email=" + email + ", phoneNumber=" + phoneNumber + ", parcelWeight="
				+ parcelWeight + "]";
	}
	
}
