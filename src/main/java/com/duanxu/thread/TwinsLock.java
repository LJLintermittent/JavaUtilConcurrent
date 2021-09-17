package com.duanxu.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Description:
 * date: 2021/9/17 15:56
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class TwinsLock implements Lock {

    public TwinsLock(int count) {
        sync = new Sync(count);
    }

    private final Sync sync;

    private static final class Sync extends AbstractQueuedSynchronizer {

        Sync(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("count must larger than zero");
            }
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int reduceCount) {
            while (true) {
                int CourrentCount = getState();
                int newCount = CourrentCount - reduceCount;
                if (newCount < 0 || compareAndSetState(CourrentCount, newCount)) {
                    return newCount;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int returnCount) {
            while (true) {
                int correntCount = getState();
                int newCount = correntCount + returnCount;
                if (compareAndSetState(correntCount, newCount)) {
                    return true;
                }
            }
        }
    }

    @Override
    public void lock() {
        sync.tryAcquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.tryReleaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
