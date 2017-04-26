package test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import controller.NetworkHandler;
import message.Message;
import message.MsgFactory;
import model.Address;
import model.Model;
import model.User;
import network.Network;
import network.NetworkTCPClient;
import network.NetworkTCPServer;


public class Main {

	public static void main(String[] args) {
		/*Network net = Network.getInstance();
		
		
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
		net.sendMessage(me);*/

		Network net = Network.getInstance();
		Model mod = Model.getInstance();
		Model.getInstance().createLocalUser("Toto");


		Thread tServer = new Thread(()->{
			try {
				NetworkTCPServer tcpServ = new NetworkTCPServer(2049);
				System.out.println("Hello?");
				tcpServ.receiveMessage();
				tcpServ.killSocket();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		Thread tClient = new Thread (()->{
			NetworkTCPClient tcpClient = new NetworkTCPClient(new Address(net.getLocalAddress().getIpAdress(), 2049));
			tcpClient.sendMessage(MsgFactory.createMessage(mod.getLocalUser(), mod.getLocalUser(),"Test TCP"));
			try {
				tcpClient.killSocket();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		tServer.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		tClient.start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Exiting");
		System.exit(1);
	}






}
