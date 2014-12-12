package com.textserv.framework.subsystem.calling.internal;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.calling.internal.callback.FunctionCallbackHandler;
import com.textserv.framework.subsystem.common.SubsystemException;

public interface FunctionCallCaller {
	public DataObject callFunc( String SubsystemName, String functionName, DataObject params) throws SubsystemException;
	public void callFuncAsync( String SubsystemName, String functionName, DataObject params, FunctionCallback callBack, FunctionCallbackHandler callbackHandler) throws SubsystemException;
}
