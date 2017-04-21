package model;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import message.MsgFactory;
import message.MsgText;
import network.Network;


public class Model {
	
	/**
	 * The list of all users known by the system since launched
	 */
	private ObservableList<User> knownUsers;
	
	/**
	 * The list of all simple conversations opened by the user
	 */
	private ObservableMap<String, SimpleConversation> simpleConversations;
	
	/**
	 * This is the representation of the person reading this...
	 */
	private User localUser = null;
	private String userName;

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
			knownUsers = FXCollections.observableArrayList();
			simpleConversations=FXCollections.observableHashMap();
	}

	public ObservableList<User> getKnownUsers() {
		return knownUsers;
	}
	
	/**
	 * 
	 * @param fullUserName The full user name on this format : userName_Address
	 * @return The User if it was found | null if not
	 */
	public User getUser(String fullUserName){
		System.out.println("Looking for " + fullUserName);
		for(User usr : knownUsers){
			System.out.println("current : " + usr.getFullUserName());
			if((usr.getFullUserName().equals(fullUserName))){
				System.out.println("User found");
				return usr;
			}
		}
		System.out.println("User not found...");
		return null;
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
	 * Add a User to the knownUser list
	 * @param usr The User to add
	 * @param isConnected The User status
	 * @return 1 if the User has been successfully added | -1 if it was already known by the system
	 */
	public int addUser(User usr, Boolean isConnected){
		
		if(getUser(usr.getFullUserName()) == null){
			System.out.println("Adding " + usr.getFullUserName() + " to known users !");

			 //Somehow this works.... Or does it?

			Platform.runLater(()-> {knownUsers.add(usr);}); //TODO find a workaround

			addSimpleConversation(usr);
			/*System.out.println("List of the known users");
			for(User user : knownUsers){
				System.out.println(user.getFullUserName());
				
			}*/
			return 1;
		}
		else{
			System.out.println("The user " + usr.getUserName().get() + " is already known !");
		}
		return -1;
	}
	
	/**
	 * Set the Status of usr to isConnected
	 * @param usr
	 * @param isConnected
	 * @return 1 if it was successful| -1 if usr is not known
	 */
	public int updateUserStatus(User usr, Boolean isConnected){
		if(getUser(usr.getFullUserName()) != null){
			System.out.println("Updating " + usr.getUserNameString() + " status. isConnected = " +isConnected);
			getUser(usr.getFullUserName()).setIsConnected(isConnected);
		
			return 1;
		}
		else{
			System.out.println("The user " + usr.getUserNameString() + " was not found !");
		}
		return -1;
	}
	
	/**
	 * Remove a User from the knownUsers list
	 * @param usr
	 * @return
	 */
	
	//TODO Why do we have a isConnected bool and we also remove users when not connected ?
	public int removeUser(User usr){
		if(getUser(usr.getFullUserName()) != null){
			System.out.println("Removing " + usr.getUserNameString() + " from known users !");
			knownUsers.remove(usr.getUserNameString());
			return 1;
		}
		else{
			System.out.println("The user " + usr.getUserNameString() + " was not found !");
		}
		return -1;
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

	public int logIn(String userName){

		createLocalUser(userName);


		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Network.getInstance().sendMessage(MsgFactory.createHelloMessage(localUser, Network.getInstance().getBroadcastAddress()));
		return 1;
	}

	public int logOut(){
		System.out.println("Logging out");
		Network.getInstance().sendMessage(MsgFactory.createByeMessage(localUser, Network.getInstance().getBroadcastAddress()));
		return 1;
	}

	public User getLocalUser() {
		return localUser;
	}

	public void setLocalUser(User localUser) {
		this.localUser = localUser;
	}
}
