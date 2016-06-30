/*
 * FILE: 	ItemLock.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	This class will allow a thread to acquire and release locks as required
 * 
 */
package game;

public class ItemLock {

	private boolean locked = false;

	ItemLock(int initial) {
		locked = (initial == 0);
	}
	
	public synchronized void waitForUnlock() throws InterruptedException {
		while (locked) {
			wait();
		}
		locked = true;
	}

	public synchronized void notifyToUnlock() {
		if (locked) {
			notify();
		}
		locked = false;
	}
	
}