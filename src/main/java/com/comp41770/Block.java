package com.comp41770;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.util.List;

public class Block<T> {
    private final Instant timestamp;
    // Should data just be transactions? Probably.
    // Or could we use generics instead? That would be cool.
    private final List<T> data;
    private String previousHash = "";
    private String hash;

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