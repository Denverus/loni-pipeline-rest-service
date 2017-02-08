package edu.usc.ini.pipeline.rest.messages;

import java.util.List;

import pipeline.api.workflow.Session;

public class SessionListFailedMessage implements OutcomingMessage {

	private List<Session> sessions;

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	} 
}
