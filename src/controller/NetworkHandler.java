package controller;

import message.Message;
import message.MsgAck;
import message.MsgBye;
import message.MsgFile;
import message.MsgHello;
import message.MsgText;
import model.Address;
import model.Model;
import model.User;
import network.Network;

/**
 * Interracts with the network in order to send and receive messages.
 * 
 * @author Romain
 *
 */

public class NetworkHandler implements INetworkObserver{

	Network net;
	
	
	public NetworkHandler(Network net) {
		this.net = net;
		this.net.register(this); //Registering to the network
	}
	
	
	/**
	 * Method called by the Network when a message is received
	 * Handles how the message is treated depending on its type.
	 */
	@Override
	public void update(Message mesg) {
		System.out.println("HANDLER : A message is received from " + mesg.getSourceUserName() + " of type " + mesg.getClass());
		
		if(mesg instanceof MsgText){
			System.out.println("Text message : \"" + ((MsgText)mesg).getTextMessage() + "\""); //DEBUG
		}
		else if(mesg instanceof MsgHello){
			System.out.println("Hello :) Adding to the UserList");
			User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);
			Model.getInstance().addUser(usr, true);
		}
		else if(mesg instanceof MsgBye){
			System.out.println("The user has left");
			Model.getInstance().updateUserStatus(Model.getInstance().findUser(mesg.getSourceUserName() + "_" + mesg.getSourceAddress() + ":" + mesg.getDestinationPort()), false);
		}
		else if(mesg instanceof MsgFile){
			System.out.println("Testing File Received");
			
		}
	}

	
	public int sendMessage(Message message){
		//TODO add error codes
		net.sendMessage(message);
		return 0;
	}
}
