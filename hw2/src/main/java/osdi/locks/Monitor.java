package osdi.locks;

/*
 * Grad students only - re-write this in terms of osdi.locks.SpinLock
 */
/*
 * Grad students only - you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class Monitor {
	
	// private variables
	// ref: https://github.com/LoyolaChicagoBooks/operatingsystems-examples/blob/master/monitor/monitor.cc
	private SpinLock lock = new SpinLock();
	private int pulse = 0;

    public interface CodeToExecute {
        void run(MonitorOperations ops);
    }

    public interface MonitorOperations {
        void Wait();
        void Pulse();
    }

    private final MonitorOperations lockObj = new MonitorOperationsImpl();

    public void sync(CodeToExecute fn) {
    		lock.lock();
        fn.run(lockObj);
    }

    private class MonitorOperationsImpl implements MonitorOperations {
    		
        @Override
        public void Wait() {
        	
        		lock.unlock();
        		lock.lock();
        		
        		pulse = --pulse;
        	
//            try {
//                this.wait();
//            } catch (InterruptedException e) {
//            }
        }

        @Override
        public void Pulse() {
        	
        		lock.unlock();
        		pulse = ++pulse;
        	
//            this.notifyAll();
        }
    }
}
