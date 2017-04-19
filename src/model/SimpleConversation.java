package model;

import message.MsgText;


/**
 * This a simple conversation with just one user
 *
 */
public class SimpleConversation extends Conversation{
	
	private User receiver;
	private String id;
	
	public SimpleConversation(){
		super();
		this.id = new String("");
	}
	
	public SimpleConversation(User receiver){
		this.receiver = receiver;
		this.id = new String(receiver.getUserName().getValueSafe() + "_" + receiver.getAddress().sysString());//.getIpAdress().toString());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		String messages = "Messages :\n";
		for(MsgText msg : this.getMessageList()){
			messages += msg.getTextMessage() + "\n";
		}
		return messages;//"SimpleConversation [receiver=" + receiver + "]";
	}

	
	
}
