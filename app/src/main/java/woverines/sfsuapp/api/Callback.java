package woverines.sfsuapp.api;

/**
 * Created by ironsquishy on 4/12/16.
 */
abstract public class Callback {
    public abstract void response(Object object);
    public abstract void error(Object object);
}

