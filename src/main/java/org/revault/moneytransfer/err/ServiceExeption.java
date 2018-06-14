package org.revault.moneytransfer.err;

public class ServiceExeption extends Exception {
    private Error error;
    public ServiceExeption(Error er){
        super(er.getDescription());
        error = er;
    }

    public Error getError(){return error;}
}
