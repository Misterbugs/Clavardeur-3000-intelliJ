package model;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a User
 * 
 * @author Florian Clanet
 *
 */
public class User implements Serializable{
	
	/**
	 * The user address
	 */
	private Address address;
	
	
	
	/**
	 * The userName is a StringProperty(JavaFx) : we get updated when modified
	 */
	private StringProperty userName;
	
	/**
	 * true : user connected
	 * false : user disconnected
	 */
	private Boolean isConnected;
	
	
	/**
	 * A basic User constructor
	 * @param userName
	 */
	public User(String userName, Address address, Boolean isConected){
		this.userName = new SimpleStringProperty(userName);
		this.address = address;
		this.setIsConnected(isConected);
		
		//System.out.println(address.sysString());
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public StringProperty getUserName() {
		return userName;
	}

	/**
	 * A setter to set the userName from a String
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName.set(userName);
	}

	
	/**
	 * A setter to set the userName from a StringProperty
	 * @param userName
	 */
	public void setUserName(StringProperty userName) {
		this.userName = userName;
	}

	public Boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(Boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	/**
	 * 
	 * @return The full user name id on this format : userName_Address
	 */
	public String getFullUserName(){
		return getUserNameString() + "_" + address.getStringPropertyAddress().getValueSafe().toString();
	}
	/**
	 * Get userName (without address)
	 * (convert from StringProperty)
	 * @return
	 */
	public String getUserNameString(){
		return userName.getValueSafe().toString();
	}
	

}
