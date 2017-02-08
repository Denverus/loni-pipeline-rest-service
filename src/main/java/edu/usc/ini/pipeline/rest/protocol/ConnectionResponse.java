package edu.usc.ini.pipeline.rest.protocol;

public class ConnectionResponse extends ResultResponse {

	private String token;
	
	public ConnectionResponse() {
		super();
	}

	public ConnectionResponse(String error) {
		super(error);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
