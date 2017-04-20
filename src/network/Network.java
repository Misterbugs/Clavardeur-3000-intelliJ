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

import controller.INetworkObserver;
import message.Message;
import message.MsgText;
import model.Address;

public class Network implements INetworkSubject{

	
	DatagramSocket sock;
	int port = 2048; //default value
	InetAddress IPAddress;

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
		System.out.println("number of observers = " + observers.size());
		
		for(INetworkObserver o : observers){
			o.update(mesg);
		}
	}
	
	public static Network getInstance() {
        return SingletonHolder.instance;
    }
	
	private Network(){
		
		observers = new ArrayList<INetworkObserver>();

		if(initAddresses()==0){
			System.out.println("Messed up addresses");

		}

		try {
			sock = new DatagramSocket(port);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] receiveData = new byte[1024];
				ByteArrayInputStream bis = new ByteArrayInputStream(receiveData);
				ObjectInput in = null;
				
				
				
				
				//ObjectInputStream input = new ObjectInputStream(new FileInputStream("test.tmp"));
				System.out.println("Socket Started !");
				while(true){
					//System.out.println("oijoij");
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					
					try {
						sock.receive(receivePacket);
						/*if(in == null){
							in = new ObjectInputStream(bis);
						}*/
						//System.out.println(in.available());
						
						ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(receiveData));
						Object o = iStream.readObject();
						iStream.close();
						
						//Object o = in.readObject();
						//System.out.println(in.available());
												
						if(o instanceof Message){
							System.out.println("Message Received");
							Message mesg = (Message)o;
							notifyObserver(mesg);
							
							/*MsgText msg = (MsgText)o;
							System.out.println("Le message deserialse " + o.getClass() + "\nMessage : " + msg.getTextMessage());*/
						}else{
							System.out.println("Object received is not a message, class is " + o.getClass().toString());
							//TODO make a throw declaration?
						}
						
						
						
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					//String sentence =new String(receivePacket.getData(),0, receivePacket.getLength());
					System.out.println("RECEIVED");
				}
			}
		});
		
		
		t.start();
	}
	
	
	public void sendMessage(String data){
		System.out.println("Sending");
		DatagramPacket sendData = new DatagramPacket(data.getBytes(), data.getBytes().length, IPAddress, port);
		try {
			sock.send(sendData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void sendMessage(Message message){
		//InetAddress a = null;
		//a = InetAddress.getByName("localhost");
		//MsgText theMsgText = new MsgText(a, 2048, a, 2048, 0, "This is a text message");	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		ObjectOutputStream o = null;
		try {
			o = new ObjectOutputStream(bos);
			o.writeObject(message);
			o.flush();
			o.close();
			byte[] b = bos.toByteArray();
			//System.out.println(b.toString());
			sock.send(new DatagramPacket(b, b.length, message.getDestinationAddress(), message.getDestinationPort()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	
	}



	private int initAddresses(){

		try {

			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {


				if(netint.isLoopback() ||  netint.isVirtual()){
					continue;
				}
				Enumeration<InetAddress> inetenum =  netint.getInetAddresses();
				for(InetAddress addr : Collections.list( inetenum)){
					//System.out.println(addr.);
					if(addr.isAnyLocalAddress() || addr.isLoopbackAddress() || addr.isLinkLocalAddress() ||addr.isAnyLocalAddress() ){
						continue;
					}
					else{
						System.out.println(addr.getHostAddress());

						if(addr.getHostAddress().matches("192.168.56*") || addr.getHostAddress().length() > 12){

							System.out.println("Raté");
						}
						else {


							System.out.println("Addr broadcast : "   + netint.getInterfaceAddresses().get(0).getBroadcast().toString()); //TODO Réparer
							System.out.println("Addr locale : " + addr.toString());
							broadcastAddress = new Address (netint.getInterfaceAddresses().get(0).getBroadcast(), port );
							localAddress = new Address(addr, port);
							return 1;
						}
					}
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}

		return 0;
	}



	public Address getLocalAddress() {
		return localAddress;
	}

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
		/*InetAddress broadcastIpAddress = null;

		broadcastIpAddress = getLocalAddress().getIpAdress();
		getLocalAddress();

		System.out.println("local address " + broadcastIpAddress.getHostAddress());
		Address addr =null;
		try {
			addr = new Address(InetAddress.getByAddress(new byte[]{(byte) 255,(byte) 255,(byte) 255,(byte) 255}), port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Generated Broadcast Address : " + addr.getIpAdress().getHostAddress());
		*/
		return broadcastAddress;




	}
	
	
	
}
