package network;

import message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 25/04/2017.
 */
public class NetworkTCPServer {

    ServerSocket socketserver  ;
    Socket socketduserveur ;


    public NetworkTCPServer(int port) throws IOException {


            socketserver = new ServerSocket(port);
            System.out.println("Le serveur est à l'écoute du port "+socketserver.getLocalPort());


           /* socketduserveur.close();
            socketserver.close();*/


    }


    /**
     *
     * @return a message received through TCP. The method is blocked until a message is received.
     * @throws IOException
     */

    public Message receiveMessage() throws IOException {

        if(!socketserver.isClosed()) {
            System.out.println("Waiting for a message");
            socketduserveur = socketserver.accept();
            System.out.println("We received something ");

            ObjectInputStream iStream = new ObjectInputStream(socketduserveur.getInputStream());
            try {
                Object o = iStream.readObject();
                if (o instanceof Message) {
                    iStream.close();
                    return (Message) o;
                } else {
                    System.out.println("The object received by TCP wasn't a message");
                    iStream.close();
                    return null;
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
       }else{
            System.out.println("TCP Server : Can't receive message, server socket is closed !");
        }



        return null;
    }

    public void killSocket() throws IOException {
        System.out.println("Server TCP Socket killed");
        socketduserveur.close();
        socketserver.close();
    }

}



