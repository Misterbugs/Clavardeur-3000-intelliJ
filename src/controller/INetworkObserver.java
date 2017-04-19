package controller;

import message.Message;

public interface INetworkObserver {
	
	public void update(Message mesg);
}
