package com.textserv.framework.subsystem.server.receiving.internal.amqp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.framework.subsystem.receiving.internal.FunctionCallImplementator;
import com.textserv.framework.subsystem.receiving.internal.InProcessFunctionCallReceiver;
import com.textserv.messaging.AMQPSimpleService;
import com.textserv.messaging.AmqpMessageListener;

public class AmqpFunctionCallReceiver extends InProcessFunctionCallReceiver implements AmqpMessageListener{

	private static final Log logger = LogFactory.getLog(AmqpFunctionCallReceiver.class.getName());
	protected static final String requestTopicPrefix = "com.textserv.framework.subsystem.function.request.";
	
	public AmqpFunctionCallReceiver( String uniqueName ) {
		super(uniqueName);
	}

	protected String getReceiverTopic( String subsystemName, String messageName ) {
		return requestTopicPrefix + subsystemName + "." + messageName;
	}
	
	public void register(String subsystemName, String functionName, FunctionCallImplementator impl) {
		super.register(subsystemName, functionName, impl);
		AMQPSimpleService.getAMQPService().subscribe(getReceiverTopic(subsystemName, functionName), this);
	}
		
	public void onMessage(DataObject message) {
		logger.debug("AmqpFunctionCallReceiver.onMessage Received message: " + message);
		try {
			sendReturnMessage(message, handleFuncCall(message));
		} catch ( Exception e ) {
			logger.error("AmqpFunctionCallReceiver.onMessage Exception processing message: " + message, e);						
		}		
	}
	
	protected void sendReturnMessage(DataObject message, DataObject returnMessage) throws Exception {
		DataObject subsystemMeta = message.getDataObject(Constants.SubsystemMeta);	
		String jmsReplyToTopic = subsystemMeta.getString(Constants.ReplyToTopic);		
		if ( jmsReplyToTopic != null ) {
			if ( returnMessage != null ) {
				AMQPSimpleService.getAMQPService().publish(jmsReplyToTopic, returnMessage);
			} else {
				logger.error("AmqpFunctionCallReceiver.onMessage No return message to send for message: " + message);					
			}
		} else {
			logger.error("AmqpFunctionCallReceiver.onMessage No ReplyTo or ReplyToSessionId set in SubsystemMeta: " + message);
		}
	}
}
