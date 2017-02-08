package edu.usc.ini.pipeline.rest.messages;

public class ConnectionFailed implements OutcomingMessage {

	private String error;
	
	public ConnectionFailed(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
