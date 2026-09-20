package com.comp41770;

import java.io.Serializable;

// IntelliJ really wants to make this a record class, could be a good idea.
// I'm not sure if that would allow us to validate the data at construction, though, which I would like to do eventually.
public class Transaction implements Serializable {
    private final int sender;
    private final int receiver;
    private final int amount; // Will have to do decimals as an int as well
    private final int transactionId; // Will have to implement as a proper ID later

    // Also will have to find a way to ensure transactions are valid at creation
    // IE sender != receiver, amount > 0, etc.
    // This could probably be done through TransactionProducer
    public Transaction(int sender, int receiver, int amount, int transactionId) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Transaction#" + transactionId + " - ID:" + sender + " sends " + amount + " units to ID:" + receiver;
    }

    public int getSender() {
        return sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public int getAmount() {
        return amount;
    }

    public int getTransactionId() {
        return transactionId;
    }
}
