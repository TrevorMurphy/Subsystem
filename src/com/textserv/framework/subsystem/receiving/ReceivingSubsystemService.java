package com.textserv.framework.subsystem.receiving;

import com.textserv.framework.subsystem.common.InProcessFunctionCallHanderSingleton;
import com.textserv.framework.subsystem.receiving.internal.InProcessFunctionCallReceiver;

public class ReceivingSubsystemService {
	protected InProcessFunctionCallReceiver inProcessReceiver = null;
	protected static ReceivingSubsystemService receivingSubsystemService = null;
	
	protected ReceivingSubsystemService( String uniqueName ) {
		//simple in proc receiver for now
		inProcessReceiver = new InProcessFunctionCallReceiver(uniqueName);
		InProcessFunctionCallHanderSingleton.setInProcessHandler(inProcessReceiver);
		SubsystemImpl.bindAll(inProcessReceiver);
	}
	
	public static void initialize( String uniqueName ) {
		if ( receivingSubsystemService == null ) {
			receivingSubsystemService = new ReceivingSubsystemService(uniqueName);
		}
	}	
}
