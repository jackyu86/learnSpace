package com.github.distribute.lock.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * ??????, ???????????????.
 *
 *
 */
public abstract class AbstractLock implements Lock {

	/**
	 * <pre>
	 * ???????????????, ????????,
	 * 1.???jvm????????????????????, ?????????????
	 * 2.???jvm?????????????, ???????????.
	 * </pre>
	 */
	protected volatile boolean locked;

	/**
	 * ??jvm????????(if have one)
	 */
	private Thread exclusiveOwnerThread;

	public void lock() {
		try {
			lock(false, 0, null, false);
		} catch (InterruptedException e) {
			// TODO ignore
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		lock(false, 0, null, true);
	}

	public boolean tryLock(long time, TimeUnit unit) {
		try {
			System.out.println("ghggggggggggggg");
			return lock(true, time, unit, false);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("" + e);
		}
		return false;
	}

	public boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException {
		return lock(true, time, unit, true);
	}

	public void unlock() {
		// TODO ???????????
		if (Thread.currentThread() != getExclusiveOwnerThread()) {
			throw new IllegalMonitorStateException("current thread does not hold the lock");
		}

		unlock0();
		setExclusiveOwnerThread(null);
	}

	protected void setExclusiveOwnerThread(Thread thread) {
		exclusiveOwnerThread = thread;
	}

	protected final Thread getExclusiveOwnerThread() {
		return exclusiveOwnerThread;
	}

	protected abstract void unlock0();

	/**
	 * ?????????
	 *
	 * @param useTimeout
	 * @param time
	 * @param unit
	 * @param interrupt
	 *            ??????
	 * @return
	 * @throws InterruptedException
	 */
	protected abstract boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt)
			throws InterruptedException;

}