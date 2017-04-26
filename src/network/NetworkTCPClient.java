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

    public NetworkTCPClient(Address remoteAddress){

        try {

            socket = new Socket(remoteAddress.getIpAdress(),2049);
            System.out.println("Client TCP : Demande de connexion");

            /*in = new BufferedReader (new InputStreamReader(socket.getInputStream()));
            String message_distant = in.readLine();
            System.out.println(message_distant);
            */


        }catch (UnknownHostException e) {

            e.printStackTrace();
        }catch (IOException e) {

            e.printStackTrace();
        }


    }


    public void sendMessage(Message mesg){

        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();


        ObjectOutputStream o = null;

            o = new ObjectOutputStream(bos);
            o.writeObject(mesg);
            o.flush();
            o.close();
           // byte[] b = bos.toByteArray();*/
        try {
            byte[] b = Network.serializeMessage(mesg);
            //System.out.println(b.toString());
            System.out.println("Client TCP : Envoi de message");
            socket.getOutputStream().write(b);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }

    public void killSocket() throws IOException {
        System.out.println("Client TCP Socket killed");
        socket.close();
    }
}
