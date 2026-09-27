package com.comp41770.runtime;

import com.comp41770.Mempool;
import com.comp41770.Transaction;
import com.comp41770.TransactionProducer;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

// Eventually I would like the client to also use generics; however, the practical specifically uses transactions.
// I hate the IntelliJ grammar correction
public class TransactionClient implements Runnable {
    private final TransactionProducer transactionProducer = new TransactionProducer("/outputs/mempoolTransactions");
    private final Queue<Transaction> queue = new ArrayDeque<>();
    private final Random rand = new Random();
    private final Mempool<Transaction> mempool;

    public TransactionClient(Mempool<Transaction> mempool) {
        this.mempool = mempool;
        queue.addAll(transactionProducer.produceTransactions(3));
    }

    private void submitTransaction() {
        // Handle nulls here or in the run loop?
        mempool.addToMempool(queue.poll());
        System.out.println("Client thread #" + Thread.currentThread().getId() + " submitted transaction to mempool.");
    }

    private void generateNewTransaction() {
        queue.addAll(transactionProducer.produceTransactions(1));
    }

    @Override
    public void run() {
        System.out.println("Client thread #" + Thread.currentThread().getId() + " running.");
        // Submit a new transaction after a bit (how long and how many?)
        while(!queue.isEmpty()) {
            try {
                Thread.sleep(rand.nextInt(1000, 3000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            submitTransaction();
            if(rand.nextBoolean()) {
                generateNewTransaction();
            }
        }
        System.out.println("Client thread #" + Thread.currentThread().getId() + " exiting.");
    }
}
