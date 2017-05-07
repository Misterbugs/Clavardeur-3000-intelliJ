package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import com.sun.security.ntlm.Server;
import controller.INetworkObserver;
import message.Message;
import message.MsgText;
import model.Address;

import javax.swing.*;

public class Network implements INetworkSubject{

	
	private DatagramSocket sock;
	private int port = 2048; //default value
	//private InetAddress IPAddress;

	private Address localAddress;
	private Address broadcastAddress;
	
    private ArrayList<INetworkObserver> observers;

    private static class SingletonHolder{ 
        public static final Network instance = new Network();
    }
	
	@Override
	public void register(INetworkObserver o) {
		// TODO Auto-generated method stub
		observers.add(o);
	}

	@Override
	public void unregister(INetworkObserver o) {
		observers.remove(o);
	}

	@Override
	public void notifyObserver(Message mesg) {
		for(INetworkObserver o : observers){
			o.update(mesg);
		}
	}
	
	public static Network getInstance() {
        return SingletonHolder.instance;
    }
	
	private Network(){


		observers = new ArrayList<>();

		if(!initAddresses()){
			System.out.println("Messed up addresses");

		}

		try {
			sock = new DatagramSocket(port);
		} catch (SocketException e1) {
			System.out.println("Canno't bind : port "+port+" already taken.");
			System.out.println("Exiting...");
			System.exit(-1);
			e1.printStackTrace();
		}  

		Thread t = new Thread(()->{

			byte[] receiveData = new byte[1024];
			System.out.println("UDP Socket Started !");
			while(true){

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				try {
					sock.receive(receivePacket);
					ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(receiveData));
					Object o = iStream.readObject();
					iStream.close();

					if(o instanceof Message){
						Message mesg = (Message)o;
						notifyObserver(mesg);

					}else{
						System.out.println("Object received is not a message, class is " + o.getClass().toString());
						//TODO make a throw declaration?
					}

				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		t.start();
	}
	
	
	public void sendMessage(String data){
		System.out.println("Sending");
		DatagramPacket sendData = new DatagramPacket(data.getBytes(), data.getBytes().length, localAddress.getIpAdress(), port);
		try {
			sock.send(sendData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void sendMessage(Message message){

		try {

			byte[] b = serializeMessage(message);
			sock.send(new DatagramPacket(b, b.length, message.getDestinationAddress(), message.getDestinationPort()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public static byte[] serializeMessage(Message message){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ObjectOutputStream o = null;
		byte[] b = null;
		try {
			o = new ObjectOutputStream(bos);
			o.writeObject(message);
			o.flush();
			o.close();
			b = bos.toByteArray();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;


	}


	/**
	 * Initialize Local and broadcast addresses. Checks of the interface in order to find an address that is not loopback
	 * @return true if a suitable local address is found
	 */

	private boolean initAddresses(){

		try {
			System.out.println("Listing Network addresses untill finding a suitable one");
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			// Listing network interfaces interfaces
			for (NetworkInterface netint : Collections.list(nets)) {

				// discarding loopback and virtual interfaces
				if(netint.isLoopback() ||  netint.isVirtual()){
					continue;
				}

				// Listing Address for the current interface
				Enumeration<InetAddress> inetenum =  netint.getInetAddresses();
				int cptAddr =  0;
				for(InetAddress addr : Collections.list( inetenum)){

					//Discarding bad addresses
					if(addr.isAnyLocalAddress() || addr.isLoopbackAddress() || addr.isLinkLocalAddress() ){
						++cptAddr;
						continue;
					}
					else{
						System.out.println(addr.getHostAddress());

						//Discarding addresses too long (not IP) or loopback that wasn't catched earlier.
						if(addr.getHostAddress().contains("192.168.56") || addr.getHostAddress().length() > 15 ){
							++cptAddr;
							continue;
						}
						else {
							//netint.getInterfaceAddresses().get(0);
							//System.out.println(" cptAddr : " + cptAddr);
							System.out.println("Local Address : " + addr.toString());
							System.out.println("Broadcast Address : "   + netint.getInterfaceAddresses().get(cptAddr).getBroadcast().toString()); //TODO Réparer, maitenant ça marche peut-être
						//	System.out.println("Generated Broadcast mask : " +  netint.getInterfaceAddresses().get(0).getBroadcast().);
							System.out.println("Network mask : /" + netint.getInterfaceAddresses().get(cptAddr).getNetworkPrefixLength());
							broadcastAddress = new Address (netint.getInterfaceAddresses().get(cptAddr).getBroadcast(), port );
							localAddress = new Address(addr, port);
							return true;
						}
					}
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}

		return false;
	}


	public Address getLocalAddress() {
		return localAddress;
	}


	/**
	 *	Initialize a TCP server that will receive the file.
	 * @return the port number of the socket created, returns -1 if there was an error
	 */
	public int receiveTCPMessage(){
		NetworkTCPServer networkTCPServer =null;
		try {
			networkTCPServer = new NetworkTCPServer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		networkTCPServer.receiveMessage(true);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(networkTCPServer != null ){
			return networkTCPServer.getPort();
		}else{
			return -1;
		}
	}


	/**
	 * Creates and kills a TCP client socket in order to send a message
	 * @param mesg
	 * @return
	 */
	public int sendTCPMessage(Message mesg){
		try {
			NetworkTCPClient networkTCPClient = new NetworkTCPClient(new Address(mesg.getDestinationAddress(), mesg.getDestinationPort()));
			networkTCPClient.sendMessage(mesg);
			networkTCPClient.killSocket();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;

	}

	/**
	 * Displays every Network interface with their addresses.
	 * @param netint
	 * @throws SocketException
	 */

	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
		System.out.printf("Display name: %s\n", netint.getDisplayName());
		System.out.printf("Name: %s\n", netint.getName());
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
			System.out.printf("InetAddress: %s\n", inetAddress);
		}
		System.out.printf("\n");
	}

	public Address getBroadcastAddress(){
		return broadcastAddress;
	}
	
}
