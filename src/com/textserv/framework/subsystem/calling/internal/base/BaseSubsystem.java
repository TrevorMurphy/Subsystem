package com.textserv.framework.subsystem.calling.internal.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.DataObjectException;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.calling.HeartbeatListener;
import com.textserv.framework.subsystem.calling.Subsystem;
import com.textserv.framework.subsystem.calling.internal.callback.FunctionCallbackHandler;
import com.textserv.framework.subsystem.calling.internal.callback.ThreadedFunctionCallbackHandler;
import com.textserv.framework.subsystem.common.SubsystemException;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.framework.subsystem.common.internal.SubsystemExceptionUtils;

public class BaseSubsystem extends Subsystem {

	private BaseFunctionCallCaller functionCallCaller = null;
	private static final Log logger = LogFactory.getLog(BaseSubsystem.class.getName());
	
	private List<HeartbeatListener> heartbeatListeners = new ArrayList<HeartbeatListener>();
	
	protected BaseSubsystem( String name, BaseFunctionCallCaller functionCallCaller) {
		super(name);
		this.functionCallCaller = functionCallCaller;
	}
	
	@Override
	public DataObject callFunc(String functionName, DataObject params) throws SubsystemException {
		logger.debug("BaseSubsystem.callFunc Calling function: " + functionName);
		
		DataObject result = functionCallCaller.callFunc(name, functionName, params);
		if ( result != null ) {
			SubsystemExceptionUtils.throwExceptionIfNeeded(result);
		}
		DataObject actualResult = null;
		try {
			actualResult = (result == null ? null : result.getDataObject(Constants.Result));
		} catch(DataObjectException e ) {
			throw new SubsystemException("Error handling Result");
		}
		return actualResult;
	}

	@Override
	public void callFuncAsync(String functionName, DataObject params,FunctionCallback callback) throws SubsystemException {
		logger.debug("BaseSubsystem.callFunc Calling Async function: " + functionName);
		functionCallCaller.callFuncAsync(name, functionName, params, callback, getCallBackHandler());
	}
	public FunctionCallbackHandler getCallBackHandler() {
		return new ThreadedFunctionCallbackHandler();
	}

	@Override
	public void registerHeartbeatListener(HeartbeatListener listener) throws SubsystemException {
		if ( !heartbeatListeners.contains(listener)) {
			heartbeatListeners.add(listener);
		}
	}

	public void notifyHeartbeatListener(DataObject heartbeat) throws SubsystemException {
		for ( HeartbeatListener listener : heartbeatListeners ) {
			listener.onHeartbeat(heartbeat);
		}
	}
}
