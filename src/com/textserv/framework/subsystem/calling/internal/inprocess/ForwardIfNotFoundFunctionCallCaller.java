package com.textserv.framework.subsystem.calling.internal.inprocess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.calling.internal.base.BaseFunctionCallCaller;
import com.textserv.framework.subsystem.calling.internal.callback.FunctionCallbackHandler;
import com.textserv.framework.subsystem.common.FunctionCallHandler;
import com.textserv.framework.subsystem.common.SubsystemException;
import com.textserv.framework.subsystem.common.internal.Constants;

public class ForwardIfNotFoundFunctionCallCaller extends InProcessFunctionCallCaller {

	protected BaseFunctionCallCaller forwardingFunctionCallCaller = null;
	private static final Log logger = LogFactory.getLog(ForwardIfNotFoundFunctionCallCaller.class.getName());
	
	public BaseFunctionCallCaller getForwardingFunctionCallCaller() {
		return forwardingFunctionCallCaller;
	}

	public ForwardIfNotFoundFunctionCallCaller(FunctionCallHandler fuctionCallHandler, BaseFunctionCallCaller forwardingFunctionCallCaller) {
		super(fuctionCallHandler);
		this.forwardingFunctionCallCaller = forwardingFunctionCallCaller;
	}

	public DataObject doCall(DataObject subsystemMessage,boolean synchronous, FunctionCallback callBack,FunctionCallbackHandler callbackHandler) throws SubsystemException {
		DataObject funcResult = super.doCall(subsystemMessage, synchronous, callBack, callbackHandler);
		if ( funcResult != null ) {
			try {
				DataObject subsystemMeta = funcResult.getDataObject(Constants.SubsystemMeta);
				boolean foundImpl = subsystemMeta.getBoolean(Constants.FoundImpl);
				if ( !foundImpl ) {
					funcResult = forwardingFunctionCallCaller.doCall(subsystemMessage, synchronous, callBack, callbackHandler);
				}
			} catch(Exception e ) {
				logger.error("ForwardIfNotFoundFunctionCallCaller.doCall Exception thrown", e);
				if ( e instanceof SubsystemException ) {
					throw (SubsystemException)e;
				} else {
					throw new SubsystemException("Exception in doCall", e);
				}
			}
		}
		return funcResult;
	}
}
