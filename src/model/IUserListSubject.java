package model;

/**
 * Created by Romain on 22/04/2017.
 */
public interface IUserListSubject {
    public void register(IUserListObserver o);
    public void unregister(IUserListObserver o);
    public void notifyObserver(User usr);
}
