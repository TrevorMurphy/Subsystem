package com.textserv.framework.subsystem.calling.internal.amqp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.subsystem.calling.Subsystem;
import com.textserv.framework.subsystem.calling.internal.base.BaseSubsystemFactory;

public class AmqpCallingSubsystemService {
	private static final Log logger = LogFactory.getLog(AmqpCallingSubsystemService.class.getName());
	protected AmqpForwardingFunctionCallCaller caller = null;
	protected static AmqpCallingSubsystemService callingSubsystemService = null;
		
	public AmqpCallingSubsystemService(String uniqueName) {				
		caller = new AmqpForwardingFunctionCallCaller(uniqueName); 	
		//Use base factory, configured with appropiate caller
		BaseSubsystemFactory.setFunctionCallCaller(caller);			
		//configure all subsystems to use this factory
		Subsystem.setFactory( BaseSubsystemFactory.getInstance());
		logger.debug("AmqpCallingSubsystemService.AmqpCallingSubsystemService Configured");		
	}
	
	public static AmqpCallingSubsystemService getInstance( String uniqueName ) {
		if ( callingSubsystemService == null ) {
			callingSubsystemService = new AmqpCallingSubsystemService(uniqueName);
		}
		return callingSubsystemService;
	}	
}
