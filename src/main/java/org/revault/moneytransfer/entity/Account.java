package org.revault.moneytransfer.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {
    @Id
    private String number;
    private Long amount;

    public Account(Long amount) {
        this.amount = amount;
    }

    public Account(String number, Long amount) {
        this.number = number;
        this.amount = amount;
    }

    public Account() {
        this.amount = 0L;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public boolean isEnough(Long amount){
        if(this.amount < amount)
            return false;
        else
            return true;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
