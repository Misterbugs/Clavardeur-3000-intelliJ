package controller;

import java.util.function.Function;

/**
 * Created by Romain on 26/04/2017.
 * A function that is called by the controller but declared in the view
 * Usually used for confirmation dialog and other
 */
public interface CallbackFunction{
        void call(int code);
}

