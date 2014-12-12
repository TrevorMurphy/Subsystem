package com.textserv.framework.subsystem.calling.internal.amqp;

import com.textserv.framework.subsystem.calling.internal.async.AsyncMessageFunctionCallCaller;

//this class just here to make Amqp stuff cleaner, easier and self contained 
public class AmqpMessageFunctionCallCaller extends AsyncMessageFunctionCallCaller {
	
	public AmqpMessageFunctionCallCaller( String uniqueName ) {
		super(uniqueName, new AmqpSubsystemAsyncMessager() );
	}
}
