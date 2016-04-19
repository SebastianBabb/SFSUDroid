package woverines.sfsuapp.api;

import woverines.sfsuapp.models.NULLOBJ;

abstract public class Callback {
    public abstract void response(Object object);
    public abstract void error(NULLOBJ nullObj);
}

