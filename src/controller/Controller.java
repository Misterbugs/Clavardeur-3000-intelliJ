package controller;

import message.MsgFactory;
import model.Model;
import network.Network;

/**
 *
 * Controller class for the Controller of the MVC pattern
 *
 * Created by Romain on 07/05/2017.
 */
public class Controller {

    private NetworkHandler networkHandler = new NetworkHandler(Network.getInstance());

    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }


    private static class SingletonHolder{
        public static final Controller instance = new Controller();
    }
    public static Controller getInstance() {
        return SingletonHolder.instance;
    }

    private Controller(){

    }


    public int logIn(String userName){
        Model.getInstance().createLocalUser(userName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        networkHandler.sendHello(false,10);

        return 1;
    }

    public int logOut(){
        System.out.println("Logging out");
        Network.getInstance().sendMessage(MsgFactory.createByeMessage(Model.getInstance().getLocalUser(),
                Network.getInstance().getBroadcastAddress()));
        return 1;
    }
}
