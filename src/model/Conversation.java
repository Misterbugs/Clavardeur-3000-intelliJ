package model;

import java.io.Serializable;
import java.util.ArrayList;

import message.Message;
import message.MsgText;

/**
 * Model class for a conversation
 * 
 * @author Florian Clanet
 *
 */
public class Conversation implements Serializable, IConversationSubject{
	
	/**
	 * The complete list of previous messages TODO change MsgText into Message
	 */
	private ArrayList<MsgText> messageList = new ArrayList<MsgText>();

	private ArrayList<IConversationObserver> observers = new ArrayList<IConversationObserver>();

	
	public Conversation(){
		messageList = new ArrayList<MsgText>();
	}
	
	public void addMessage(MsgText message){
		//TODO Check the last message not to send 2 time the same thing
		this.messageList.add(message);
		notifyObserver(message);
		
	}

	public ArrayList<MsgText> getMessageList() {
		return messageList;
	}

	public void setMessageList(ArrayList<MsgText> messageList) {
		this.messageList = messageList;
	}

	@Override
	public String toString() {
		String messages = "";
				
				
		return "Conversation [messageList=" + messageList.toString() + "]";
	}


	@Override
	public void register(IConversationObserver o) {

		if(!observers.contains(o)){
			observers.add(o);
		}
	}

	@Override
	public void unregister(IConversationObserver o) {
		observers.remove(o);
	}

	@Override
	public void notifyObserver(Message mesg) {
		for(IConversationObserver o : observers){
			o.update(mesg);
		}
	}
}
