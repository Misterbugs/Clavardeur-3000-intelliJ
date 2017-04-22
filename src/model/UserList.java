package model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by Romain on 22/04/2017.
 */
public class UserList implements IUserListSubject{
    /**
     * The list of all users known by the system since launched
     */
    private ArrayList<User> knownUsers;

    private ArrayList<IUserListObserver> observers;


    Model model = Model.getInstance();

    @Override
    public void register(IUserListObserver o) {
        observers.add(o);
    }

    @Override
    public void unregister(IUserListObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObserver(User usr) {
        System.out.println("Notifying");
            for (IUserListObserver o : observers) {
                o.update(usr);
            }

    }

    /**
     * A singleton to be sure of unity of the Model instantiation
     *
     */
    private static class SingletonHolder{
        public static final UserList instance = new UserList();
    }

    /**
     * Here is how to access the Model from other classes
     * @return
     */
    public static UserList getInstance() {
        return SingletonHolder.instance;
    }

    private UserList(){
        //knownUsers = FXCollections.observableArrayList();
        //simpleConversations=FXCollections.observableHashMap();
        observers = new ArrayList<>();
        knownUsers = new ArrayList<>();
        model = Model.getInstance();
    }



    /**
     * Add a User to the knownUser list
     * @param usr The User to add
     * @return 1 if the User has been successfully added | -1 if it was already known by the system
     */
    public int addUser(User usr){

        if(getUser(usr.getFullUserName()) == null){
            System.out.println("Adding " + usr.getFullUserName() + " to known users !");

            //Somehow this works.... Or does it?

            knownUsers.add(usr);

            model.addSimpleConversation(usr);
			/*System.out.println("List of the known users");
			for(User user : knownUsers){
				System.out.println(user.getFullUserName());

			}*/

			notifyObserver(usr);
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

    public ArrayList<User> getKnownUsers() {
        return knownUsers;
    }
}
