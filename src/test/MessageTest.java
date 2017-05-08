package test;

import controller.NetworkHandler;
import message.Message;
import message.MsgFactory;
import message.MsgText;
import model.Model;
import model.User;
import network.Network;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Romain on 08/05/2017.
 */
public class MessageTest {
    NetworkHandler networkHandler = new NetworkHandler(Network.getInstance());
    User localUser = Model.getInstance().getLocalUser();
    @Test
    public void messageTest(){

        networkHandler.sendHello(true,0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        //Checks if a conversation between the localUser and himself was created
        assertNotNull(Model.getInstance().getConversation(localUser.getFullUserName()));


    }


    @Test
    public void sendMessage(){
        Message mesg = MsgFactory.createMessage(localUser, localUser, "This is a test Message");
        networkHandler.sendMessage(mesg);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message receivedMessage = Model.getInstance().getConversation(localUser.getFullUserName()).getMessageList().get(0);


        System.out.println(receivedMessage.getClass());
        assert(receivedMessage.getClass() == MsgText.class);
        assert(((MsgText)receivedMessage).getTextMessage().equals(((MsgText)mesg).getTextMessage()));

    }

}
