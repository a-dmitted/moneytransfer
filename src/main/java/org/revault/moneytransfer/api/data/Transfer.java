package org.revault.moneytransfer.api.data;

public class Transfer {
    private String debitAcc;
    private String creditAcc;
    private Long amount;

    public Transfer() {
    }

    public Transfer(String debitAcc, String creditAcc, Long amount) {
        this.debitAcc = debitAcc;
        this.creditAcc = creditAcc;
        this.amount = amount;
    }

    public String getDebitAcc() {
        return debitAcc;
    }

    public String getCreditAcc() {
        return creditAcc;
    }

    public Long getAmount() {
        return amount;
    }

    public void setDebitAcc(String debitAcc) {
        this.debitAcc = debitAcc;
    }

    public void setCreditAcc(String creditAcc) {
        this.creditAcc = creditAcc;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
