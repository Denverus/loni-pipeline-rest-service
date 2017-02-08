package edu.usc.ini.pipeline.rest.protocol;

public class ResultResponse {

	public static final int OK = 0;
	public static final int ERROR = 1;
	
	private int code;
	
	private String error;
	
	public ResultResponse() {
		this.code = OK; 
	}
	
	public ResultResponse(String error) {
		this.code = ERROR; 
		this.error = error;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
