package com.textserv.framework.subsystem.calling;

import com.textserv.framework.DataObject;

public interface HeartbeatListener {
	public void onHeartbeat(DataObject heartbeat);
}
