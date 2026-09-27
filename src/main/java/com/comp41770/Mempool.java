package com.comp41770;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.PriorityBlockingQueue;

public class Mempool<T> {
    private final PriorityBlockingQueue<T> queue;

    public Mempool(Comparator<T> comparator) {
        this.queue = new PriorityBlockingQueue<>(11, Objects.requireNonNull(comparator));
    }

    public void addToMempool(T item) {
        queue.add(Objects.requireNonNull(item));
    }

    public Optional<T> popFromMempool() {
        return Optional.ofNullable(queue.poll());
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
