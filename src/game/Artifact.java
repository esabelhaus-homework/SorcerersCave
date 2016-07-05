/*
 * FILE: 	Artifact.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage of artifact
 */

package game;

import java.util.Scanner;

public class Artifact extends CaveElement {
	// define internal private variables
	private String type;
	private int creature;
	private String name;
	// initialized unlocked
	private ItemLock nessy = new ItemLock(1);
	
	// constructor
	// requires index, type, creature, and name
	Artifact(Scanner itemScanner) {
		itemScanner.next();
		this.index = itemScanner.nextInt();
		this.type = itemScanner.next();
		this.creature = itemScanner.nextInt();
		this.name = itemScanner.next();
	}
	
	// check if locked currently
	public boolean isLocked() {
		return nessy.isLocked();
	}
	
	// set itemlock to locked
	public void lock() throws InterruptedException {
		nessy.waitForUnlock();
	}
	
	// set itemlock to unlocked
	public void unlock() {
		nessy.notifyToUnlock();
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
