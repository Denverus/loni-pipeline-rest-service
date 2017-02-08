package edu.usc.ini.pipeline.rest.protocol;

import java.util.List;

import pipeline.api.workflow.Session;

public class SessionsResponse extends ResultResponse {

	private List<Session> sessions;
	
	public SessionsResponse() {
		super();
	}

	public SessionsResponse(String error) {
		super(error);
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}
}
