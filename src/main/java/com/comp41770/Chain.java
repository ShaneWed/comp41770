package com.comp41770;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Chain {
    private final List<Block<?>> blockchain;

    // Should this validate immediately upon construction?
    // A chain has to consist of multiple blocks
    public Chain(List<? extends Block<?>> blocks) {
        this.blockchain = new LinkedList<>(blocks);
    }

    public boolean validateChain() {
        for(int i = 1; i < blockchain.size(); i++) {
            Block<?> current = blockchain.get(i);
            Block<?> previous = blockchain.get(i - 1);

            if(!Objects.equals(current.getPreviousHash(), previous.getHash())) {
                System.out.println("Invalid chain due to broken hash...");
                return false;
            }
            if(!Objects.equals(current.getHash(), current.calculateHash())) {
                System.out.println("Invalid chain due to hash mismatch...");
                return false;
            }
        }
        return true;
    }

    public synchronized String getPreviousHash() {
        return blockchain.get(blockchain.size() - 1).getHash();
    }

    // I'm not sure if this function will work. Have not tested it.
    public synchronized boolean addBlocksToChain(List<Block<? extends Block<?>>> blocks) {
        String previousHash = blockchain.get(blockchain.size() - 1).getHash();
        if(!blocks.get(blocks.size() - 1).getPreviousHash().equals(previousHash)) {
            return false;
        }
        blockchain.addAll(blocks);
        return true;
    }

    public synchronized boolean addBlockToChain(Block<?> block) {
        String previousHash = blockchain.get(blockchain.size() - 1).getHash();
        if(!block.getPreviousHash().equals(previousHash)) {
            // Hopefully we don't see this line.
            System.out.println("Error adding block due to hash!");
            return false;
        }
        blockchain.add(block);
        return true;
    }
}
