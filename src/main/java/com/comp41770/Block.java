package com.comp41770;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Block<T> {
    private final Instant timestamp;
    // Should data just be transactions? Probably.
    // Or could we use generics instead? That would be cool.
    private final List<T> data;
    private List<List<byte[]>> merkleTree;
    private String previousHash = "";
    private String hash;
    private int nonce = 0;

    public void mine(int difficulty) {
        String zeroes = "";
        for(int i = difficulty; i > 0; i--) {
            zeroes = zeroes.concat("0");
        }

        while(!hash.startsWith(zeroes)) {
            nonce++;
            hash = calculateHash();
        }

        System.out.println("Block mined in " + nonce + " attempts");
    }

    public Block(List<T> data) {
        this.timestamp = Instant.now();
        this.data = data;
        this.hash = calculateHash();
    }

    public Block(List<T> data, String previousHash) {
        this.timestamp = Instant.now();
        this.data = data;
        this.previousHash = previousHash;
        constructMerkleTree(); // I don't like initializing it this way.
        this.hash = calculateHash();
    }

    // Declare a block with a specific timestamp for testing purposes
    public Block(Instant timestamp, List<T> data, String previousHash) {
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash;
    }

    // Using Merkle Tree in hash. Is it still necessary to use data.toString()? That data is kind of stored in merkleTree
    public String calculateHash() {
        return DigestUtils.sha256Hex(timestamp + data.toString() + previousHash + merkleTree + nonce);
    }

    // Should this return a status of some sort? Not sure.
    public void addData(List<T> newDataList) {
        this.data.addAll(newDataList);
        this.hash = calculateHash();
    }

    // Constructs Merkle Tree from this block's data, sets the block's Merkle Tree, and returns the tree
    public List<List<byte[]>> constructMerkleTree() {
        List<List<byte[]>> layers = constructMerkleTreeHelper(data);
        setMerkleTree(layers);
        return layers;
    }

    public List<List<byte[]>> constructMerkleTreeFromData(List<T> inputs) {
        return constructMerkleTreeHelper(inputs);
    }

    private List<List<byte[]>> constructMerkleTreeHelper(List<T> inputs) {
        List<byte[]> leaves = new ArrayList<>();
        for(T point : inputs) {
            leaves.add(DigestUtils.sha256(point.toString()));
        }
        List<List<byte[]>> layers = new ArrayList<>();
        layers.add(leaves);

        while(layers.get(layers.size() - 1).size() > 1) {
            List<byte[]> current = layers.get(layers.size() - 1);
            List<byte[]> next = new ArrayList<>();

            for(int i = 0; i < current.size() - 1; i += 2) {
                byte[] left = current.get(i);
                byte[] right;
                if(i + 1 < current.size()) {
                    right = current.get(i + 1);
                } else {
                    right = current.get(i);
                }
                byte[] combined = new byte[left.length + right.length];
                System.arraycopy(left, 0, combined, 0, left.length);
                System.arraycopy(right, 0, combined, left.length, right.length);
                next.add(DigestUtils.sha256(combined));
            }
            layers.add(next);
        }

        return layers;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<T> getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setMerkleTree(List<List<byte[]>> merkleTree) {
        this.merkleTree = merkleTree;
    }

    public List<List<byte[]>> getMerkleTree() {
        return merkleTree;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
}