package edu.usc.ini.pipeline.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientDispatcher {

	private Map<UUID, Client> clients = new HashMap<>();
	
	public String register() {
		Client client = new Client();
		clients.put(client.getId(), client);
		return client.getId().toString();
	}

	public String unregister(String clientId) {
		Client client = clients.remove(UUID.fromString(clientId));
		return client != null ? client.getId().toString() : null;
		
	}	
}
