package com.acoer.test.contact.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contacts")
public class Contact {

	private long ID;
	private String firstName;
	private String lastName;
	private int phoneNumber;

	//empty constructor for contact class
	public Contact() {}

	//fully populated constructor for contact class
	public Contact(long ID, String firstName, String lastName, int phoneNumber) {
		this.ID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
	}

	//getter method ID feild
	public long getID() {
		return ID;
	}
	//setter method for ID feild
	public void setID(long ID) {
		this.ID = ID;
	}

	//getter method firstName feild
	public String getFirstName() {
		return firstName;
	}
	//setter method for firstName feild
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	//getter method lastName feild
	public String getLastName() {
		return lastName;
	}
	//setter method for firstName feild
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	//getter method phoneNumber feild
	public int getPhoneNumber() {
		return phoneNumber;
	}
	//setter method for phoneNumber feild
	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
