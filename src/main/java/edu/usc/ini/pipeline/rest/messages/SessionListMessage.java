package edu.usc.ini.pipeline.rest.messages;

import java.util.List;

import pipeline.api.workflow.Session;

public class SessionListMessage implements OutcomingMessage {
	
	private List<Session> sessions;
	
	public SessionListMessage(List<Session> sessions) {
		this.sessions = sessions;
	}

	public List<Session> getSessions() {
		return sessions;
	}
}
