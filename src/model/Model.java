package model;

import java.net.InetAddress;
import java.net.UnknownHostException;

import controller.NetworkHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import message.MsgFactory;
import message.MsgText;
import network.Network;


public class Model {
	

	
	/**
	 * The list of all simple conversations opened by the user
	 */
	private ObservableMap<String, SimpleConversation> simpleConversations;
	
	/**
	 * This is the representation of the person reading this...
	 */
	private User localUser = null;

	/**
	 * A singleton to be sure of unity of the Model instantiation
	 *
	 */
	private static class SingletonHolder{ 
        public static final Model instance = new Model();
    }
	
	/**
	 * Here is how to access the Model from other classes
	 * @return
	 */
	public static Model getInstance() {
        return SingletonHolder.instance;
    }
	
	private Model(){
			//knownUsers = FXCollections.observableArrayList();
			simpleConversations=FXCollections.observableHashMap();
	}


	


	public ObservableMap<String, SimpleConversation> getSimpleConversations() {
		return simpleConversations;
	}


	public int addSimpleConversation(User usr){
		if(simpleConversations.get(usr.getFullUserName())!=null){
			System.out.println("Conversation already exists!");
			return 0;
		}
		else{
			simpleConversations.put(usr.getFullUserName(), new SimpleConversation(usr));
		}
		return 1;

	}

	public SimpleConversation getConversation(String fullName){
		return simpleConversations.get(fullName);
	}



	/**
	 * Your creator, be grateful !
	 * @param name Your userName
	 * @return
	 */
	public User createLocalUser(String name){
		if(localUser == null){
			Address addr = null;
			addr = Network.getInstance().getLocalAddress();

			localUser = new User(name, addr, true) ;
		}
		else{System.out.println("Local user already created !");}
		return localUser;
	}


	public User getLocalUser() {
		return localUser;
	}

	public void setLocalUser(User localUser) {
		this.localUser = localUser;
	}
}
