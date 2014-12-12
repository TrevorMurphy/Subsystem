package com.textserv.framework.subsystem.receiving.internal;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.DataObjectException;
import com.textserv.framework.subsystem.common.SubsystemException;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.framework.subsystem.common.internal.SubsystemExceptionUtils;

public class InProcessFunctionCallReceiver implements FunctionCallReceiver {

	protected HashMap<String, HashMap<String, FunctionCallImplementator>> registeredImplementators = new HashMap<String, HashMap<String, FunctionCallImplementator>>();
	protected String uniqueName;
	private static final Log logger = LogFactory.getLog(InProcessFunctionCallReceiver.class.getName());
	
	public InProcessFunctionCallReceiver(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	
	public FunctionCallImplementator getImplementator(String subsystemName,	String functionName) {
		FunctionCallImplementator currentImpl = null;
		HashMap<String, FunctionCallImplementator> subsystemImpls = registeredImplementators.get(subsystemName);
		if ( subsystemImpls != null ) {
			currentImpl = subsystemImpls.get(functionName);
		}
		return currentImpl;
	}

	public DataObject handleFuncCall(DataObject message) {
		DataObject functionReturn = new DataObject(Constants.SubsystemFuncCallReturn);
		DataObject subsystemMeta = null;		
		try {
//			logger.debug("InProcessFunctionCallReceiver.handleFuncCall Received message " + message.toXMLEncoded());				
			subsystemMeta = message.getDataObject(Constants.SubsystemMeta);
			if ( subsystemMeta != null ) {
				String subsystemName = subsystemMeta.getString(Constants.SubsystemName);
				String messageName = subsystemMeta.getString(Constants.MessageName);
				DataObject params = message.getDataObject(Constants.Params);
				FunctionCallImplementator currentImpl = getImplementator(subsystemName, messageName);
				if ( currentImpl != null ) {
					subsystemMeta.setBoolean(Constants.FoundImpl, true);				
					subsystemMeta.setString(Constants.ProcessedByName, uniqueName);
					try {
						DataObject result = currentImpl.innerHandleFuncCall(messageName, params);
						if ( result != null ) {
							functionReturn.setDataObject(Constants.Result,result);
						}
					} catch ( Throwable t ) {
						DataObject exception = SubsystemExceptionUtils.exceptionToDataObject(t);
						functionReturn.setDataObject(Constants.Exception,exception); 
					}
				} else {
					subsystemMeta.setBoolean(Constants.FoundImpl, false);
				}			
			} else {
				DataObject exception = SubsystemExceptionUtils.exceptionToDataObject(new SubsystemException("Malfomed message, unable to find subsystem meta data"));
				functionReturn.setDataObject(Constants.Exception,exception); 
		}
		} catch(Throwable e) {
			DataObject exception = SubsystemExceptionUtils.exceptionToDataObject(e);
			try {
				functionReturn.setDataObject(Constants.Exception,exception); 
			} catch( DataObjectException e1 ) {
				logger.debug("InProcessFunctionCallReceiver.handleFuncCall Exception in handling subsystem message",e1 );				
			}
		}finally {
			try {
				functionReturn.setDataObject(Constants.SubsystemMeta, subsystemMeta);
			} catch( DataObjectException e2 ) {
				logger.debug("InProcessFunctionCallReceiver.handleFuncCall Exception in handling subsystem message",e2 );				
			}
		}
		return functionReturn;
	}

	public void register(String subsystemName, String functionName, FunctionCallImplementator impl) {
		HashMap<String, FunctionCallImplementator> subsystemImpls = registeredImplementators.get(subsystemName);
		if ( subsystemImpls == null ) {
			synchronized(registeredImplementators) {
				subsystemImpls = registeredImplementators.get(subsystemName);
				if ( subsystemImpls == null ) {
					subsystemImpls = new HashMap<String, FunctionCallImplementator>();
					registeredImplementators.put(subsystemName, subsystemImpls);
				}
			}
		}
		subsystemImpls.put(functionName, impl);//only one impl per function allowed
	}

	public void unregister(String subsystemName, String functionName,FunctionCallImplementator impl) {
		HashMap<String, FunctionCallImplementator> subsystemImpls = registeredImplementators.get(subsystemName);
		if ( subsystemImpls != null ) {
			FunctionCallImplementator currentImpl = subsystemImpls.get(functionName);
			if (currentImpl != null && currentImpl == impl ) {
				subsystemImpls.remove(functionName);
			}
		}
	}
}
