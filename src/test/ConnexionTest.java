package test;

import model.Model;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by florian on 24/04/2017.
 */
public class ConnexionTest {

    /**
     *
     */
    @Test
    public void creatinOfLocalUserIsNotNull(){
        System.out.println("This is a connexion test");
        Model model = Model.getInstance();
        model.createLocalUser("John Doe");
        assertNotNull(model.getLocalUser());

    }

}