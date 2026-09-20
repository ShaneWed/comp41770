package com.comp41770;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.util.List;

public class Block {
    private final Instant timestamp;
    // Should data just be transactions? Probably.
    // Or could we use generics instead? That would be cool.
    private final List<Transaction> data;
    private String previousHash = "";
    private String hash;

    public Block(List<Transaction> data) {
        this.timestamp = Instant.now();
        this.data = data;
        this.hash = calculateHash();
    }

    public Block(List<Transaction> data, String previousHash) {
        this.timestamp = Instant.now();
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return DigestUtils.sha256Hex(timestamp + data.toString() + previousHash);
    }

    // Should this return status?
    public void addData(List<Transaction> newDataList) {
        this.data.addAll(newDataList);
        this.hash = calculateHash();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }
}