package test;

import com.sun.xml.internal.ws.developer.UsesJAXBContext;
import controller.NetworkHandler;
import message.Message;
import message.MsgAskFile;
import message.MsgFactory;
import message.MsgText;
import model.Address;
import model.Model;
import model.User;
import model.UserList;
import network.Network;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Romain on 08/05/2017.
 */
public class MessageTest {
    NetworkHandler networkHandler = new NetworkHandler(Network.getInstance());
    User localUser = Model.getInstance().getLocalUser();

    @Test
    public void messageTest() {

        networkHandler.sendHello(true, 0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Checks if a conversation between the localUser and himself was created
        assertNotNull(Model.getInstance().getConversation(localUser.getFullUserName()));


    }


    @Test
    public void sendMessage() {
        Message mesg = MsgFactory.createMessage(localUser, localUser, "This is a test Message");
        networkHandler.sendMessage(mesg);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message receivedMessage = null;

        for(Message m : Model.getInstance().getConversation(localUser.getFullUserName()).getMessageList()){
            if(m.getNumMessage() == mesg.getNumMessage()){
                receivedMessage = m;
                break;
            }
        }

        if(receivedMessage!=null){
            assert (receivedMessage.getClass() == MsgText.class);
            assert (((MsgText) receivedMessage).getTextMessage().equals(((MsgText) mesg).getTextMessage()));

        }
        else{
            assert false;
        }

       // Message receivedMessage = Model.getInstance().getConversation(localUser.getFullUserName()).getMessageList().get(0);



    }

    @Test
    public void messageNotDeliveredACK(){
        User notPresentUser = null;
        try {
        notPresentUser = new User("NotPresent", new Address(InetAddress.getByName("10.0.0.0"), 2048), true);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        UserList.getInstance().addUser(notPresentUser);
        networkHandler.sendMessageACK(MsgFactory.createMessage(localUser, notPresentUser,"Test"),res ->{
            if(res ==1){
                assert false;
            }
            else{
                assert true;
            }
        });


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void messageDeliveredACK(){

        networkHandler.sendMessageACK(MsgFactory.createMessage(localUser,localUser,"Hello me!"), res ->{
           if(res ==1){
               assert true;
           } else{
               assert false;
           }

        });


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
