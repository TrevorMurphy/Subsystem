package com.textserv.framework.subsystem.calling.internal.async;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.FunctionCallback;
import com.textserv.framework.subsystem.calling.internal.base.BaseFunctionCallCaller;
import com.textserv.framework.subsystem.calling.internal.callback.FunctionCallbackHandler;
import com.textserv.framework.subsystem.common.SubsystemException;
import com.textserv.framework.subsystem.common.SubsystemTimeoutException;


public class AsyncMessageFunctionCallCaller extends BaseFunctionCallCaller implements SubsystemAsyncMessagerListener {

	protected String uniqueName = null;
	protected HashMap waitingThreads = null;
	protected static final long defaultSynchronousWaitTimeout = 3000 * 10;// 30
	// seconds
	protected static long initialWaitTimeout = 1000;// 1 sec
	protected long messageID = 0;
	protected SubsystemAsyncMessager asyncMessager = null;
	private static final Log logger = LogFactory.getLog(AsyncMessageFunctionCallCaller.class.getName());

	public AsyncMessageFunctionCallCaller(String uniqueName, SubsystemAsyncMessager asyncMessager) {
		waitingThreads = new HashMap();
		this.asyncMessager = asyncMessager;
		this.uniqueName = uniqueName;
		this.asyncMessager.registerListener(uniqueName, this);
	}

	private synchronized long getNextMessageID() {
		return messageID++;
	}

	public DataObject doCall(DataObject subsystemMessage, boolean synchronous, FunctionCallback callback, FunctionCallbackHandler callbackHandler) throws SubsystemException {
		// if a synchronous message then blocks calling thread until
		// response is received
		DataObject returnMessage = null;
		 
		try {
			Long myMessageID = new Long(getNextMessageID());
			trackCurrentThreadOrCallBack(myMessageID, synchronous, callback, callbackHandler);
			DataObject meta = subsystemMessage.getDataObject(com.textserv.framework.subsystem.common.internal.Constants.SubsystemMeta);
			meta.setString(com.textserv.framework.subsystem.calling.internal.async.Constants.AsyncSenderUniqueName, uniqueName);
			meta.setLong(com.textserv.framework.subsystem.calling.internal.async.Constants.AysncMessageID, myMessageID);
			logger.debug("AsyncMessageFunctionCallCaller.doCall sending message " + subsystemMessage.toStringEncoded(true));
		
			asyncMessager.sendMessage(uniqueName, subsystemMessage);
			if (synchronous) {
				long synchronousWaitTimeout = defaultSynchronousWaitTimeout;
					DataObject params = subsystemMessage.getDataObject(com.textserv.framework.subsystem.common.internal.Constants.Params);
					if ( params != null ) {
						if ( params.itemExists(com.textserv.framework.subsystem.common.internal.Constants.FuncTimeOut)) {
							synchronousWaitTimeout = params.getLong(com.textserv.framework.subsystem.common.internal.Constants.FuncTimeOut);
						}
					}
					returnMessage = makeCurrentThreadWaitIfNeeded(myMessageID,synchronousWaitTimeout);
			}
		} catch( Exception e ) {
			logger.error("AsyncMessageFunctionCallCaller.doCall Exception in doCall", e);
			if ( e instanceof SubsystemException ) {
				throw (SubsystemException)e;
			} else {
				throw new SubsystemException("Exception in doCall", e);
			}
		}
		return returnMessage;
	}

	@SuppressWarnings("unchecked")
	private void trackCurrentThreadOrCallBack(Long messageID, boolean synchronous, FunctionCallback callback,FunctionCallbackHandler callbackHandler) {
		synchronized (waitingThreads) {
			HashMap tuple = new HashMap();
			if (synchronous) {
				tuple.put(com.textserv.framework.subsystem.common.internal.Constants.Synchronous, true);
				tuple.put("Thread", Thread.currentThread());
				waitingThreads.put(messageID, tuple);
			} else {
				if (callback != null) {
					tuple.put(com.textserv.framework.subsystem.common.internal.Constants.Synchronous, false);
					tuple.put("Callback", callback);
					tuple.put("CallbackHandler", callbackHandler);
					waitingThreads.put(messageID, tuple);
				}
			}
		}
	}

