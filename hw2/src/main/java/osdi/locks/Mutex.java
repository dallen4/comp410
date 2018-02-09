package osdi.locks;

/*
 * Grad students only - re-write this in terms of osdi.locks.SpinLock
 */
/*
 * Grad students only - you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class Mutex {
	private final SpinLock lock;
	
	
//    private final java.util.concurrent.locks.ReentrantLock javaLock;

    public Mutex() {
    		lock = new SpinLock();
        
    		
//    		javaLock = new java.util.concurrent.locks.ReentrantLock(false);
    }

    public void lock() {
    		lock.lock();
    		
    		
//        javaLock.lock();
    }

    public void unlock() {
    		lock.unlock();
    		
    		
//        javaLock.unlock();
    }
}
