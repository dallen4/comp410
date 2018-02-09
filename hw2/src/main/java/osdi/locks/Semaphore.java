package osdi.locks;

/*
 * Grad students only - re-write this in terms of osdi.locks.SpinLock
 */
/*
 * Grad students only - you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class Semaphore {
//    private final java.util.concurrent.Semaphore javaSemaphore;
    // values 0:N
    private volatile int value;
    private SpinLock lock;

    public Semaphore(int initialValue) {
        value = initialValue;
        lock = new SpinLock();
//    		javaSemaphore = new java.util.concurrent.Semaphore(initialValue);
    }

    // while(value == 0) block();
    // value--
    public void down() {
    		try {
    			lock.lock();
    			while(value <= 0) {
        			try {
        				lock.unlock();
        			} finally {
        				lock.lock();
        			}
        		}
    			--value;
    		} finally {
    			lock.unlock();
    		}
    		
//        try {
//            javaSemaphore.acquire(1);
//        } catch (InterruptedException e) {
//            throw new IllegalStateException(e);
//        }
    }

    // value++
    public void up() {
        
        try {
        		lock.lock();
        		++value;
        } finally {
        		lock.unlock();
        }
        
//        javaSemaphore.release(1);
    }
    
    // returns the current value held in the Semaphore
    public int getCurrentValue() {
    		return value;
//    		return javaSemaphore.availablePermits();
    }
}
