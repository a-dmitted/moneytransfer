package org.revault.moneytransfer.api.data;

import java.io.Serializable;

public class Fail implements Serializable {
    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public Fail() {
    }

    public Fail(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
