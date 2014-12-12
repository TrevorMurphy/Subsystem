package com.textserv.framework.subsystem.calling.internal.amqp;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.internal.async.SubsystemAsyncMessager;
import com.textserv.framework.subsystem.calling.internal.async.SubsystemAsyncMessagerListener;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.messaging.AMQPSimpleService;
import com.textserv.messaging.AmqpMessageListener;

public class AmqpSubsystemAsyncMessager extends AmqpSubsystemMessagePublisher implements SubsystemAsyncMessager, AmqpMessageListener {

	private HashMap<String, SubsystemAsyncMessagerListener> listeners = new HashMap<String, SubsystemAsyncMessagerListener>();
	private static final Log logger = LogFactory.getLog(AmqpSubsystemAsyncMessager.class.getName());
	private HashMap<String, String> subscriptionsMade = new HashMap<String, String>();
	protected static final String responseTopicPrefix = "com.textserv.framework.subsystem.function.response.";

	public AmqpSubsystemAsyncMessager() {
	}
	
	public void registerListener(String uniqueName,SubsystemAsyncMessagerListener listener) {
		listeners.put(uniqueName, listener);
	}

	protected String getResponseTopic(String uniqueName) {
		return responseTopicPrefix+uniqueName;
	}
	
	public void sendMessage(String uniqueName, DataObject message) {
		try {
			String responseTopic = subscriptionsMade.get(uniqueName);			
			if ( responseTopic == null ) {
				responseTopic = getResponseTopic(uniqueName);
				AMQPSimpleService.getAMQPService().subscribe(responseTopic, this);
				subscriptionsMade.put(uniqueName, responseTopic);
			}
			DataObject subsystemMeta = message.getDataObject(Constants.SubsystemMeta);
			subsystemMeta.setString(Constants.ReplyToTopic, responseTopic);
			publishAmqpMessage(message);
		} catch ( Exception e ) {
			logger.error("JMSSubsystemAsyncMessager.sendMessage Exception sending subsystem message", e);			
		}
	}

	public void onMessage(DataObject message) {
		logger.debug("AmqpSubsystemAsyncMessager.onMessage Received Message: "+ message.toString());			
		try {
			DataObject subsystemMeta = message.getDataObject(Constants.SubsystemMeta);
			String uniqueName = subsystemMeta.getString(com.textserv.framework.subsystem.calling.internal.async.Constants.AsyncSenderUniqueName);
			SubsystemAsyncMessagerListener listener = listeners.get(uniqueName);
			if ( listener != null ) {
				listener.onAsyncMessage(message);
			} else {
				logger.debug("AmqpSubsystemAsyncMessager.onMessage No listener for Message: "+ message.toString());							
			}
		} catch ( Exception e ) {
			logger.error("AmqpSubsystemAsyncMessager.onMessage Exception receiving subsystem message", e);			
		}		
	}

}
