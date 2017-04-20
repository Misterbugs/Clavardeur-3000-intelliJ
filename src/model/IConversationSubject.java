package model;

import message.Message;

/**
 * Created by Romain on 20/04/2017.
 */
public interface IConversationSubject {

        public void register(IConversationObserver o);
        public void unregister(IConversationObserver o);
        public void notifyObserver(Message mesg);
}
