package com.planet.courier.model;

import com.planet.courier.constant.Country;

public class Courier {
	
	private Integer id;
	private String email;
	private String phoneNumber;
	private Double parcelWeight;
	private Country country;
	
	
	@Override
	public String toString() {
		return "Courier [id=" + id + ", email=" + email + ", phoneNumber=" + phoneNumber + ", parcelWeight="
				+ parcelWeight + ", country=" + country + "]";
	}
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
	public Double getParcelWeight() {
		return parcelWeight;
	}
	public void setParcelWeight(Double parcelWeight) {
		this.parcelWeight = parcelWeight;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
	

}
