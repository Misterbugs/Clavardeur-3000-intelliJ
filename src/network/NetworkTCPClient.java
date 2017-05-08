package network;

import message.Message;
import model.Address;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Romain on 25/04/2017.
 */
public class NetworkTCPClient {

    Socket socket;

    public NetworkTCPClient(Address remoteAddress) throws IOException {

        try {

            socket = new Socket(remoteAddress.getIpAdress(),remoteAddress.getPort());
            System.out.println("TCP Client : trying to connect...");

        }catch (UnknownHostException e) {

            e.printStackTrace();
        }


    }


    public void sendMessage(Message mesg){

        try {
            byte[] b = Network.serializeMessage(mesg);
            System.out.println("TCP Client : Sending message");
            socket.getOutputStream().write(b);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void killSocket() throws IOException {
        System.out.println("TCP Client : Socket killed");
        socket.close();
    }
}