	private DataObject getReturnMessage(Long messageID) throws Exception {
		DataObject returnMessage = null;
		synchronized (waitingThreads) {
			Map returnTuple = (HashMap) waitingThreads.get(messageID);
			if (returnTuple != null) {
				returnMessage = (DataObject) returnTuple.get(com.textserv.framework.subsystem.common.internal.Constants.ReturnMessage);
				if (returnMessage != null) {// got return message
					waitingThreads.remove(messageID);
				}
			}
		}
		return returnMessage;
	}

	private DataObject makeCurrentThreadWaitIfNeeded(Long messageID,
			long synchronousWaitTimeout) throws Exception {
		boolean mustWait = true;
		long currentTime = System.currentTimeMillis();
		boolean timedOut = false;
		long timeToWait = initialWaitTimeout;// wait for a short period
		// at first to prevent race
		// condition causing undue
		// delay

		DataObject returnMessage = getReturnMessage(messageID);
		mustWait = returnMessage == null;

		while (mustWait) {
			synchronized (Thread.currentThread()) {
				try {
					if (mustWait) {
						Thread.currentThread().wait(timeToWait);
					}
				} catch (InterruptedException ie) {
				}
				timeToWait = synchronousWaitTimeout;
				long currentTimeAfterWait = System.currentTimeMillis();
				if (currentTimeAfterWait >= (currentTime + synchronousWaitTimeout)) {
					timedOut = true;
				}
				returnMessage = getReturnMessage(messageID);
				if (returnMessage != null) {
					mustWait = false;
				} else if (timedOut) {
					// timed out and did not receive ack
					mustWait = false;
					throw new SubsystemTimeoutException();
				}
			}
		}
		return returnMessage;
	}

	protected void handleTheCallback( DataObject message, HashMap tuple, Long messagID ) {
		try {
			waitingThreads.remove(messageID);
			DataObject subsystemMeta = message.getDataObject(com.textserv.framework.subsystem.common.internal.Constants.SubsystemMeta);
			String subsystemName = subsystemMeta.getString(com.textserv.framework.subsystem.common.internal.Constants.SubsystemName);
			String messageName = subsystemMeta.getString(com.textserv.framework.subsystem.common.internal.Constants.MessageName);
			FunctionCallback callback = (FunctionCallback) tuple.get("Callback");
			FunctionCallbackHandler callbackHandler = (FunctionCallbackHandler) tuple.get("CallbackHandler");
			if (callback != null && callbackHandler != null) {
				callbackHandler.handleFuncCallback(subsystemName, messageName, callback, message);
			} else {
				logger.debug("AsyncMessageFunctionCallCaller.handleTheCallback"+new StringBuffer("Unable to find asyncronous callback for response ").append(message).toString());
			}
		} catch (Exception e) {
			logger.error("AsyncMessageFunctionCallCaller.handleTheCallback Exception thrown", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onAsyncMessage(DataObject message) {
		try {
			Thread waitingThread = null;
			DataObject asyncMeta = message.getDataObject(com.textserv.framework.subsystem.common.internal.Constants.SubsystemMeta);
			Long messageID = asyncMeta.getLong(com.textserv.framework.subsystem.calling.internal.async.Constants.AysncMessageID);
			if (messageID != null) {
				HashMap tuple = null;
				synchronized (waitingThreads) {
					tuple = (HashMap) waitingThreads.get(messageID);
					if (tuple != null) {
						tuple.put(com.textserv.framework.subsystem.common.internal.Constants.ReturnMessage, message);
						if (((Boolean) tuple.get(com.textserv.framework.subsystem.common.internal.Constants.Synchronous)).booleanValue()) {
							waitingThread = (Thread) tuple.get("Thread");
						} else {
							handleTheCallback(message, tuple, messageID);
							return;
						}
					} else {
						logger.debug("AsyncMessageFunctionCallCaller.onAsyncMessage"+new StringBuffer("Unable to find waiting tuple for response ").append(message).toString());
					}
				}
			} else {
				logger.error("AsyncMessageFunctionCallCaller.onAsyncMessage"+ new StringBuffer("correlation message ID is NULL for response ").append(message).toString());
			}
			if (waitingThread != null) {
				synchronized (waitingThread) {
					waitingThread.notifyAll();
				}
			} else {
				logger.debug("AsyncMessageFunctionCallCaller.onAsyncMessage"+ new StringBuffer("Unable to find Thread for response ").append(message).toString());
			}
		} catch (Exception e) {
			logger.error("AsyncMessageFunctionCallCaller.onAsyncMessage Exception thrown", e);
		}
	}
}
