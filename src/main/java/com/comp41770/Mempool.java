package com.comp41770;

import java.util.concurrent.PriorityBlockingQueue;

public class Mempool<T> {
    private final PriorityBlockingQueue<T> queue;

    public Mempool(PriorityBlockingQueue<T> queue) {
        this.queue = queue;
    }

    public void addToMempool(T item) {
        queue.add(item);
    }

    public T popFromMempool() {
        return queue.poll();
    }
}
