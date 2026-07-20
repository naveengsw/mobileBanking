package com.example.mobilebanking.android;

public class Account {
    public String accountId;
    public String accountName;
    public String accountType;
    public double balance;

    @Override
    public String toString() {
        return accountName + " • " + accountType + " • $" + String.format("%.2f", balance);
    }
}
