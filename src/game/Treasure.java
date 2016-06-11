/*
 * FILE: 	Treasure.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage of treasure
 */

package game;

import java.util.Scanner;

public class Treasure extends CaveElement {
	// define internal private variables
	private String type;
	private int creature;
	private double weight;
	private int value;
	
	// constructor
	// requires index and type
	Treasure(Scanner itemScanner) {
		itemScanner.next();
		this.index = itemScanner.nextInt();
		this.type = itemScanner.next();
		this.creature = itemScanner.nextInt();
		this.weight = itemScanner.nextDouble();
		this.value = itemScanner.nextInt();
	}
	
	// get the creature who owns this treasure
	public int getCreature() {
		return creature;
	}
	
	
	// get type
	public String getType() {
		return type;
	}
	
	// return formatted string for overall cave display
	public String toString() {
		return "          " + type + " - weight:" + 
				String.valueOf(weight) + ", value: " + String.valueOf(value) + "\n";
	}

	public double getWeight() {
		return weight;
	}

	public int getValue() {
		return value;
	}
}
