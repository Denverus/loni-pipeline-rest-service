package edu.usc.ini.pipeline.rest;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.logging.Logger;

import edu.usc.ini.pipeline.rest.messages.ConnectMessage;
import edu.usc.ini.pipeline.rest.messages.ConnectionEstablished;
import edu.usc.ini.pipeline.rest.messages.ConnectionFailed;
import edu.usc.ini.pipeline.rest.messages.GetSessionsMessage;
import edu.usc.ini.pipeline.rest.messages.IncomingMessage;
import edu.usc.ini.pipeline.rest.messages.Message;
import edu.usc.ini.pipeline.rest.messages.OutcomingMessage;
import edu.usc.ini.pipeline.rest.messages.SessionListMessage;
import pipeline.api.PipelineAPI;
import pipeline.api.callback.ConnectionCallback;
import pipeline.api.callback.SessionListCallback;
import pipeline.api.workflow.Connection;
import pipeline.api.workflow.Session;

public class ApiThread extends Thread implements ConnectionCallback, SessionListCallback {
	
	private static final Logger logger = Logger.getLogger(ApiThread.class);
	
	private PipelineAPI pipelineApi;
	
	private volatile boolean stoped = false;
	
	private LinkedBlockingQueue<IncomingMessage> innerQueue;
	private LinkedBlockingQueue<OutcomingMessage> outcommingQueue;
	
	public ApiThread() throws InterruptedException {
		pipelineApi = new PipelineAPI();
		innerQueue = new LinkedBlockingQueue<>();
		outcommingQueue = new LinkedBlockingQueue<>();
	}
	
	public void shutdown() {
		stoped = true;
	}
	
	public void addMessage(IncomingMessage message) {
		logger.info("New message "+message.getClass());
		try {
			outcommingQueue.clear();
			innerQueue.put(message);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
	
	public OutcomingMessage getMessage() {
		OutcomingMessage outMessage = null;
		try {
			outMessage = outcommingQueue.take();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return outMessage;
	}	
	
	private void handleMessage(Message message) {
		logger.info("handleMessage "+message);
		if (message instanceof ConnectMessage) {
			ConnectMessage connectMessage = (ConnectMessage) message;
			Connection connection = new Connection(connectMessage.getUsername(), connectMessage.getPassword(), 
					connectMessage.getServer(), connectMessage.getPort());
			logger.info("Call PipelineApi to connect "+connection);
			pipelineApi.connect(connection , this);
		} else if (message instanceof GetSessionsMessage) {
			GetSessionsMessage getSessionsMessage = (GetSessionsMessage) message; 
			Connection connection = getSessionsMessage.getConnection();
			pipelineApi.requestSessionList(connection , this);
		}
	}
	
	public void run() {
		while (!stoped) {
			Message message = innerQueue.poll();
			if (message != null) {
				handleMessage(message);
			}
		}
	}
	
	private void addOutcommingMessage(OutcomingMessage outcomingMessage) {
		try {
			outcommingQueue.put(outcomingMessage);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	@Override
	public void onConnectionEstablished(Connection connection) {
		logger.info("Connection established");
		addOutcommingMessage(new ConnectionEstablished());
	}

	@Override
	public void onConnectionFailed(Connection connection, String error) {
		logger.info("Connection failed. "+error);
		addOutcommingMessage(new ConnectionFailed(error));
	}

	@Override
	public void onSessionList(List<Session> sessions) {
		logger.info("Got sessions list "+sessions.size());
		addOutcommingMessage(new SessionListMessage(sessions));
	}
}
