package com.comp41770;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Transaction transaction0 = new Transaction(23, 46, 2095, 0);
        List<Transaction> data = new LinkedList<>();
        data.add(transaction0);

        Block<Transaction> block1 = new Block<>(data);
        System.out.println(block1.getHash());
        System.out.println(block1.getTimestamp());
        System.out.println(block1.getPreviousHash());

        data.add(new Transaction(395, 35, 23459, 999));
        Block<Transaction> block2 = new Block<>(data, block1.getHash());
        System.out.println(block2.getHash());

        Transaction transaction1 = new Transaction(1, 2, 100, 1);
        System.out.println(transaction1);

        TransactionProducer transactionProducer = new TransactionProducer("outputs/transactions");
        List<Transaction> transactions = transactionProducer.produceTransactions(5);
        for(Transaction i : transactions) {
            System.out.println(i.toString());
        }
        transactionProducer.writeToFile(transactions);
        System.out.println("Transactions written to file, reading them back now...");
        transactionProducer.readFromFile();

        Block<Transaction> block3 = new Block<>(transactions, block2.getHash());

        List<Block<Transaction>> blocks = new LinkedList<>();
        blocks.add(block1);
        blocks.add(block2);
        Chain chain = new Chain(blocks);
        chain.addBlockToChain(block3);
        System.out.println(chain.validateChain());

        System.out.println("Hello world!");

        System.out.println(block3.constructMerkleTree());
        //block3.constructMerkleTree();
    }
}
