package com.comp41770;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

// Will have to clean up how the pool is treated
public class TransactionProducer {
    private final String filePath;
    private final Random rand = new Random();
    private int totalTransactions = 0;
    private final LinkedList<Transaction> transactionPool = new LinkedList<>();

    public TransactionProducer(String filePath) {
        this.filePath = filePath;
    }

    public List<Transaction> produceTransactions(int numTransactions) {
        List<Transaction> newTransactions = new LinkedList<>();
        for(int i = 0; i < numTransactions; i++) {
            newTransactions.add(
                    new Transaction(
                            rand.nextInt(1, 1000),
                            rand.nextInt(1, 1000),
                            rand.nextInt(1, 999999),
                            rand.nextInt(1, 9999),
                            ++totalTransactions)
            );
        }
        return newTransactions;
    }

    // Check for fencepost errors with numCandidates
    public List<Transaction> getBlockCandidates(int numCandidates) {
        List<Transaction> candidates = new LinkedList<>();
        if(transactionPool.size() < numCandidates) {
            System.out.println("Can only produce " + transactionPool.size() + " candidates...");
            numCandidates = transactionPool.size();
        }
        for(int i = 0; i < numCandidates; i++) {
            candidates.add(transactionPool.remove());
        }
        return candidates;
    }

    public List<Transaction> getTransactionPool() {
        return transactionPool;
    }

    public void writeToFile(List<Transaction> transactions) {
        try {
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(transactions);
            out.close();
            file.close();
        } catch (IOException exception) {
            System.out.println("IOException while writing transactions to file");
        }
    }

    public void readFromFile() {
        try {
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);
            // Maybe fix the "unchecked cast" warning, but tbh it probably won't ever cause issues
            List<Transaction> transactions = (List<Transaction>) in.readObject();
            for(Transaction transaction : transactions) {
                System.out.println(transaction.toString());
            }
            this.transactionPool.addAll(transactions);
            System.out.println("Transactions added to the pool");
        } catch (IOException exception) {
            System.out.println("IOException while reading transactions from file");
        } catch (ClassNotFoundException exception) {
            System.out.println("ClassNotFoundException while reading transactions from file");
        }
    }
}
