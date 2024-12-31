package com.iremia.app.numisight.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class Transaction {
    private long mTransactionId;
    private int mType; // 1 - deposit, 0 - withdrawal
    private double mAmount;
    private java.util.Date mTimestamp;
    private String mDescription;
    private int mPaymentMethod; // 1 - card, 0 - cash
    private String mCategory;
    private double mBalance;

    public Transaction() {
    }

    public Transaction(int id, int type, double amount, java.util.Date timestamp, String description, String category, int paymentMethod, double balance) {
        mTransactionId = id;
        mType = type;
        mAmount = amount;
        mTimestamp = timestamp;
        mDescription = description;
        mCategory = category;
        mPaymentMethod = paymentMethod;
        mBalance = balance;
    }

    public long getPaymentId() {
        return mTransactionId;
    }

    public int getType() {
        return mType;
    }

    public double getAmount() {
        return mAmount;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getPaymentMethod() {
        return mPaymentMethod;
    }

    public String getCategory() {
        return mCategory;
    }

    public double getBalance() {
        return mBalance;
    }

    @NonNull
    @Override
    public String toString() {
        return mTransactionId + ". " + mType
                + "\r\n"
                + mAmount + " " + mCategory
                + "\r\n"
                + mDescription
                + "\r\n"
                + mTimestamp;
    }
}
