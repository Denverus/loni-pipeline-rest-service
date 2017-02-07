package edu.usc.ini.pipeline.rest.messages;

public class ConnectMessage implements IncomingMessage {
	private String server;
	private int port;
	private String username;
	private String password;
	public ConnectMessage(String server, int port, String username, String password) {
		super();
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
