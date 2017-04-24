package message;

import java.net.InetAddress;

/**
 * Created by Romain on 22/04/2017.
 */



public class MsgReplyFile extends Message {
    public boolean isAcceptsTranfer() {
        return acceptsTranfer;
    }

    public int getReceivingTCPPort() {
        return receivingTCPPort;
    }

    private boolean acceptsTranfer;
    private int receivingTCPPort;

    public MsgReplyFile(InetAddress sourceAddress, int sourcePort, String sourceUserName, InetAddress destinationAddress, int destinationPort, int numMessage,  int receivingTCPPort, boolean acceptsTranfer ) {
        super(sourceAddress, sourcePort, sourceUserName, destinationAddress, destinationPort, numMessage);

        this.acceptsTranfer = acceptsTranfer;
        this.receivingTCPPort = receivingTCPPort;
    }
}
