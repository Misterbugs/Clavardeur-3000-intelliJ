package model;

import java.util.ArrayList;

public class GroupConversation extends Conversation{
	
	private ArrayList<User> userList;
	
	public GroupConversation(){
		super();
		userList = new ArrayList<User>();
	}
	
	
	
	
	public ArrayList<User> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}

}
