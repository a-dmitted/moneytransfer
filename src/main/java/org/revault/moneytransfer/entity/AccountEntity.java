package org.revault.moneytransfer.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AccountEntity {
    @Id
    private String number;
    private Long amount;

    public AccountEntity(Long amount) {
        this.amount = amount;
    }

    public AccountEntity(String number, Long amount) {
        this.number = number;
        this.amount = amount;
    }

    public AccountEntity() {
        this.amount = 0L;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
