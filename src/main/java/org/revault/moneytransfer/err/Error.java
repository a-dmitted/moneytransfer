package org.revault.moneytransfer.err;

public enum Error {
    DATABASE(1000, "A database error has occured."),
    NOT_ENOUGH_MONEY(1001, "Not enough money on the debit accout.");

    private final int code;
    private final String description;

    private Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}