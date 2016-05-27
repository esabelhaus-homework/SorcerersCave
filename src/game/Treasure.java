/*
 * FILE: 	Treasure.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage of treasure
 */

package game;

public class Treasure {
	// define internal private variables
	private int index;
	private String type;
	private int creature;
	private double weight;
	private int value;
	
	// constructor
	// requires index and type
	Treasure(int myIndex, String myType, int myCreature, double myWeight, int myValue) {
		this.index = myIndex;
		this.type = myType;
		this.creature = myCreature;
		this.weight = myWeight;
		this.value = myValue;
	}
	
	// get the creature who owns this treasure
	public int getCreature() {
		return creature;
	}
	
	// get the index of this treasure
	public int getIndex() {
		return index;
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
}
