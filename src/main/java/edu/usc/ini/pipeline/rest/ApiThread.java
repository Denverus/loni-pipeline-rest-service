package edu.usc.ini.pipeline.rest;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.logging.Logger;

import edu.usc.ini.pipeline.rest.messages.ConnectMessage;
import edu.usc.ini.pipeline.rest.messages.ConnectionEstablished;
import edu.usc.ini.pipeline.rest.messages.ConnectionFailed;
import edu.usc.ini.pipeline.rest.messages.IncomingMessage;
import edu.usc.ini.pipeline.rest.messages.Message;
import edu.usc.ini.pipeline.rest.messages.OutcomingMessage;
import pipeline.api.PipelineAPI;
import pipeline.api.callback.ConnectionCallback;
import pipeline.api.workflow.Connection;

public class ApiThread extends Thread implements ConnectionCallback {
	
	private static final Logger logger = Logger.getLogger(ApiThread.class);
	
	private PipelineAPI pipelineApi;
	
	private volatile boolean stoped = false;
	
	private Queue<IncomingMessage> innerQueue;
	private Queue<OutcomingMessage> outcommingQueue;
	
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
		innerQueue.add(message);
		try {
			outcommingQueue.wait();
		} catch (InterruptedException e) {
			logger.error(e);
		}		
	}
	
	public OutcomingMessage getMessage() {
		OutcomingMessage message = null;
		/*while (outcommingQueue.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		};*/
		message = outcommingQueue.poll();
		return message;
	}	
	
	private void handleMessage(Message message) {
		logger.info("handleMessage "+message);
		if (message instanceof ConnectMessage) {
			ConnectMessage connectMessage = (ConnectMessage) message;
			Connection connection = new Connection(connectMessage.getUsername(), connectMessage.getPassword(), 
					connectMessage.getServer(), connectMessage.getPort());
			logger.info("Call PipelineApi to connect "+connection);
			pipelineApi.connect(connection , this);
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
		synchronized(outcommingQueue) {
			outcommingQueue.add(outcomingMessage);
			outcommingQueue.notify();
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
}
