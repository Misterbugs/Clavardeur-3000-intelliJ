package model;

import message.Message;

/**
 * Created by Romain on 20/04/2017.
 */
public interface IConversationObserver {
    public void update(Message mesg);
}
