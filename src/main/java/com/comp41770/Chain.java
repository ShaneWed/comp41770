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

    public void addBlocksToChain(List<Block<? extends Block<?>>> blocks) {
        blockchain.addAll(blocks);
    }

    public void addBlockToChain(Block<?> block) {
        blockchain.add(block);
    }
}
