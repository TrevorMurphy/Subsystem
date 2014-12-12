package com.textserv.framework.subsystem.receiving.internal;

import com.textserv.framework.subsystem.common.FunctionCallHandler;

public interface FunctionCallReceiver extends FunctionCallHandler {
	public void register(String subsystemName, String functionName, FunctionCallImplementator impl);
	
	public void unregister(String subsystemName, String functionName, FunctionCallImplementator impl);

	public FunctionCallImplementator getImplementator( String subsystemName, String functionName);
}
