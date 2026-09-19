package com.comp41770;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> data = new ArrayList<>();
        data.add("some data");
        data.add("more data");

        Block block1 = new Block(data);
        System.out.println(block1.getHash());
        System.out.println(block1.getTimestamp());
        System.out.println(block1.getPreviousHash());

        data.add("even more data!");
        Block block2 = new Block(data, block1.getHash());
        System.out.println(block2.getHash());

        Transaction transaction1 = new Transaction(1, 2, 100, 1);

        TransactionProducer transactionProducer = new TransactionProducer("outputs/transactions");
        List<Transaction> transactions = transactionProducer.produceTransactions(5);
        for(Transaction i : transactions) {
            System.out.println(i.toString());
        }
        transactionProducer.writeToFile(transactions);
        System.out.println("Transactions written to file, reading them back now...");
        transactionProducer.readFromFile();



        System.out.println("Hello world!");
    }
}
