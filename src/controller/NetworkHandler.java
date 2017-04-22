package controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.xml.internal.bind.v2.runtime.property.StructureLoaderBuilder;
import javafx.application.Platform;
import message.*;
import model.Address;
import model.Model;
import model.User;
import model.UserList;
import network.Network;

import javax.jws.WebParam;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Interracts with the network in order to send and receive messages.
 * 
 * @author Romain
 *
 */

public class NetworkHandler implements INetworkObserver{

	Network net;

	boolean seeLocalUser = true;

	private ConcurrentHashMap<Integer, Boolean> waitingForAck; // contains messages that wait ACK
	
	public NetworkHandler(Network net) {
		this.net = net;
		this.net.register(this); //Registering to the network
		this.waitingForAck = new ConcurrentHashMap<>();
	}
	
	
	/**
	 * Method called by the Network when a message is received
	 * Handles how the message is treated depending on its type.
	 */
	@Override
	public void update(Message mesg) {
		System.out.println();
		System.out.println("HANDLER : A message is received from " + mesg.getSourceUserName() + " of type " + mesg.getClass());

		if (Model.getInstance().getLocalUser() != null) {

			if (mesg instanceof MsgText) {

				MsgText txtMesg = (MsgText) mesg;
				System.out.println("Text message : \"" + txtMesg.getTextMessage() + "\""); //DEBUG

				String fullUserName = User.fullUserName(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()));
				//System.out.println("Created username : " + fullUserName);

				Model.getInstance().getSimpleConversations().get(fullUserName).addMessage(txtMesg);

				//sending ACK
				sendMessage(MsgFactory.createAckMessage(mesg));


			} else if (mesg instanceof MsgHello) {

				User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);

				if(usr.getFullUserName().equals( Model.getInstance().getLocalUser().getFullUserName())  && !seeLocalUser){
					System.out.println("Received our own hello message => IGNORED");
				}else{
					System.out.println("Hello :) Adding to the UserList");

					UserList.getInstance().addUser(usr);

					System.out.println("Replying");
					Message replymesg = MsgFactory.createReplyPresence(Model.getInstance().getLocalUser(), usr);
					sendMessage(replymesg);
				}



			}
			else if (mesg instanceof  MsgReplyPresence){
				User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);

				if(usr.getFullUserName().equals( Model.getInstance().getLocalUser().getFullUserName())){
					System.out.println("Received our own ReplyPresence message => IGNORED ");
				}else{
					System.out.println("Someone replied :) Adding to the UserList");

					UserList.getInstance().addUser(usr);
				}

			}
			else if (mesg instanceof MsgBye) {
				System.out.println("The user has left");
				UserList.getInstance().updateUserStatus(UserList.getInstance().getUser(User.fullUserName(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(),mesg.getSourcePort()))), false);
			}



			else if (mesg instanceof MsgFile) {
				System.out.println("Testing File Received");

			}

			else if (mesg instanceof MsgAck){
				System.out.println("Received ACK for message #" + ((MsgAck)mesg).getNumMessage());
				//waitingForAck.get(mesg.getNumMessage()) = false;
				//
				if(waitingForAck.get(mesg.getNumMessage())!=null){
					waitingForAck.put(mesg.getNumMessage(), true); // ACK has been received
				}
			}

		}
	}
	
	public int sendMessage(Message message){
		//TODO add error codes
		System.out.println();
		System.out.println("Sending message of type " + message.getClass().toString());
		System.out.println("Src : "+ message.getSourceUserName() + " @" + message.getSourceAddress());
		System.out.println("Dest : @" + message.getDestinationAddress());
		System.out.println("message #" + message.getNumMessage());

		net.sendMessage(message);
		return 0;
	}

	/**
	 * Sends a message expecting an acknowledgment
	 *
	 *
	 * @param message
	 * @param f callback function that is called. Calls with the argument 1 if ACK is received, -1 if it is not received
	 * @return
	 */

	public int sendMessageACK(Message message,Function<Integer, Integer> f){

		// Puts the message number in the hashmap
		// it is updated when an acknowledgement is received
		waitingForAck.put(message.getNumMessage(), false);

		System.out.println();
		System.out.println("Sending message of type " + message.getClass().toString());
		System.out.println("Src : "+ message.getSourceUserName() + " @" + message.getSourceAddress());
		System.out.println("Dest : @" + message.getDestinationAddress());
		System.out.println("message #" + message.getNumMessage());

		Thread t = new Thread(()->{
			System.out.println("Waiting for ACK");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(int i =1; i<4; ++i) {

				if(waitingForAck.get(message.getNumMessage())){
					System.out.println("Ack received ("+i+" attempt(s))");
					f.apply(1);
					return;
				}
				else{
					System.out.println("no ack ("+i+" attempt(s))");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			System.out.println("I AM ERROR");
			try {
				f.apply(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}


		});
		t.start();
		net.sendMessage(message);



		return 0;
	}
}
