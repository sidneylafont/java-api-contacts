package com.acoer.test.contact.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acoer.test.contact.domain.Contact;
import com.acoer.test.contact.service.ContactService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.mongodb.MongoWriteException;

@Controller
@RequestMapping("/contacts")
@Api(tags = { "Contacts API" })
public class ContactController {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get all contacts", notes = "Get all contacts")
	@ResponseBody
	public ResponseEntity<List<Contact>> getContacts() {
		logger.info("Retrieving list of contacts");
		List<Contact> result = new ArrayList<Contact>();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	//creating contact and adding it to MongoDB
	@RequestMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "adds a contact", notes = "adds a contact")
	public ResponseEntity<?> add(Contact contact) throws Exception {

		logger.info("creating/adding to contacts");

		//get all contacts
		List<Contact> contactList = contactService.getAll();
		for (int i = 0; i < contactList.size(); i++) {
			//if phone number already existing in DB throw an error
			if (contactList.get(i).getPhoneNumber() == contact.getPhoneNumber() ) {
				return new ResponseEntity<>("Duplicate Number",HttpStatus.OK);
			}
		}

		//attempt to add contact
		try {
			contactService.add(contact);
			return new ResponseEntity<>("Entry saved successfully", HttpStatus.OK);

		//catching duplicate errors (contact already exists in DB)
		} catch (MongoWriteException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		} 
	}

	//updating phone number of contact method
	@RequestMapping(value = "/update", produces =  MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "updates a contact's phone number", notes = "updates a contact's phone number")
	public ResponseEntity<?> update( Contact contact, int phoneNumber) throws Exception {

		logger.info("updating phone number of a contact");

		//get all contacts
		List<Contact> contactList = contactService.getAll();

		Contact oldContact = null;

		for (int i = 0; i < contactList.size(); i++) {
			//if current contact has the right ID update its phone number
			if (contactList.get(i).getID() == contact.getID() ) {
				oldContact = contactList.get(i);
			}
			//if current contact doesn't have matching ID and has the updated phone number throw an error
			else if (contactList.get(i).getPhoneNumber() == phoneNumber) {
				return new ResponseEntity<>("Duplicate phone number",HttpStatus.OK);
			}
		}

		if (oldContact == null) {
			return new ResponseEntity<>("Contact doesn't exist in DB",HttpStatus.OK);
		}

		Contact updatedContact = new Contact(oldContact.getID(), oldContact.getFirstName(), oldContact.getLastName(), phoneNumber);

		//updating phone number and DB
		contactService.update(oldContact, updatedContact);
		return new ResponseEntity<>("Contact successfully updated",HttpStatus.CONFLICT);
	}

	//deletes a contact
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ApiOperation(value = "deletes a contact", notes = "deletes a contact")
	public ResponseEntity<?> delete(Contact contact) throws Exception {

		logger.info("deleting contact");

		//attempts to delete contact
		try {
			contactService.delete(contact);
			return new ResponseEntity<>("Number deleted successfully", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Contact doesn't exist in DB, cannot delete it",HttpStatus.CONFLICT);
		}
	}

	
	// get all contacts, sorted by first name
	@RequestMapping(method = RequestMethod.GET, value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = " get all contacts, sorted by first name", notes = " get all contacts, sorted by first name")
	public ResponseEntity<List<Contact>> getAllSortedFirstName() {

		logger.info("getting all contacts, sorted by first name");

		// Retrives all contacts into array list
		List<Contact> contactList = contactService.getAll();

		//sorting by first name
		contactList.sort(Comparator.comparing(Contact::getFirstName));

		// returns, sorted list (by first name), and HTTP response
		return new ResponseEntity<>(contactList, HttpStatus.OK);

	}
	
	// get all contacts, sorted by last name
	@RequestMapping(method = RequestMethod.GET, value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = " get all contacts, sorted by last name", notes = " get all contacts, sorted by last name")
	public ResponseEntity<List<Contact>> getAllSortedLastName() {

		logger.info("getting all contacts, sorted by last name");

		// Retrives all contacts into array list
		List<Contact> contactList = contactService.getAll();

		//sorting by last name
		contactList.sort(Comparator.comparing(Contact::getLastName));

		// returns sorted list (by first name), and HTTP response
		return new ResponseEntity<>(contactList, HttpStatus.OK);
	}


	// Search Operation - Searches for contact via Phone number
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "searching contacts based on a field", notes = "searching contacts based on a field")
	public ResponseEntity<List<Contact>> showContact(String searchTerm, Boolean sortByFirst) throws Exception {

		logger.info("searching DB based on: " + searchTerm);

		//searching by provided search term
		List<Contact> contacts;
		try {
			contacts = contactService.search(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Contact>>(HttpStatus.CONFLICT);
		}

		// sorting by first name
		if (sortByFirst) {
			contacts.sort(Comparator.comparing(Contact::getFirstName));
		// sorting by last name
		} else {
			contacts.sort(Comparator.comparing(Contact::getLastName));
		}

		// returns sorted list (by first or last name), and HTTP response
		return new ResponseEntity<>(contacts, HttpStatus.OK);
	}
}
