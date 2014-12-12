package com.textserv.framework.subsystem.test;

import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.Subsystem;
import com.textserv.framework.subsystem.server.ServerSubsystemService;



public class SubsystemHostingTest {
    private static final Logger LOG = Logger.getLogger(SubsystemHostingTest.class);

	public static void  main(String[] args) {
		BasicConfigurator.configure();
    	Properties prop = new Properties();
       	try {
            //load a properties file
//       		prop.load(new FileInputStream("fastserver.properties"));
    		String amqpAddress  = prop.getProperty("amqp_addresses", "127.0.0.1:5672");
    		String amqp_username = prop.getProperty("amqp_username", "guest");
    		String amqp_password = prop.getProperty("amqp_password", "guest");
    		ServerSubsystemService.initialize("SubsystemRemoteTest", amqpAddress, amqp_username, amqp_password, "/");
    		
    		new SimpleSubsystemImpl();
    		new AnotherSimpleSubystemImpl();

       	} catch (Exception ex) {
       		LOG.error(ex);
       	}

	 }    
}
