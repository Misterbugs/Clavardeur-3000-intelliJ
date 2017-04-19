package model;

import java.io.Serializable;
import java.util.ArrayList;

import message.MsgText;

/**
 * Model class for a conversation
 * 
 * @author Florian Clanet
 *
 */
public class Conversation implements Serializable{
	
	/**
	 * The complete list of previous messages TODO change MsgText into Message
	 */
	private ArrayList<MsgText> messageList = new ArrayList<MsgText>();
	
	
	
	public Conversation(){
		messageList = new ArrayList<MsgText>();
	}
	
	public void addMessage(MsgText message){
		//TODO Check the last message not to send 2 time the same thing
		this.messageList.add(message);
		
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

	
	
	

}
