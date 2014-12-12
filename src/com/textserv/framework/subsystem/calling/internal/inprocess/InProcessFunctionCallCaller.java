package com.textserv.framework.subsystem.calling.internal.inprocess;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.calling.internal.base.BaseFunctionCallCaller;
import com.textserv.framework.subsystem.calling.internal.callback.FunctionCallbackHandler;
import com.textserv.framework.subsystem.common.FunctionCallHandler;
import com.textserv.framework.subsystem.common.SubsystemException;

public class InProcessFunctionCallCaller extends BaseFunctionCallCaller {

	protected FunctionCallHandler fuctionCallHandler = null;
	
	public InProcessFunctionCallCaller( FunctionCallHandler fuctionCallHandler ) {
		this.fuctionCallHandler = fuctionCallHandler;
	}
	
	public DataObject doCall(DataObject subsystemMessage, boolean synchronous, FunctionCallback callBack,FunctionCallbackHandler callbackHandler) throws SubsystemException {
		return fuctionCallHandler.handleFuncCall(subsystemMessage);
	}	
}
