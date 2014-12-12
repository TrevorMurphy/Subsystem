package com.textserv.framework.subsystem.receiving;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.DataObjectException;
import com.textserv.framework.subsystem.common.SubsystemException;
import com.textserv.framework.subsystem.common.internal.Constants;
import com.textserv.framework.subsystem.receiving.internal.FunctionCallImplementator;
import com.textserv.framework.subsystem.receiving.internal.FunctionCallReceiver;

public abstract class SubsystemImpl implements FunctionCallImplementator {
	
	private String subsystemName;
	private HashMap<String, FunctionCallImplementator> functions = new HashMap<String,FunctionCallImplementator>();
	private static List<SubsystemImpl> allSubsystems = new ArrayList<SubsystemImpl>();
	private static List<FunctionCallReceiver> allReceivers = new ArrayList<FunctionCallReceiver>();
	private static final Log logger = LogFactory.getLog(SubsystemImpl.class.getName());
	private DataObject stats = new DataObject();
	
	public SubsystemImpl( String subsystemName) {
		this.subsystemName = subsystemName;
		try {
			stats.setDate("StartTime", new Date());
			stats.setLong("StartTimeMillis", System.currentTimeMillis());
		} catch ( Exception e ) {
			
		}
		allSubsystems.add(this);
		registerFunction(Constants.fnRegisterObjectListener);
		registerFunction(Constants.fnUnregisterObjectListener);
		logger.info("Subsystem: "+ subsystemName + " loaded");
	}
	
	public static DataObject generateHeartbeat() {
		DataObject heartbeat = null;
		if ( !allSubsystems.isEmpty()) {
			try {
				heartbeat = new DataObject();
				heartbeat.setLong("Time", System.currentTimeMillis());
				java.net.InetAddress thisMachine = java.net.InetAddress.getLocalHost(); 
				heartbeat.setString("HostName", thisMachine.getHostName());
				heartbeat.setString("IPAddress", thisMachine.getHostAddress());
				for ( SubsystemImpl subsystemImpl : allSubsystems ) {
					heartbeat.getDataObjectList("Subsystems", true).add(subsystemImpl.internalHeartbeat());
				}
			} catch ( Exception e ) {
				logger.error("SubsystemImpl.generateHeartbeat", e);
			}
		}
		return heartbeat;
	}

	public DataObject internalHeartbeat() throws DataObjectException {
		DataObject internalHeartbeat = new DataObject();
		internalHeartbeat.setString(Constants.SubsystemName, subsystemName);
		List<String> functionNames = new ArrayList<String>();
		functionNames.addAll(functions.keySet());
		internalHeartbeat.setStringList(Constants.Functions, functionNames);
		DataObject statsCopy = null;		
		synchronized(stats) {
			statsCopy = stats.createCopy();
		}
		internalHeartbeat.setDataObject("Stats", statsCopy);		
		long startMillis = statsCopy.getLong("StartTimeMillis");
		long duration = System.currentTimeMillis() - startMillis;
		long durationSecs = duration / 1000;
		long durationMins = durationSecs / 60;
		long durationHours =  durationMins / 60;
		long durationDays =  durationHours / 24;
		long days =  durationDays % 365;
		long hours = durationHours % 24;
		long mins = durationMins % 60;
		long secs = durationSecs % 60;
		statsCopy.setString("Uptime", days + " days:" + hours + " hours:" + mins + " mins:" + secs + " secs"); 
		return internalHeartbeat;
	}
	
	public static String describeAllSubsystems() {
		String theSubsystems = new String("Subsystem description for process at: ");
		try {
			java.net.InetAddress thisMachine = java.net.InetAddress.getLocalHost(); 
			theSubsystems += " HostName: " + thisMachine.getHostName() + " IP: " + thisMachine.getHostAddress() + System.getProperty("line.separator");
		} catch ( Exception e ) {
			
		}
		for ( SubsystemImpl theSubsystem : allSubsystems ) {			
			theSubsystems += theSubsystem.describe();
			theSubsystems += System.getProperty("line.separator"); 
		}
		return theSubsystems;
	}

	public String getName() {
		return subsystemName;
	}
	
	public static void bindAll(FunctionCallReceiver functionCallReceiver) {
		allReceivers.add(functionCallReceiver);
		for ( Iterator<SubsystemImpl> i = allSubsystems.iterator(); i.hasNext(); ) {
			SubsystemImpl impl = i.next();
			impl.bind(functionCallReceiver, false);
		}
	}
	
	public void bind(FunctionCallReceiver functionCallReceiver) {
		bind(functionCallReceiver, true);
	}

	public void bind(FunctionCallReceiver functionCallReceiver, boolean addReceiver) {
		if ( addReceiver ) {
			allReceivers.add(functionCallReceiver);
		}
		for ( Iterator<String> i = functions.keySet().iterator(); i.hasNext(); ) {
			String functionName = i.next();
			functionCallReceiver.register(subsystemName, functionName, functions.get(functionName));
		}
	}
	
	public void registerFunction( String functionName ) {
		functions.put(functionName, this);
		for ( Iterator<FunctionCallReceiver> i = allReceivers.iterator(); i.hasNext(); ) {
			FunctionCallReceiver receiver = i.next();
			receiver.register(subsystemName, functionName, functions.get(functionName));
		}
		logger.info("Subsystem: "+ subsystemName + " function registered: " + functionName);		
	}
	
