/*
 * FILE: 	Artifact.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage of artifact
 */

package game;

public class Artifact{
	// define internal private variables
	private int index;
	private String type;
	private int creature;
	private String name;
	
	// constructor
	Artifact(int myIndex, String myType, int myCreature, String myName) {
		this.index = myIndex;
		this.type = myType;
		this.creature = myCreature;
		this.name = myName;
	}
	
	// get creature who owns this treasure
	public int getCreature() {
		return creature;
	}
	
	// get index of artifact
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	// return formatted string for overall cave display
	public String toString() {
		return "          " + type + " - " + String.valueOf(name) + "\n";
	}
}
