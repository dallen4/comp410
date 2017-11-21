package osdi.primesolver;

import osdi.collections.BoundBuffer;
import osdi.collections.SimpleQueue;
import osdi.locks.Mutex;
import osdi.locks.Semaphore;

import java.util.ArrayList;
import java.util.Collection;

/*
 * you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class PrimeNumberCounter {

    private long currentCount = 0L;

    /*
     * you may not modify this method
     */
    private static int getThreadCount() {
        return Runtime.getRuntime().availableProcessors() * 4;
    }

    /*
     * you may not modify the method, but you can modify the signature of the method if needed
     */
    private void startThreads(SimpleQueue<Long> valuesToCheck, SimpleQueue<Long> valuesThatArePrime, Semaphore operationsCompleted) {
        Collection<Thread> threads = new ArrayList<>();
        int threadCount = getThreadCount();
        for(int i = 0; i < threadCount; i++) {
            Thread t = new Thread(()->findPrimeValues(valuesToCheck, valuesThatArePrime, operationsCompleted));
            t.setDaemon(true);
            threads.add(t);
        }
        Thread counter = new Thread(()->countPrimeValues(valuesThatArePrime));
        threads.add(counter);

        for(Thread t : threads) {
            t.setDaemon(true);
            t.start();
        }
    }

    /*
     * you may modify this method
     */
    public long countPrimeNumbers(NumberRange range) {
        SimpleQueue<Long> valuesToCheck = BoundBuffer.createBoundBufferWithSemaphores(100);
        SimpleQueue<Long> valuesThatArePrime = BoundBuffer.createBoundBufferWithSemaphores(50);
        
        int operationsToComplete = (int)((range.getMaxValue() - range.getMinValue() - 2) *-1);
        Semaphore operationsCompleted = new Semaphore(operationsToComplete);
        
        startThreads(valuesToCheck, valuesThatArePrime, operationsCompleted);

        for(Long value : range) {
            valuesToCheck.enqueue(value);
        }
        // Conditions to meet before returning value:
//        valuesToCheck.empty() && valuesThatArePrime.empty() && operationsCompleted >= 1
        while(true) {
        		// try to do a down (must be >= 1)
//        		System.out.println(operationsCompleted.getCurrentValue());
        		operationsCompleted.down();
        		// if down can be completed, test queues
//        		valuesToCheck.getEmptyFlag().down();
        		valuesThatArePrime.getEmptyFlag().down();
        		
        		return currentCount;
        }
    }

    /*
     * you may modify this method
     */
    private void findPrimeValues(SimpleQueue<Long> valuesToCheck, SimpleQueue<Long> valuesThatArePrime, Semaphore operationsCompleted) {
    		    		
        while(true) {
        		// secured by ReaderWriterLock in BoundBufferImpl
            Long current = valuesToCheck.dequeue();
            
            if(Number.IsPrime(current)) {
            		// secured by ReaderWriterLock in BoundBufferImpl
                valuesThatArePrime.enqueue(current);
            }
            // up on operationsCompleted (moving closer to 1)
            operationsCompleted.up();
        }
    }

    /*
     * you may modify this method
     */
    private void countPrimeValues(SimpleQueue<Long> valuesThatArePrime) {
    	    	
    		Mutex m = new Mutex();
    		
        while(true) {
			// critical section
        		m.lock();
        		// secured by ReaderWriterLock in BoundBufferImpl
			valuesThatArePrime.dequeue();
			currentCount += 1;
			m.unlock();
			// end critical
            
            if(currentCount % 10000 == 0) {
                System.out.println("have " + currentCount + " prime values");
                System.out.flush();
            }
        }
    }
}
