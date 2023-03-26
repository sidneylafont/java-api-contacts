/**
 * 
 */
package com.acoer.test.contact.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import com.acoer.test.contact.domain.Contact;

@Service
public class ContactService implements ICrudOperations<Contact> {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private MongoOperations mongoOps;

	@Override
	public List<Contact> getAll() {
		logger.debug("getAll method called");
		return mongoOps.findAll(null);
	}

	@Override
	public List<Contact> search(String searchTerm) {
		logger.debug("searching by term");
		return mongoOps.findById(null,null, searchTerm);
	}

	@Override
	public Contact add(Contact item) {
		logger.debug("saving item");
		return mongoOps.save(item);
	}

	@Override
	public Contact update(Contact oldContact, Contact updatedContact) {
		logger.debug("updating item");
		mongoOps.remove(oldContact);
		return mongoOps.save(updatedContact);
	}

	@Override
	public void delete(Contact item) {
		logger.debug("deleting item");
		mongoOps.remove(item);
	}

}
