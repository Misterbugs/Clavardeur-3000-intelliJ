package network;

import message.Message;
import org.junit.experimental.theories.Theories;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 25/04/2017.
 */
public class NetworkTCPServer {

    ServerSocket serverSocket  ;
    Socket listeningSocket ;


    public NetworkTCPServer() throws IOException {
            // Instanciation with port 0 will give a free port to the socket
            serverSocket = new ServerSocket(0);

            System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
    }


    /**
     *
     * The method starts a thread that will wait for a message to be received
     * When a message is received it will notify the observers of Network with it.
     */

    public void receiveMessage(boolean killSocketAfterReceive) {

        Thread tReceiveMessage = new Thread(()->{
            if(!serverSocket.isClosed()) {
                System.out.println("Waiting for a message");
                try {
                    listeningSocket = serverSocket.accept();

                    System.out.println("We received something ");

                    ObjectInputStream iStream = new ObjectInputStream(listeningSocket.getInputStream());
                    try {
                        //System.out.println("HELLO");
                        Object o = iStream.readObject();
                        if (o instanceof Message) {
                            iStream.close();
                            //return (Message) o;
                            Thread t = new Thread(()->{
                                //System.out.println("NOTIFIED");
                                Network.getInstance().notifyObserver((Message)o);

                            });
                            t.start();
                        } else {
                            System.out.println("The object received by TCP wasn't a message");
                            iStream.close();
                        }
                        if(killSocketAfterReceive){
                            System.out.println("killing socket");
                            this.killSocket();
                        }


                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("TCP Server : Can't receive message, server socket is closed !");
            }
        });

        tReceiveMessage.start();
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    public void killSocket() throws IOException {
       if(!serverSocket.isClosed()) {
           System.out.println("Killing TCP server socket");
           listeningSocket.close();
           serverSocket.close();
       }else{
           System.out.println("Can't kill socket : the socket is already closed.");
       }
    }

}



