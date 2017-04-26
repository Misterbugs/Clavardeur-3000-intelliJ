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
		//Check if the message is already in the conversation (when ack is lost for exemple)
		if(!containsMessage(message)) {
			this.messageList.add(message);
			notifyObserver(message);
		}
		else{
			System.out.println("Received the same message as before");
		}
		
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

	/**
	 * Checks if a message is already in a conversation
	 * @param mesg
	 * @return true if the message is already in the conversation
	 */

	private boolean containsMessage(Message mesg){

		//Building full username for the message
		String messageUser = User.fullUserName(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()));

		for(Message m : messageList){
			//Building full username for the compared message
			String testUser = User.fullUserName(m.getSourceUserName(), new Address(m.getSourceAddress(), m.getSourcePort()));

			if(m.getNumMessage() == mesg.getNumMessage() && messageUser.equals(testUser)){
				return true;
			}
		}
		return false;
	}
}
