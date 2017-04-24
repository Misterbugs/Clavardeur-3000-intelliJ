package network;

/**
 * Created by Romain on 24/04/2017.
 */
public class NetworkTCP {


    private static class SingletonHolder {
        public static final NetworkTCP instance = new NetworkTCP();
    }
    private NetworkTCP(){

    }

    public static NetworkTCP getInstance() {
        return SingletonHolder.instance;
    }


    private int getFirstTCPPort(){

        return 0;
    }


   /*public int createSendingFileSocket(){

    }*/



}
