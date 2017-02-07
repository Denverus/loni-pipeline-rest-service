package edu.usc.ini.pipeline.rest;

import java.util.UUID;

public class Client {
	private UUID id;
	
	public Client() {
		id = UUID.randomUUID();
	}
	
	public UUID getId() {
		return id;
	}
}