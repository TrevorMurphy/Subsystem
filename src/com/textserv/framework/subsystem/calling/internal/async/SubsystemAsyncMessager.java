package com.textserv.framework.subsystem.calling.internal.async;

import com.textserv.framework.DataObject;

public interface SubsystemAsyncMessager {
	public void sendMessage(String uniqueName, DataObject message);
	public void registerListener(String uniqueName, SubsystemAsyncMessagerListener listener);

}