	public String describe() {
		String subystemDescription = new String("Subsystem with name: " + getName() +" has the following registered functions" + System.getProperty("line.separator"));
		for ( String functionName : functions.keySet()) {
			subystemDescription += describeFuncCall(functionName);
		}
		return subystemDescription;
	}
	public String describeFuncCall(String functionName) {
		//simple impl to describe a func call
		return subsystemName + "." + functionName;
	}

	public DataObject innerHandleFuncCall(String functionName, DataObject params) throws Exception {		
		logger.debug("Subsystem: "+ subsystemName + " handling functioncall: " + functionName + " with params: " + params);
		long timeStart = System.currentTimeMillis();
		try {		
			if ( functionName.equals(Constants.fnRegisterObjectListener) ) {
				String changeListenerId = params.getString(Constants.ChangeListenerId);
				DataObject changeListenerParams = params.getDataObject(Constants.ChangeListenerParams);
				String objectTypeName = params.getString(Constants.ObjectTypeName);
				return registerObjectChangeListener(objectTypeName, changeListenerId, changeListenerParams);
			} else if ( functionName.equals(Constants.fnUnregisterObjectListener)) {
				String changeListenerId = params.getString(Constants.ChangeListenerId);
				String objectTypeName = params.getString(Constants.ObjectTypeName);
				unRegisterObjectChangeListener(objectTypeName, changeListenerId);
				return null;
			} else {			 
				return handleFuncCall(functionName, params);
			}
		} finally {
			long timeEnd = System.currentTimeMillis();
			trackStats(functionName, timeStart, timeEnd);
		}
	}
	
	private void trackStats(String functionName, long timeStart, long timeEnd) {
		try {
			synchronized(stats) {
				long callDuration = timeEnd - timeStart;	
				incrementTotalCalls(stats);
				incrementAvgTime(stats, callDuration);
				trackLastCallTime(stats, timeStart);
				DataObject theFunctionStats = stats.getDataObject(functionName);
				if ( theFunctionStats == null ) {
					theFunctionStats = new DataObject();
					stats.setDataObject(functionName, theFunctionStats);
				}
				incrementTotalCalls(theFunctionStats);
				incrementAvgTime(theFunctionStats, callDuration);
				trackLastCallTime(theFunctionStats, timeStart);			
			}
		} catch ( Exception e ) {
			logger.error(e);
		}				
	}

	private void trackLastCallTime(DataObject theStats, long startTime) throws DataObjectException {
		theStats.setDate("LastCallTime", new Date(startTime) );		
	}

	private void incrementAvgTime(DataObject theStats, long callDuration ) throws DataObjectException {
		theStats.setLong("LastCall", callDuration);
		if ( theStats.itemExists("LastCallTrackNumber")) {
			long num = theStats.getLong("LastCallTrackNumber");
			theStats.setLong("LastCallTrackNumber", ++num);

			if ( theStats.itemExists("AvgLast10Calls")) {
				long totalTime = theStats.getLong("TotalLast10Time");
				totalTime += callDuration;
				theStats.setLong("TotalLast10Time", totalTime);
				long totalCalls = theStats.getLong("TotalLast10Calls") + 1 ;
				theStats.setLong("TotalLast10Calls", totalCalls);
				theStats.setDouble("AvgLast10Calls", totalTime / totalCalls); 				
				if ( num >= 10 ) {
					theStats.remove("LastCallTrackNumber");
				}
			} else {
				theStats.setDouble("AvgLast10Calls", callDuration);
				theStats.setLong("TotalLast10Time", callDuration);
				theStats.setLong("TotalLast10Calls", 1);
			}		
		} else {
			theStats.setDouble("AvgLast10Calls", callDuration);
			theStats.setLong("TotalLast10Time", callDuration);
			theStats.setLong("LastCallTrackNumber", 1);
			theStats.setLong("TotalLast10Calls", 1);				
		}
		if ( theStats.itemExists("AvgTime")) {
			long totalTime = theStats.getLong("TotalTime");
			totalTime += callDuration;
			theStats.setLong("TotalTime", totalTime);
			long totalCalls = theStats.getLong("TotalCalls");
			theStats.setDouble("AvgTime", totalTime / totalCalls); 
		} else {
			theStats.setDouble("AvgTime", callDuration);
			theStats.setLong("TotalTime", callDuration);
		}		
	}

	private void incrementTotalCalls(DataObject theStats) throws DataObjectException {
		if ( theStats.itemExists("TotalCalls")) {
			theStats.setLong("TotalCalls", theStats.getLong("TotalCalls") + 1);				
		} else {
			theStats.setLong("TotalCalls", 1);
		}		
	}
	
	public abstract DataObject handleFuncCall(String functionName, DataObject params) throws Exception;
	
	public DataObject registerObjectChangeListener(String objectTypeName, String objectListenerId, DataObject params) throws Exception {
		//essentially a no op
		throw new SubsystemException("registerObjectChangeListener Not supported for subsystem: " + subsystemName);
	}

	public void unRegisterObjectChangeListener(String objectTypeName, String objectListenerId) {
		//a no op here
	}
}
