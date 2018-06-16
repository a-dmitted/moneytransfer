package org.revault.moneytransfer.err;

public enum Error {
    ROLLBACK_ERR(2001, "Rollback transaction error."),
    RETRIEVING_ERR(2002, "Error retrieving AccountEntity."),
    DELETING_ERR(2003, "Error deleting AccountEntity."),
    SAVING_ERR(2004, "Error saving AccountEntity."),
    SAVING_TWO_ERR(2005, "Error saving two AccountEntity's."),
    NOT_ENOUGH_MONEY(1001, "Not enough money on the debit accout."),
    DATA_LAYER_ERR(1002, "Error in data layer occured.");



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