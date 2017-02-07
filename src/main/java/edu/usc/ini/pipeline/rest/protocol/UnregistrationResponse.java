package edu.usc.ini.pipeline.rest.protocol;

public class UnregistrationResponse extends ResultResponse {
	private String clientId;

	public UnregistrationResponse(String clientId) {
		super();
		this.clientId = clientId;
	}
	
	public UnregistrationResponse() {
		super("Client is not registered.");
	}	
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String id) {
		this.clientId = id;
	}
}
