package com.comp41770;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.util.ArrayList;

public class Block {
    private final Instant timestamp;
    // Should data just be transactions? Probably.
    private final ArrayList<String> data;
    private String previousHash = "";
    private String hash;

    public Block(ArrayList<String> data) {
        this.timestamp = Instant.now();
        this.data = data;
        this.hash = calculateHash();
    }

    public Block(ArrayList<String> data, String previousHash) {
        this.timestamp = Instant.now();
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return DigestUtils.sha256Hex(timestamp + data.toString() + previousHash);
    }

    // Should this return status?
    public void addData(ArrayList<String> newDataList) {
        this.data.addAll(newDataList);
        this.hash = calculateHash();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }
}