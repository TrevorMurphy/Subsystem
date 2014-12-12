package com.textserv.framework.subsystem.calling.internal.async;

import com.textserv.framework.DataObject;

public interface SubsystemAsyncMessagerListener {
	public void onAsyncMessage( DataObject message);
}
