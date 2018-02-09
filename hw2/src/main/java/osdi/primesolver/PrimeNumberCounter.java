package osdi.primesolver;

import osdi.collections.BoundBuffer;
import osdi.collections.SimpleQueue;
import osdi.locks.Semaphore;

import java.util.ArrayList;
import java.util.Iterator;
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
    private void startThreads(SimpleQueue<ArrayList<Long>> valuesToCheck, SimpleQueue<ArrayList<Long>> valuesThatArePrime, Semaphore operationsCompleted, Semaphore valuesCounted) {
        Collection<Thread> threads = new ArrayList<>();
        int threadCount = getThreadCount();
        for(int i = 0; i < threadCount; i++) {
            Thread t = new Thread(()->findPrimeValues(valuesToCheck, valuesThatArePrime, operationsCompleted, valuesCounted));
            t.setDaemon(true);
            threads.add(t);
        }
        Thread counter = new Thread(()->countPrimeValues(valuesThatArePrime, valuesCounted));
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
        SimpleQueue<ArrayList<Long>> valuesToCheck = BoundBuffer.createBoundBufferWithSemaphores(7000);
        SimpleQueue<ArrayList<Long>> valuesThatArePrime = BoundBuffer.createBoundBufferWithSemaphores(425);
        
        // calculates number of values to be tested
        int operationsToComplete = (int)((range.getMaxValue() - range.getMinValue()) *-1);
        // declares grain of work (i.e., size of arrays stored in each queue index)
        int grainSize = 4275;
        
        // ensures all values have completed isPrime() calculation
        Semaphore operationsCompleted = new Semaphore(operationsToComplete);
        // ensures all values have been accounted for (both prime and not prime)
        Semaphore valuesCounted = new Semaphore(operationsToComplete);
        
        startThreads(valuesToCheck, valuesThatArePrime, operationsCompleted, valuesCounted);
        
        ArrayList<Long> temp = new ArrayList<Long>(grainSize);

        for(Long value : range) {
        		if(temp.size() == grainSize) {
        			valuesToCheck.enqueue(temp);
        			temp = new ArrayList<Long>(grainSize);
        			temp.add(value);
        			continue;
        		}
        		temp.add(value);	
        }
        
        if (!temp.isEmpty()) valuesToCheck.enqueue(temp);
        		
        // Conditions to meet before returning value: valuesToCheck.empty() && valuesThatArePrime.empty() && operationsCompleted >= 1
        while(true) {
        		// try to do a down (must be >= 1)
        		operationsCompleted.down();
        		// if down can be completed, test if all numbers have been accounted for
        		valuesCounted.down();
        		
        		return currentCount;
        }
    }

    /*
     * you may modify this method
     */
    private void findPrimeValues(SimpleQueue<ArrayList<Long>> valuesToCheck, SimpleQueue<ArrayList<Long>> valuesThatArePrime, Semaphore operationsCompleted, Semaphore valuesCounted) {
    		    		
        while(true) {
        		// secured by ReaderWriterLock in BoundBufferImpl
            ArrayList<Long> current = valuesToCheck.dequeue();
            Iterator<Long> i = current.iterator();
            
            ArrayList<Long> temp = new ArrayList<Long>();
            
            // proceed while array has more values
            while (i.hasNext()) {
            		// grab current value
            		Long currValue = i.next();
	            if(Number.IsPrime(currValue)) {
		        		// add value to temp array
		            	temp.add(currValue);
	            } else {
	            		// up on valuesCounted because value won't be counted by countPrimeValues() (moves closer to 1)
	            		valuesCounted.up();
	            }
	            // up on operationsCompleted (moving closer to 1)
	            operationsCompleted.up();
            }
            // secured by ReaderWriterLock in BoundBufferImpl
			valuesThatArePrime.enqueue(temp);
        }
    }

    /*
     * you may modify this method
     */
    private void countPrimeValues(SimpleQueue<ArrayList<Long>> valuesThatArePrime, Semaphore valuesCounted) {
    	    	    		
        while(true) {
			// critical section
        		// secured by ReaderWriterLock in BoundBufferImpl
			ArrayList<Long> temp = valuesThatArePrime.dequeue();
			Iterator<Long> i = temp.iterator();
			
			while (i.hasNext()) {
				// pull current value
				i.next();
				currentCount += 1;
				// up on valuesCounted
				valuesCounted.up();
				// end critical
	            
	            	if(currentCount % 10000 == 0) {
	            		System.out.println("have " + currentCount + " prime values");
	            		System.out.flush();
	            	}
			}
        }
    }
}
