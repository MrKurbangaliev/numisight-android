package com.iremia.app.numisight.model;

import androidx.annotation.NonNull;

public class Account {
    private int mAccountId;
    private String mAccountLabel;
    private String mCurrency;
    private String mDbReference;
    private double mBalance;

    public Account() {
    }

    public Account(int id, String label, String dbReference, String currency, double balance) {
        mAccountId = id;
        mAccountLabel = label;
        mDbReference = dbReference;
        mCurrency = currency;
        mBalance = balance;
    }

    public String getAccountLabel() {
        return mAccountLabel;
    }

    public void setAccountLabel(String name) {
        this.mAccountLabel = name;
    }

    public String getDbReference() {
        return mDbReference;
    }

    public int getAccountId() {
        return mAccountId;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public double getBalance() {
        return mBalance;
    }

    public void setBalance(double balance) {
        this.mBalance = balance;
    }

    @NonNull
    @Override
    public String toString() {
        return mAccountId + ". " + mAccountLabel + " " + mCurrency + " (" + mDbReference + ")\r\n" + mBalance;
    }
}
