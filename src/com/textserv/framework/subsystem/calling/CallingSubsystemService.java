package com.textserv.framework.subsystem.calling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.subsystem.calling.internal.async.AsyncMessageFunctionCallCaller;
import com.textserv.framework.subsystem.calling.internal.async.InProcessSubsystemAsyncMessager;
import com.textserv.framework.subsystem.calling.internal.base.BaseSubsystemFactory;

public class CallingSubsystemService {
	private static final Log logger = LogFactory.getLog(CallingSubsystemService.class.getName());
	
	public static void initialize() {	
		
		//in process calls, handled within the calling thread, Async essentially ignored
//		InProcessFunctionCallCaller caller = new InProcessFunctionCallCaller(InProcessFunctionCallHanderSingleton.getInProcessHandler());

//Configured for now with an in process but asyn function call caller
//This set up would only be used for testing or if you wanted the in process
//Caller to mimic the behavior of async calls
		AsyncMessageFunctionCallCaller caller = new AsyncMessageFunctionCallCaller( "InProcAsync", new InProcessSubsystemAsyncMessager() ); 
		
		//Use base factory, configured with appropiate caller
		BaseSubsystemFactory.setFunctionCallCaller(caller);
		
		//configure all subsystems to use this factory
		Subsystem.setFactory( BaseSubsystemFactory.getInstance());
		logger.debug("CallingSubsystemService.initialize Initialized");		
		
	}
}
