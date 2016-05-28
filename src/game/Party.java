/*
 * FILE: 	Party.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of creatures within party
 */

package game;

import java.util.ArrayList;

public class Party {
	// assign private instance variables to be populated after creation
	private int index;
	private String name;	
	private ArrayList<Creature> creatures = new ArrayList<Creature>();

	// constructor, requires index and name
	Party(int myIndex, String myName) {
		this.index = myIndex;
		this.name = myName;
	}
	
	// helper method to get creature by index
	public Creature getCreatureByIndex(int myIndex) {
		for (Creature creature: creatures) {
			if (myIndex == creature.getIndex()) {
				return creature;
			}
		}
		return null;
	}
	
	// add creature to party
	public void addCreature(Creature myCreature) {
		creatures.add(myCreature);
	}
	
	// get creatures without exposing private object
	public ArrayList<Creature> getCreatures() {
		return creatures;
	}
	
	// get party name
	public String getName() {
		return name;
	}
	
	// get party index
	public int getIndex() {
		return index;
	}
	
	// return party string broken by new lines
	// and space delimited
	public String toString() {
		String partyString = " Party: " + name + "\n";
		for(Creature creature: creatures){
			partyString += creature.toString();
		}
		return partyString;
	}
}
