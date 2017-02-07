package edu.usc.ini.pipeline.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jboss.logging.Logger;

import edu.usc.ini.pipeline.rest.messages.ConnectMessage;
import edu.usc.ini.pipeline.rest.messages.ConnectionEstablished;
import edu.usc.ini.pipeline.rest.messages.ConnectionFailed;
import edu.usc.ini.pipeline.rest.messages.OutcomingMessage;
import edu.usc.ini.pipeline.rest.protocol.ConnectionResponse;

public class ClientDispatcher {
	
	private static final Logger logger = Logger.getLogger(ClientDispatcher.class);

	private Map<UUID, Client> clients = new HashMap<>();
	
	private Map<UUID, ApiThread> apies = new HashMap<>();
	
	public String register() {
		logger.info("Register client");
		Client client = new Client();
		clients.put(client.getId(), client);
		return client.getId().toString();
	}

	public String unregister(String clientId) {
		logger.info("Unregister client "+clientId);
		Client client = clients.remove(UUID.fromString(clientId));
		
		ApiThread apiThread = apies.get(client.getId());
		if (apiThread != null) {
			apiThread.shutdown();
		}
		
		return client != null ? client.getId().toString() : null;
	}

	public ConnectionResponse connect(String clientId, String server, int port, String username, String password) {
		logger.info("Connect client "+clientId+" to server "+server);
		
		Client client = clients.get(UUID.fromString(clientId));
		
		if (client == null) {
			return new ConnectionResponse("Client is not registered.");
		}
		
		ApiThread apiThread = apies.get(clientId);
		
		if (apiThread == null) {
			try {
				apiThread = new ApiThread();
			} catch (Exception e) {
				return new ConnectionResponse("Can't create connection. Internal service error.");
			}
			apies.put(client.getId(), apiThread);
			apiThread.start();
		}
		
		ConnectMessage connectMessage = new ConnectMessage(server, port, username, password);
		apiThread.addMessage(connectMessage);
		
		OutcomingMessage outMessage = apiThread.getMessage();
		logger.info("Connect result message "+outMessage);

		if (outMessage instanceof ConnectionFailed) {
			ConnectionFailed connectionFailed = (ConnectionFailed) outMessage;
			return new ConnectionResponse(connectionFailed.getError());
		} else if (outMessage instanceof ConnectionEstablished) {
			return new ConnectionResponse();
		} else {
			return new ConnectionResponse("Unknown error.");
		}
	}	
}
