package controller;

import message.*;
import model.Address;
import model.Model;
import model.User;
import model.UserList;
import network.Network;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Interracts with the network in order to send and receive messages.
 * Updates the model.
 * Implements a Singleton Design Pattern, as well as an Observer Design Pattern
 * It is the observer of the observer pattern.
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

		User sourceUser = UserList.getInstance().getSourceUser(mesg);


		if (Model.getInstance().getLocalUser() != null) {

			if (mesg instanceof MsgText) {

				MsgText txtMesg = (MsgText) mesg;
				System.out.println("Text message : \"" + txtMesg.getTextMessage() + "\""); //DEBUG

				if (sourceUser != null) {
					Model.getInstance().getSimpleConversations().get(sourceUser.getFullUserName()).addMessage(txtMesg);

					//sending ACK
					sendMessage(MsgFactory.createAckMessage(mesg));
				}

			} else if (mesg instanceof MsgHello) {

				if (sourceUser != null) {
					if (sourceUser.isLocalUser()) {
						//if it is our own hello mesage.
						System.out.println("Received our own hello message => IGNORED");
					}
				} else {
					User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);
					System.out.println("Hello :) Adding to the UserList");

					UserList.getInstance().addUser(usr);

					System.out.println("Replying");
					Message replymesg = MsgFactory.createReplyPresence(Model.getInstance().getLocalUser(), usr);
					sendMessage(replymesg);
				}

			} else if (mesg instanceof MsgReplyPresence) {
				//
				if (sourceUser != null) {

					if (sourceUser.isLocalUser()) {
						// if it is our own own presence message we ignore it.
						System.out.println("Received our own ReplyPresence message => IGNORED ");
					} else {

					}
				} else {
					User usr = new User(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()), true);
					System.out.println("Someone that we don't know replied :) Adding to the UserList");
					UserList.getInstance().addUser(usr);
				}

			} else if (mesg instanceof MsgBye) {
				System.out.println("The user has left");
				UserList.getInstance().updateUserStatus(UserList.getInstance().getUser(User.fullUserName(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()))), false);

			} else if (mesg instanceof MsgFile) {
				MsgFile msgFile = (MsgFile)mesg;
				try {

					System.out.println("File size : "+ msgFile.getFileData().length);
					OutputStream out = new FileOutputStream(new File("Downloads/" + msgFile.getFile().getName()));
					out.write(msgFile.getFileData());

				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (mesg instanceof MsgAck) {
				System.out.println("Received ACK for message #" + ((MsgAck) mesg).getNumMessage());

				if (waitingForAck.get(mesg.getNumMessage()) != null) {
					waitingForAck.put(mesg.getNumMessage(), true); // ACK has been received
				}
			}

			/**
			 * When a remote user wants to send a file to the local user
			 * The interface is notified alowing the local user to make a choice
			 */
			else if (mesg instanceof MsgAskFile) {
				MsgAskFile msgAskFile = (MsgAskFile) mesg;
				if (sourceUser != null) {
					Model.getInstance().getConversation(sourceUser.getFullUserName()).notifyObserver(msgAskFile);
					System.out.println("Filename : \"" + msgAskFile.getFilename() + "\", Size : " + msgAskFile.getSize() / 1024 + "Ko port : " + msgAskFile.getSendingTCPPort());
				}

			} else if (mesg instanceof MsgReplyFile) {
				if(sourceUser != null){
					MsgReplyFile msgReplyFile = (MsgReplyFile) mesg;
					if (msgReplyFile.isAcceptsTranfer()) {
						System.out.println(msgReplyFile.getSourceUserName() + " accepted to receive our file on port " + msgReplyFile.getReceivingTCPPort());
						File file = pendingUploads.get(sourceUser);
						if (file != null) {

							Address tcpAddr = new Address(sourceUser.getAddress().getIpAdress(), msgReplyFile.getReceivingTCPPort());
							RandomAccessFile f = null;
							byte[] fileData = null;
							try {
								f = new RandomAccessFile(file, "r");
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}

							if(f!=null){
								try {
									fileData = new byte[(int)f.length()];
									f.readFully(fileData);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							if(fileData != null) {
								Message message = MsgFactory.createFileMessage(Model.getInstance().getLocalUser(), tcpAddr, file, fileData);

								net.sendTCPMessage(message);
								pendingUploads.remove(sourceUser);
							}else{
								System.out.println("Couldn't get the data from the file");
							}
						} else {
							System.out.println("Couldn't get file from pending uploads");
						}

					} else {
						System.out.println(msgReplyFile.getSourceUserName() + " declined our file :'(");

					}
			}
		}

		}
	}
	
	public int sendMessage(Message message){

		System.out.println();
		System.out.println("Sending message of type " + message.getClass().toString());
		System.out.println("Src : "+ message.getSourceUserName() + " @" + message.getSourceAddress());
		System.out.println("Dest : @" + message.getDestinationAddress());
		System.out.println("message #" + message.getNumMessage());

		int res = net.sendMessage(message);

		if(res!=1){
			System.out.println("Network : error : the message couldn't be sent");
		}
		return res;

	}

	/**
	 * Sends a message expecting an acknowledgment
	 *
	 *
	 * @param message
	 * @param callbackFunction callback function that is called. Calls with the argument 1 if ACK is received, -1 if it is not received
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


	/**
	 * When a file is accepted by the local user
	 * Creates the TCP socket to receive the file and kills it when the file is received.
	 * @param distantUser
	 * @param filename
	 * @param size
	 */
	public void acceptFile(User distantUser, String filename, long size){

		int port =net.receiveTCPMessage();
		sendMessage(MsgFactory.createReplyFileMessage(Model.getInstance().getLocalUser(), distantUser, port, true));
	}

	/**
	 * Declines the receiving of a file.
	 * @param distantUser
	 * @param filename
	 */
	public void declineFile(User distantUser, String filename){
		sendMessage(MsgFactory.createReplyFileMessage(Model.getInstance().getLocalUser(), distantUser, -1, false));

	}




	/**
	 * Sends one or multiple Hello messages
	 *
	 * @param sendOneTime true if the hello message is broadcasted only once
	 *                    false if the hello message is sent periodically
	 * @param period time in seconds between two broadcasts of hello messages on the network
	 */

	public void sendHello(boolean sendOneTime, int period){


		if(sendOneTime){

			Network.getInstance().sendMessage(MsgFactory.createHelloMessage(Model.getInstance().getLocalUser(),
					Network.getInstance().getBroadcastAddress()));

		}else {
			Thread threadHello = new Thread(() -> {
				while (true) {
					Network.getInstance().sendMessage(MsgFactory.createHelloMessage(Model.getInstance().getLocalUser(),
							Network.getInstance().getBroadcastAddress()));
					try {
						Thread.sleep(period*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			});

			threadHello.start();
		}
	}



}
