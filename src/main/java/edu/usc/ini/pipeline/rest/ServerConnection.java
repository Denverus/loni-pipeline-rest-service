package edu.usc.ini.pipeline.rest;

import java.util.UUID;

import pipeline.api.workflow.Connection;

public class ServerConnection {
	
	private Connection connection;
	
	private UUID id;
	
	public ServerConnection() {
		id = UUID.randomUUID();
	}
	
	public UUID getId() {
		return id;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}