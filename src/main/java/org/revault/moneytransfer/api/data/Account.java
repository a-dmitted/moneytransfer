package org.revault.moneytransfer.api.data;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = -5183069099661541090L;
    private String number;
    private Long amount;

    public Account() {
    }

    public Account(String number, Long amount) {
        this.number = number;
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}
