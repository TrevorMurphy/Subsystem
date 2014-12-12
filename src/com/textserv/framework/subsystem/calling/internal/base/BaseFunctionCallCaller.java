package com.textserv.framework.subsystem.calling.internal.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.calling.internal.FunctionCallCaller;
import com.textserv.framework.subsystem.calling.internal.callback.FunctionCallbackHandler;
import com.textserv.framework.subsystem.common.SubsystemException;
import com.textserv.framework.subsystem.common.internal.Constants;

public abstract class BaseFunctionCallCaller implements FunctionCallCaller {
	private static final Log logger = LogFactory.getLog(BaseFunctionCallCaller.class.getName());

	protected DataObject createSubsystemMessage( String subsystemName, String functionName, DataObject params, boolean synchronous ) throws SubsystemException {
		DataObject subsystemMessage = null;
		try {
			subsystemMessage = new DataObject(Constants.SubsystemFuncCall);
			DataObject subsystemMeta = new DataObject();
			subsystemMeta.setString(Constants.SubsystemName, subsystemName);
			subsystemMeta.setString(Constants.MessageName, functionName);
			subsystemMeta.setBoolean(Constants.Synchronous, synchronous);
			subsystemMessage.setDataObject(Constants.SubsystemMeta,subsystemMeta); 
			subsystemMessage.setDataObject(Constants.Params,params);
		} catch ( Exception e ) {
			logger.error("BaseSubsystem.createSubsystemMessage Exception creating subsystem message", e);
			throw new SubsystemException("Exception creating subsystem message");
		}
		return subsystemMessage;
	}
	
	public DataObject callFunc(String subsystemName, String functionName, DataObject params)  throws SubsystemException{
		return doCall(createSubsystemMessage(subsystemName, functionName, params, true), true, null, null);
	}

	public void callFuncAsync(String subsystemName, String functionName,DataObject params, FunctionCallback callBack, FunctionCallbackHandler callbackHandler) throws SubsystemException {
		doCall(createSubsystemMessage(subsystemName, functionName, params, false), false, callBack, callbackHandler);
	}
	
	public abstract DataObject doCall( DataObject subsystemMessage, boolean synchronous, FunctionCallback callBack, FunctionCallbackHandler callbackHandler) throws SubsystemException;
}
