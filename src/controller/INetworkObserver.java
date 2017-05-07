package controller;

import message.Message;

/**
 * Interface used for the Observer pattern.
 * This is the interface used by the observers of the Network
 */

public interface INetworkObserver {
	
	public void update(Message mesg);
}
