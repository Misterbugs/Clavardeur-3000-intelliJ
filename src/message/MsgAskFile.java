package message;

import java.net.InetAddress;

/**
 * Created by Romain on 22/04/2017.
 */
public class MsgAskFile extends Message {
    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public int getSendingTCPPort() {
        return sendingTCPPort;
    }

    /**
     * Basic constructor for Message
     *
     * @param sourceAddress
     * @param sourcePort
     * @param sourceUserName
     * @param destinationAddress
     * @param destinationPort
     * @param numMessage
     */

    String filename;
    long size;
    int sendingTCPPort;


    public MsgAskFile(InetAddress sourceAddress, int sourcePort, String sourceUserName, InetAddress destinationAddress, int destinationPort, int numMessage, String fileName, long size, int sendingTCPPort) {
        super(sourceAddress, sourcePort, sourceUserName, destinationAddress, destinationPort, numMessage);
        this.filename =fileName;
        this.size = size;
        this.sendingTCPPort=sendingTCPPort;
    }
}
