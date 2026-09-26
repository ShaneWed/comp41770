package com.comp41770;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Block<T> {
    private final Instant timestamp;
    // Should data just be transactions? Probably.
    // Or could we use generics instead? That would be cool.
    private final List<T> data;
    // TreeMap? Integer, T? For now.
    private TreeMap<Integer, T> merkleTree;
    private String previousHash = "";
    private String hash;

    public List<List<byte[]>> constructMerkleTree() {
        List<byte[]> leaves = new ArrayList<>();
        for(T point : data) {
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

    public Block(List<T> data) {
        this.timestamp = Instant.now();
        this.data = data;
        this.hash = calculateHash();
    }

    public Block(List<T> data, String previousHash) {
        this.timestamp = Instant.now();
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    // Declare a block with a specific timestamp for testing purposes
    public Block(Instant timestamp, List<T> data, String previousHash) {
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash;
    }

    public String calculateHash() {
        return DigestUtils.sha256Hex(timestamp + data.toString() + previousHash);
    }

    // Should this return a status of some sort? Not sure.
    public void addData(List<T> newDataList) {
        this.data.addAll(newDataList);
        this.hash = calculateHash();
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
}