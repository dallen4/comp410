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
    private final Semaphore empty, full, emptyFlag;

    public BoundBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        empty = new Semaphore(bufferSize);
        full = new Semaphore(0);
        emptyFlag = new Semaphore(1);
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
        if (queue.size() == 1)
        		emptyFlag.down();
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
        if (queue.size() == 0)
        		emptyFlag.up();
        // release readerLock
        lock.ExitWrite();
        // increment empty
        empty.up();
        return item;
    }
    
    @Override
    public Boolean empty() {
    		// add testing functions
    		Boolean empty;
    		// acquire readerLock
    		lock.EnterRead();
    		empty = queue.isEmpty();
    		// release readerLock
    		lock.ExitRead();
    		return empty;
    }
    
    @Override
    public int size() {
    		// add testing functions
    		int size;
    		// acquire readerLock
    		lock.EnterRead();
    		size = queue.size();
    		// release readerLock
    		lock.ExitRead();
    		return size;
    }
    
    @Override
    public Semaphore getEmptyFlag() {
    		return emptyFlag;
    }
}
