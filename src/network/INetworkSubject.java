package network;

import controller.INetworkObserver;
import message.Message;

public interface INetworkSubject {
	
	public void register(INetworkObserver o);
	public void unregister(INetworkObserver o);
	public void notifyObserver(Message mesg);
}
