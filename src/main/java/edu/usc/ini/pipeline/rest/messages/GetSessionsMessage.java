package edu.usc.ini.pipeline.rest.messages;

import pipeline.api.workflow.Connection;

public class GetSessionsMessage implements IncomingMessage {
	
	private Connection connection;
	
	public GetSessionsMessage(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

}
