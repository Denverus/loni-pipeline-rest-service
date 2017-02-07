package edu.usc.ini.pipeline.rest.protocol;

public class RegistrationResponse extends ResultResponse {
	private String clientId;

	public RegistrationResponse(String clientId) {
		super();
		this.clientId = clientId;
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String id) {
		this.clientId = id;
	}
}
