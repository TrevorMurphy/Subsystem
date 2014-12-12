package com.textserv.framework.subsystem.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Address;
import com.textserv.framework.subsystem.calling.internal.amqp.AmqpCallingSubsystemService;
import com.textserv.framework.subsystem.server.receiving.internal.amqp.AmqpReceivingSubsystemService;
import com.textserv.framework.subsystem.server.receiving.internal.amqp.SubsystemHeartbeatService;
import com.textserv.messaging.AMQPSimpleService;


public class ServerSubsystemService {

	private static final Log logger = LogFactory.getLog(ServerSubsystemService.class.getName());
	
	public static void initialize(String serverUniqueName, Address[] amqpAddress, String amqpUsername, String amqpPassword, String vHost) {
		try {
			AMQPSimpleService amqpService = AMQPSimpleService.getAMQPService(amqpAddress, amqpUsername, amqpPassword, vHost);
			AmqpReceivingSubsystemService.getInstance(serverUniqueName);
			AmqpCallingSubsystemService.getInstance(serverUniqueName);
			amqpService.start();
			SubsystemHeartbeatService.getInstance();
			logger.debug("ServerSubsystemService.initialize Initialized");
		} catch( Exception e ) {
			logger.debug("ServerSubsystemService.initialize Failed", e);			
		}
	}
	
		public static void initialize(String serverUniqueName, String amqpAddress, String amqpUsername, String amqpPassword, String vHost) {
		try {
			Address[] amqp_addresses  = Address.parseAddresses(amqpAddress);
			initialize(serverUniqueName, amqp_addresses, amqpUsername, amqpPassword, vHost);
		} catch( Exception e ) {
			logger.debug("ServerSubsystemService.initialize Failed", e);			
		}
	}	

}
