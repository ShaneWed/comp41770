package com.comp41770;

import com.comp41770.runtime.TransactionClient;
import com.comp41770.runtime.TransactionMiner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n\nWorksheet 4 Task 3\n\n");

        Block<String> genesisBlock = new Block<>(List.of("In the beginning"), "hash");
        genesisBlock.mine(2);
        Chain blockchain = new Chain(List.of(genesisBlock));

        // Eventually we will change mempool to the most likely sort by transaction fee.
        Mempool<Transaction> mempool = new Mempool<>(Comparator.comparing(Transaction::getTimestamp));
        List<Thread> threads = getThreads(mempool, blockchain);

        threads.forEach(Thread::start);

        // Joining threads so we can validate the blockchain after they've finished.
        try {
            for(Thread thread : threads) {
                thread.join(); // Idk why the forEach was giving an error, but this isn't.
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        // To get the remainder of the transactions from the mempool.
        TransactionMiner lateMiner = new TransactionMiner(mempool, blockchain, 4);
        Thread lateMinerThread = new Thread(lateMiner);
        lateMinerThread.start();
        try {
            lateMinerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Blockchain valid: " + blockchain.validateChain());
    }

    private static List<Thread> getThreads(Mempool<Transaction> mempool, Chain blockchain) {
        TransactionClient client1 = new TransactionClient(mempool, 5);
        TransactionClient client2 = new TransactionClient(mempool, 10);
        TransactionMiner greedyMiner = new TransactionMiner(mempool, blockchain, 1);
        TransactionMiner lazyMiner = new TransactionMiner(mempool, blockchain, 3);

        Thread clientThread1 = new Thread(client1);
        Thread clientThread2 = new Thread(client2);
        Thread greedyMinerThread = new Thread(greedyMiner);
        Thread lazyMinerThread = new Thread(lazyMiner);

        List<Thread> threads = new ArrayList<>();

        threads.add(clientThread1);
        threads.add(clientThread2);
        threads.add(greedyMinerThread);
        threads.add(lazyMinerThread);
        return threads;
    }
}
