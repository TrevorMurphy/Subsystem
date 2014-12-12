package com.textserv.framework.subsystem.calling.internal.amqp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.messaging.AMQPSimpleService;

public class AmqpSubsystemMessagePublisher {
	protected static final String requestExchangePrefix = "com.textserv.framework.subsystem.function.request.";
	private static final Log logger = LogFactory.getLog(AmqpSubsystemMessagePublisher.class.getName());

	public AmqpSubsystemMessagePublisher(){
	}

	protected String getSenderExchange( String subsystemName, String messageName ) {
		return requestExchangePrefix + subsystemName + "." + messageName;
	}
	
	public void publishAmqpMessage( DataObject message ) {
		try {
			DataObject subsystemMeta = message.getDataObject(Constants.SubsystemMeta);		
			String subsystemName = subsystemMeta.getString(Constants.SubsystemName);
			String messageName = subsystemMeta.getString(Constants.MessageName);
			String topicName = getSenderExchange(subsystemName,messageName);
			AMQPSimpleService.getAMQPService().publish(topicName, message);
		} catch ( Exception e ) {
			logger.error("AmqpSubsystemMessagePublisher.publishAmqpMessage Exception sending subsystem message", e);			
		}		
	}
}
