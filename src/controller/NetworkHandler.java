package controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.application.Platform;
import message.*;
import model.Address;
import model.Model;
import model.User;
import network.Network;

import javax.jws.WebParam;

/**
 * Interracts with the network in order to send and receive messages.
 * 
 * @author Romain
 *
 */

public class NetworkHandler implements INetworkObserver{

	Network net;

	boolean seeLocalUser = true;
	
	
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

		if (Model.getInstance().getLocalUser() != null) {

			if (mesg instanceof MsgText) {

				MsgText txtMesg = (MsgText) mesg;
				System.out.println("Text message : \"" + txtMesg.getTextMessage() + "\""); //DEBUG

				String fullUserName = User.fullUserName(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()));
				System.out.println("Created username : " + fullUserName);

				 Model.getInstance().getSimpleConversations().get(fullUserName).addMessage(txtMesg);


			} else if (mesg instanceof MsgHello) {

				User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);

				if(usr.getFullUserName().equals( Model.getInstance().getLocalUser().getFullUserName())  && !seeLocalUser){
					System.out.println("Received our own hello message => IGNORED");
				}else{
					System.out.println("Hello :) Adding to the UserList");

					Model.getInstance().addUser(usr, true);

					System.out.println("Replying");
					Message replymesg = MsgFactory.createReplyPresence(Model.getInstance().getLocalUser(), usr);
					sendMessage(replymesg);
				}



			}
			else if (mesg instanceof  MsgReplyPresence){
				User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);

				if(usr.getFullUserName().equals( Model.getInstance().getLocalUser().getFullUserName())){
					System.out.println("Received our own ReplyPresence message ");
				}else{
					System.out.println("Someone replied :) Adding to the UserList");

					Model.getInstance().addUser(usr, true);
				}

			}
			else if (mesg instanceof MsgBye) {
				System.out.println("The user has left");
				Model.getInstance().updateUserStatus(Model.getInstance().findUser(mesg.getSourceUserName() + "_" + mesg.getSourceAddress() + ":" + mesg.getDestinationPort()), false);
			}



			else if (mesg instanceof MsgFile) {
				System.out.println("Testing File Received");

			}

		}
	}
	
	public int sendMessage(Message message){
		//TODO add error codes

		System.out.println("Sending message of type " + message.getClass().toString());
		System.out.println("Src : "+ message.getSourceUserName() + " @" + message.getSourceAddress());
		System.out.println("Dest : @" + message.getDestinationAddress());

		net.sendMessage(message);
		return 0;
	}
}
