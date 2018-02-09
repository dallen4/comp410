package osdi.locks;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Grad students only - re-write this in terms of osdi.locks.SpinLock
 */
/*
 * Grad students only - you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class ReaderWriterLock {
	private final SpinLock readLock = new SpinLock();
	private final SpinLock writeLock = new SpinLock();
	private Boolean writing = false;
	
//    private final java.util.concurrent.locks.ReadWriteLock javaRwLock = new ReentrantReadWriteLock(false);

    public void EnterRead() {
    		readLock.lock();
    		while (writing) {
    			readLock.unlock();
    			readLock.lock();
    		}
    		readLock.unlock();
    	
//        javaRwLock.readLock().lock();
    }

    public void ExitRead() {
    		readLock.unlock();
    	
//        javaRwLock.readLock().unlock();
    }

    public void EnterWrite() {
    		writeLock.lock();
    		while(writing) {
    			writeLock.unlock();
    			writeLock.lock();
    		}
    		writing = true;
    	
//        javaRwLock.writeLock().lock();
    }

    public void ExitWrite() {
    		writing = false;
    		writeLock.unlock();
    	
//        javaRwLock.writeLock().unlock();
    }
}
