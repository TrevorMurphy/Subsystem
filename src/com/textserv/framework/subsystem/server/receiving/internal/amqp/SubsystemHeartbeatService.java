package com.textserv.framework.subsystem.server.receiving.internal.amqp;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.Subsystem;
import com.textserv.framework.subsystem.receiving.SubsystemImpl;
import com.textserv.messaging.AMQPSimpleService;
import com.textserv.messaging.AmqpMessageListener;

public class SubsystemHeartbeatService extends Thread implements AmqpMessageListener {
	private int heartBeatInterval = 5000;
	private static SubsystemHeartbeatService instance = null;
	private static final Log logger = LogFactory.getLog(SubsystemHeartbeatService.class.getName());
	private static Object instancelock = new Object();
	private String heartbeatTopic = "com.textserv.framework.subsystem.heartbeat";
	
	public SubsystemHeartbeatService() {
		super("SusbsystemHeartbeatService");
		setDaemon(true);
		AMQPSimpleService.getAMQPService().subscribeTopic(heartbeatTopic, this);
	}
	
	public void setHearbeatInterval( int numMillis ) {
		heartBeatInterval = numMillis;
	}
	
	public static SubsystemHeartbeatService getInstance() {
		synchronized(instancelock) {
			if ( instance == null ) {
				instance = new SubsystemHeartbeatService();
				instance.start();
			}
		}
		return instance;
	}
	
	public void run() {
		while(true) {
			try{
				try {
					sleep(heartBeatInterval);
				} catch( InterruptedException e ) {
				}
				DataObject heartbeats = SubsystemImpl.generateHeartbeat();
				if ( heartbeats != null ) {
					logger.debug("SusbsystemHeartbeatService publishing heartbeat");
					AMQPSimpleService.getAMQPService().publish(heartbeatTopic, heartbeats);
				}
			} catch( Exception e ) {
				logger.error("SusbsystemHeartbeatService.run", e);				
			}
		}
	}

	public void onMessage(DataObject message) {
		try {
			if ( message != null && message.getString(com.textserv.framework.subsystem.common.internal.Constants.TopicName).equals(heartbeatTopic) ) {
				logger.debug("Received heartbeat: " + message);
				List<DataObject> subsystemHeartbeats = message.getDataObjectList("Subsystems");
				for ( DataObject theSubsystemHeartbeat : subsystemHeartbeats) {
					String subsystemName = theSubsystemHeartbeat.getString("SubsystemName");
					if (subsystemName != null) {
						Subsystem.getInstance(subsystemName).notifyHeartbeatListener(theSubsystemHeartbeat);
					}
				}				
			}
		} catch ( Exception e ) {
			logger.error("SusbsystemHeartbeatService.onMessage", e);				
		} 
	}
}
