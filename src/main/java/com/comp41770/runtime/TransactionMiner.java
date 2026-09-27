package com.comp41770.runtime;

import com.comp41770.Mempool;
import com.comp41770.Transaction;

public class TransactionMiner implements Runnable {
    private final Mempool<Transaction> mempool;

    public TransactionMiner(Mempool<Transaction> mempool) {
        this.mempool = mempool;
    }

    @Override
    public void run() {

    }
}
