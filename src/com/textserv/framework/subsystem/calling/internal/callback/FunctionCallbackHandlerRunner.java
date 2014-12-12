package com.textserv.framework.subsystem.calling.internal.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.DataObjectException;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.framework.subsystem.common.internal.SubsystemExceptionUtils;

public class FunctionCallbackHandlerRunner implements Runnable {
	protected String subsystemName;
	protected String functionName;
	protected FunctionCallback callBack;
	protected DataObject results;
	private static final Log logger = LogFactory.getLog(FunctionCallbackHandlerRunner.class.getName());
	
	public FunctionCallbackHandlerRunner( String subsystemName, String functionName, FunctionCallback callback, DataObject results) {
		this.subsystemName = subsystemName;
		this.functionName = functionName;
		this.callBack = callback;
		this.results = results;
		logger.debug("FunctionCallbackHandlerRunner.FunctionCallbackHandlerRunner"+ subsystemName);		
	}

	public void run() {
		if ( results != null ) {
			DataObject exception = SubsystemExceptionUtils.getException(results);
			if ( exception != null ) {
				callBack.handleFunctionException(subsystemName, functionName, SubsystemExceptionUtils.dataObjectToException(exception));
			} else {
				DataObject actualResult = null;
				try {
					actualResult = (results == null ? null : results.getDataObject(Constants.Result));
				} catch(DataObjectException e ) {
				}				
				callBack.handleFunctionResult(subsystemName, functionName, actualResult);
			}
		}
	}
}
