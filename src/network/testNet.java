package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import message.Message;
import message.MsgText;

public class testNet{

	
	DatagramSocket sock;
	//Socket sock;
	int port;
	InetAddress IPAddress;
	public testNet(int _port) throws SocketException{
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		port = _port;
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		sock = new DatagramSocket(_port);  
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ByteArrayInputStream bis = new ByteArrayInputStream(receiveData);
				ObjectInput in = null;

				System.out.println("Socket Started !");
				while(true){
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					
					try {
						sock.receive(receivePacket);
						in = new ObjectInputStream(bis);
						Object o = in.readObject();
						MsgText msg = (MsgText)o;
						System.out.println("Le message deserialse " + o.getClass() + "\nMessage : " + msg.getTextMessage());
						
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
			byte[] b = bos.toByteArray();
			//System.out.println(b.toString());
			sock.send(new DatagramPacket(b, b.length, message.getDestinationAddress(), message.getDestinationPort()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
			
	}
	
	
}
