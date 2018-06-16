package org.revault.moneytransfer.api.data;

public class Success {
    private final String message;

    public Success(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}