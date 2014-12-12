package com.textserv.framework.subsystem.calling.internal.callback;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;

public class ThreadedFunctionCallbackHandler implements FunctionCallbackHandler {

	public void handleFuncCallback(String subsystemName, String functionName,FunctionCallback callback, DataObject result) {
		new Thread(new FunctionCallbackHandlerRunner(subsystemName, functionName, callback, result)).start();
	}	
}
