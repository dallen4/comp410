package osdi.collections;

import osdi.locks.*;

/*
 * Modify this as you see fit. you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
class BoundBufferImpl<T> implements SimpleQueue<T> {
    private final int bufferSize;
    private final java.util.Queue<T> queue;
    // implement ReaderWriterLock
    private final ReaderWriterLock lock;
    private final Semaphore empty, full;

    public BoundBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        empty = new Semaphore(bufferSize);
        full = new Semaphore(0);
        queue = new java.util.ArrayDeque<>();
        lock = new ReaderWriterLock();
    }

    @Override
    public void enqueue(T item) {
    		// decrement empty
    		empty.down();
    		// acquire writerLock 
    		lock.EnterWrite();
        queue.add(item);
        // release writerLock
        lock.ExitWrite();
        // increment full
        full.up();
    }

    @Override
    public T dequeue() {
    		// decrement full
    		full.down();
    		// acquire readerLock 
    		lock.EnterWrite();
        T item = queue.remove();
        // release readerLock
        lock.ExitWrite();
        // increment empty
        empty.up();
        return item;
    }
}
