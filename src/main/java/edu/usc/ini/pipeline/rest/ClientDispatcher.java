package edu.usc.ini.pipeline.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jboss.logging.Logger;

import edu.usc.ini.pipeline.rest.messages.ConnectMessage;
import edu.usc.ini.pipeline.rest.messages.ConnectionEstablished;
import edu.usc.ini.pipeline.rest.messages.ConnectionFailed;
import edu.usc.ini.pipeline.rest.messages.GetSessionsMessage;
import edu.usc.ini.pipeline.rest.messages.OutcomingMessage;
import edu.usc.ini.pipeline.rest.messages.SessionListFailedMessage;
import edu.usc.ini.pipeline.rest.messages.SessionListMessage;
import edu.usc.ini.pipeline.rest.protocol.ConnectionResponse;
import edu.usc.ini.pipeline.rest.protocol.SessionsResponse;
import pipeline.api.workflow.Connection;
import pipeline.api.workflow.Session;

public class ClientDispatcher {
	
	private static final Logger logger = Logger.getLogger(ClientDispatcher.class);

	private Map<UUID, Connection> connectionsMap = new HashMap<>();
	
	private Map<UUID, ApiThread> apies = new HashMap<>();
	
	private UUID register(Connection connection) {
		logger.info("Register client");
		final UUID token = UUID.randomUUID();
		connectionsMap.put(token, connection);
		return token;
	}
	
	private ApiThread createApiThread(UUID token) {
		ApiThread apiThread = null;
		try {
			apiThread = new ApiThread();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		if (apiThread != null) {
			apiThread.start();
			apies.put(token, apiThread);
		}
		return apiThread;
	}

	private String unregister(UUID token) {
		logger.info("Unregister client "+token);
		Connection connection = connectionsMap.remove(token);
		
		ApiThread apiThread = apies.get(token);
		if (apiThread != null) {
			apiThread.shutdown();
		}
		
		return connection != null ? token.toString() : null;
	}

	public ConnectionResponse connect(String server, int port, String username, String password) {
		logger.info("Connect to server "+server + " with username="+username);
		Connection connection = new Connection(username, password, server, port);

		UUID newToken = register(connection);
		
		ApiThread apiThread = createApiThread(newToken);
		
		if (apiThread == null) {
			return new ConnectionResponse("Can't create connection. Internal service error.");
		}
		
		ConnectMessage connectMessage = new ConnectMessage(server, port, username, password);
		apiThread.addMessage(connectMessage);
		
		OutcomingMessage outMessage = apiThread.getMessage();
		logger.info("Connect result message "+outMessage);

		if (outMessage instanceof ConnectionFailed) {
			ConnectionFailed connectionFailed = (ConnectionFailed) outMessage;
			return new ConnectionResponse(connectionFailed.getError());
		} else if (outMessage instanceof ConnectionEstablished) {
			final ConnectionResponse connectionResponse = new ConnectionResponse();
			connectionResponse.setToken(newToken.toString());
			return connectionResponse;
		} else {
			return new ConnectionResponse("Unknown error.");
		}
	}

	public ConnectionResponse close(String token) {
		String result = unregister(UUID.fromString(token));
		return result != null ? new ConnectionResponse() : new ConnectionResponse("No connections with specified token.");
	}

	public SessionsResponse getSessionsList(String tokenText) {
		UUID token = UUID.fromString(tokenText);
		
		final Connection connection = connectionsMap.get(token);
		final ApiThread apiThread = apies.get(token);
		
		if (apiThread == null) {
			return new SessionsResponse("Connection not established.");
		}
		
		GetSessionsMessage getSessionsMessage = new GetSessionsMessage(connection);
		apiThread.addMessage(getSessionsMessage);

		OutcomingMessage outMessage = apiThread.getMessage();
		
		if (outMessage instanceof SessionListMessage) {
			SessionListMessage sessionListMessage = (SessionListMessage) outMessage;
			final List<Session> sessions = sessionListMessage.getSessions();
			SessionsResponse response = new SessionsResponse();
			response.setSessions(sessions);
			return response;
		} else if (outMessage instanceof SessionListFailedMessage) {
			return new SessionsResponse("Can't retrevie sessions.");
		} else {
			return new SessionsResponse("Unknown error.");
		}
	}	
}
