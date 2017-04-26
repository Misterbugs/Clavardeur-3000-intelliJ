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
import java.io.File;
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


	/**
	 * the files that wait for the authorisation from a distant user to be sent.
	 * Only one file per distant user can wait to be sent.
	 */
	private HashMap<User, File> pendingUploads;
	
	public NetworkHandler(Network net) {
		this.net = net;
		this.net.register(this); //Registering to the network
		this.waitingForAck = new ConcurrentHashMap<>();
		this.pendingUploads = new HashMap<>();
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

				Model.getInstance().getSimpleConversations().get(fullUserName).addMessage(txtMesg);

				//sending ACK
				sendMessage(MsgFactory.createAckMessage(mesg));


			} else if (mesg instanceof MsgHello) {

				User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);

				if(usr.getFullUserName().equals( Model.getInstance().getLocalUser().getFullUserName())  && !seeLocalUser){
				    //if it is our own hello mesage.
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
				    // if it is our own own presence message we ignore it.
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
			else if(mesg instanceof MsgAskFile){
				MsgAskFile msgAskFile = (MsgAskFile)mesg;
				User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);

				Model.getInstance().getConversation(usr.getFullUserName()).notifyObserver(msgAskFile);

				System.out.println("Filename : \""+ msgAskFile.getFilename()+"\", Size : " + msgAskFile.getSize()/ 1024 + "Ko port : "+ msgAskFile.getSendingTCPPort());
			}
			else if (mesg instanceof MsgReplyFile){
				MsgReplyFile msgReplyFile = (MsgReplyFile) mesg;
				if(msgReplyFile.isAcceptsTranfer()){
					System.out.println(msgReplyFile.getSourceUserName() + " accepted to receive our file");
				}
				else{
					System.out.println(msgReplyFile.getSourceUserName() + " declined our file :'(");

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

	public int sendMessageACK(Message message,CallbackFunction callbackFunction){

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
					callbackFunction.call(1);
					return;
				}
				else{
					System.out.println("no ack ("+i+" attempt(s))");
					sendMessage(message);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			System.out.println("I AM ERROR");
			try {
				callbackFunction.call(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}


		});
		t.start();
		net.sendMessage(message);
		return 0;
	}


	/**
	 *
	 * @param distantUser
	 * @param file
	 * @param callbackFunction function called with argument 1 if the file was added to the pendingUploads
	 *                         If there is already a file waiting the function is called with arg 0
	 * @return
	 */

	public int sendAskFile(User distantUser, File file, CallbackFunction callbackFunction){
		Message msg;
		msg = MsgFactory.createFileAskMessage(Model.getInstance().getLocalUser(), distantUser, file.getName(), file.length(), 987);

		boolean added = addToPendingUploads(distantUser, file);

		if(added){
			callbackFunction.call(1);
			sendMessage(msg);
			return 1;
		}else{
			callbackFunction.call(-1);
			return 0;
		}
	}

	/**
	 * Adds a file to the hashmap to get track of which files still waits the authorization to be sent by the local user
	 * @param distantUser
	 * @param file
	 * @return true if the file was added, false if another file is currently waiting
	 */
	private boolean addToPendingUploads(User distantUser,File file){
		if(!pendingUploads.containsKey(distantUser)){
			pendingUploads.put(distantUser, file);
			return true;
		}
		else{
			System.out.println("Can't add file to upload, a file is already waiting to be sent");
			return false;
		}
	}

	public void acceptFile(User distantUser, String filename, long size){
		sendMessage(MsgFactory.createReplyFileMessage(Model.getInstance().getLocalUser(), distantUser, 2049, true));
	}

	public void declineFile(User distantUser, String filename){
		sendMessage(MsgFactory.createReplyFileMessage(Model.getInstance().getLocalUser(), distantUser, -1, false));

	}



}
