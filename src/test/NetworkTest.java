package test;

import network.Network;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Romain on 08/05/2017.
 */
public class NetworkTest {

    @Test
    public void networkTest(){
        assertNotNull(Network.getInstance().getLocalAddress());
        assertNotNull(Network.getInstance().getBroadcastAddress());
    }

}
