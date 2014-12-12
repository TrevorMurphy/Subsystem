package com.textserv.framework.subsystem.server.receiving.internal.amqp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.subsystem.common.InProcessFunctionCallHanderSingleton;
import com.textserv.framework.subsystem.receiving.SubsystemImpl;

public class AmqpReceivingSubsystemService {
	protected AmqpFunctionCallReceiver inProcessReceiver = null;
	protected static AmqpReceivingSubsystemService receivingSubsystemService = null;
	private static final Log logger = LogFactory.getLog(AmqpReceivingSubsystemService.class.getName());
	
	protected AmqpReceivingSubsystemService( String uniqueName ) {
		//simple in proc receiver for now
		inProcessReceiver = new AmqpFunctionCallReceiver(uniqueName);
		InProcessFunctionCallHanderSingleton.setInProcessHandler(inProcessReceiver);
		SubsystemImpl.bindAll(inProcessReceiver);
		logger.debug("AMQPReceivingSubsystemService.AMQPReceivingSubsystemService Configured");
	}
	
	public static AmqpReceivingSubsystemService getInstance( String uniqueName ) {
		if ( receivingSubsystemService == null ) {
			receivingSubsystemService = new AmqpReceivingSubsystemService(uniqueName);
		}
		return receivingSubsystemService;
	}	
}
