package com.textserv.framework.subsystem.calling;

import com.textserv.framework.DataObject;

public interface FunctionCallback {
	public void handleFunctionResult( String subsystemName, String functionName, DataObject result);
	public void handleFunctionException( String subsystemName, String functionName, Exception exception);
}
