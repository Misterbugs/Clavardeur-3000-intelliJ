package message;

import model.Address;
import model.Model;
import model.User;

public final class MsgFactory {
	
	private static int messageNumber= 0;
	
	private MsgFactory(){}
	
	 public static Message createMessage( User sourceUsr, User destUsr, String txtMesg){
		return new MsgText(sourceUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), sourceUsr.getUserName().get(), 
				destUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), messageNumber++, txtMesg);
	}

	 
	 public static Message createHelloMessage(User sourceUsr, Address broadcastAddress){
		return new MsgHello(sourceUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), sourceUsr.getUserName().get(), 
				broadcastAddress.getIpAdress(), broadcastAddress.getPort(), messageNumber++);
	 }
	 public static Message createByeMessage(User sourceUsr, Address broadcastAddress){
			return new MsgBye(sourceUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), sourceUsr.getUserName().get(),
					broadcastAddress.getIpAdress(), broadcastAddress.getPort(), messageNumber++);
	 }

	 public static Message createReplyPresence(User sourceUsr, User destUsr){
		 return new MsgReplyPresence(sourceUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), sourceUsr.getUserName().get(),
				 destUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), messageNumber++);
	 }

	 public static Message createAckMessage(Message originalMessage){
	 	return new MsgAck(originalMessage.getDestinationAddress(), originalMessage.getDestinationPort(),
				Model.getInstance().getLocalUser().getUserNameString(), originalMessage.getSourceAddress(), originalMessage.getSourcePort(), originalMessage.getNumMessage());
	 }

	 public static Message createFileAskMessage(User sourceUsr, User destUsr, String fileName, long size, int sendingPort){
		 return new MsgAskFile(sourceUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), sourceUsr.getUserName().get(),
				 destUsr.getAddress().getIpAdress(), sourceUsr.getAddress().getPort(), messageNumber++, fileName, size, sendingPort);
	 }

}
