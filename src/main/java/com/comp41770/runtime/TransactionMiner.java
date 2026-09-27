package com.comp41770.runtime;

import com.comp41770.Block;
import com.comp41770.Mempool;
import com.comp41770.Transaction;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

// I don't see how the blocks will be added to the chain. Practical was not very clear
// on what we are expected to do. As of now, none of the runtime classes seem to be
// operating in a functioning blockchain environment
public class TransactionMiner implements Runnable {
    private final Mempool<Transaction> mempool;
    private final List<Transaction> storedTransactions = new LinkedList<>();
    private final int requiredTransactions;

    public TransactionMiner(Mempool<Transaction> mempool, int requiredTransactions) {
        this.mempool = mempool;
        this.requiredTransactions = requiredTransactions;
    }

    // W Optional usage
    private boolean pollMempool() {
        Optional<Transaction> result = mempool.popFromMempool();
        if(result.isPresent()) {
            result.ifPresent(storedTransactions::add);
            return true;
        } else {
            return false;
        }
    }

    private void mineBlock() {
        Block<Transaction> newBlock = new Block<>(storedTransactions);
        System.out.println("Miner thread #" + Thread.currentThread().getId() + " mining new block...");
        newBlock.mine(2);
        System.out.println("Miner thread #" + Thread.currentThread().getId() + " done mining block.");
        storedTransactions.clear();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(6000); // Let the clients submit transactions first.
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Miner thread #" + Thread.currentThread().getId() + " running.");
        System.out.println("Miner thread #" + Thread.currentThread().getId() + " needs " + requiredTransactions + " transactions to produce a block.");
        while(!mempool.isEmpty()) {
            if(pollMempool() && storedTransactions.size() >= requiredTransactions) {
                mineBlock();
            }
        }
        System.out.println("Miner thread #" + Thread.currentThread().getId() + " exiting.");
    }
}
