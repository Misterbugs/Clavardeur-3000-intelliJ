package message;

import java.io.File;
import java.net.InetAddress;

public class MsgFile extends Message{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7746685928835284793L;

	/**
	 * File
	 */
	private File file;
	private byte[] fileData;
	


	/**
	 * A beautiful constructor using the fileName
	 * @param sourceAddress
	 * @param sourcePort
	 * @param destinationAddress
	 * @param destinationPort
	 * @param numMessage
	 * @param file
	 */
	public MsgFile(InetAddress sourceAddress, int sourcePort, String senderUserName, InetAddress destinationAddress, int destinationPort,int numMessage, File file, byte[] fileData) {
		super(sourceAddress, sourcePort, senderUserName, destinationAddress, destinationPort, numMessage);

		this.fileData = fileData;
		this.file = file;
	}

	public byte[] getFileData(){ return fileData;}
	public File getFile() {
		return file;
	}

}
