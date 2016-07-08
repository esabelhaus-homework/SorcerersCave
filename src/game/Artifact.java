/*
 * FILE: 	Artifact.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage of artifact
 */

package game;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Artifact extends CaveElement {
	// define internal private variables
	private String type;
	private int creature;
	private String name;
	// initialized unlocked
	private Semaphore mutex;
	
	// constructor
	// requires index, type, creature, and name
	Artifact(Scanner itemScanner) {
		itemScanner.next();
		this.index = itemScanner.nextInt();
		this.type = itemScanner.next();
		this.creature = itemScanner.nextInt();
		this.name = itemScanner.next();
		this.mutex = new Semaphore(1);
	}
	
	public boolean state() {
		return mutex.tryAcquire();
	}
	
	// set itemlock to locked
	public void acquire() throws InterruptedException {
		System.out.println("acquire " + name);
		mutex.acquire();
	}
	
	// set itemlock to unlocked
	public void release() {
		System.out.println("release " + name);
		mutex.release();
	}
	
	// get creature who owns this treasure
	public int getCreature() {
		return creature;
	}
	
	
	// get name
	public String getName() {
		return name;
	}
	
	// get type
	public String getType() {
		return type;
	}
	
	// return formatted string for overall cave display
	public String toString() {
		return "          " + type + " - " + String.valueOf(name) + "\n";
	}
}
