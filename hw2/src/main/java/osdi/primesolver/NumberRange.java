package osdi.primesolver;

import java.util.Iterator;

/*
 * You may modify this as you see fit
 */
/*
 * you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class NumberRange implements Iterable<Long> {
    private final long maxValue;
    private final long startValue;

    public NumberRange(long startValue, long maxValue) {
        if(maxValue <= startValue) {
            throw new IllegalStateException("start value < max value");
        }
        this.startValue = startValue;
        this.maxValue = maxValue;
    }

    @Override
    public Iterator<Long> iterator() {
        return new NumberIterator(startValue, maxValue);
    }

    private static class NumberIterator implements Iterator<Long> {
        private final long maxValue;
        private long currentValue;

        private NumberIterator(long startValue, long maxValue) {
            this.maxValue = maxValue;
            this.currentValue = startValue;
        }

        @Override
        public boolean hasNext() {
            return currentValue <= maxValue;
        }

        @Override
        public Long next() {
            return (currentValue <= maxValue) ? currentValue++ : Long.MIN_VALUE;
        }
    }
    
    // returns maxValue of number range
    public long getMaxValue() {
    		return maxValue;
    }
    
    // returns startValue (minimum) of number range
    public long getMinValue() {
    		return startValue;
    }
}
