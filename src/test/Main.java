package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import controller.NetworkHandler;
import message.Message;
import message.MsgFactory;
import model.Address;
import model.User;
import network.Network;


public class Main {

	public static void main(String[] args) {
		Network net = Network.getInstance();
		
		
		Address broadcastAddr = net.getBroadcastAddress();
		
		NetworkHandler nethandler = new NetworkHandler(net);
		
		
		User localUsr;
		InetAddress inet = null;
		try {
			inet = InetAddress.getLocalHost();
			System.out.println("Local Address is :" + inet);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		localUsr = new User("John Doe", new Address( inet, 2048), true) ;
		
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message m = MsgFactory.createHelloMessage(localUsr, broadcastAddr);
		
		//Message m = MsgFactory.createMessage(localUsr, localUsr, "Bien le bonjour");
		

		System.out.println(m.getSourceUserName());
		
		System.out.println("Constructing");
		net.sendMessage(m);
		System.out.println("sent");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message me = MsgFactory.createByeMessage(localUsr, broadcastAddr);
		net.sendMessage(me);

	}



}
