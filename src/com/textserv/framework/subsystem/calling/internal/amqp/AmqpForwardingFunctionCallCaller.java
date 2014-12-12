package com.textserv.framework.subsystem.calling.internal.amqp;

import com.textserv.framework.subsystem.calling.internal.inprocess.ForwardIfNotFoundFunctionCallCaller;
import com.textserv.framework.subsystem.common.InProcessFunctionCallHanderSingleton;

//an inprocess handler that forwards to Amqp should a impl not be found in process
//worked out nice and simple, quite pleased with that
public class AmqpForwardingFunctionCallCaller extends ForwardIfNotFoundFunctionCallCaller {

	public AmqpForwardingFunctionCallCaller(String uniqueName) {
		super(InProcessFunctionCallHanderSingleton.getInProcessHandler(), new AmqpMessageFunctionCallCaller(uniqueName));
	}
}
