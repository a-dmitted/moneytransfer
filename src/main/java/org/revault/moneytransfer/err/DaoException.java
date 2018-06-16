package org.revault.moneytransfer.err;

public class DaoException extends Exception {
    private Error error;
    public DaoException(Error er){
        super(er.getDescription());
        error = er;
    }

    public Error getError(){return error;}
}