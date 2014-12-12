package com.textserv.framework.subsystem.calling.internal.callback;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;

public interface FunctionCallbackHandler {
	public void handleFuncCallback(String subsystemName, String functionName, FunctionCallback callback, DataObject result);
}
