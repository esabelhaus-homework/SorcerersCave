/*
 * FILE: 	Party.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of creatures within party
 */

package game;

import java.util.ArrayList;

public class Party {
	private int index;
	private String name;	
	private ArrayList<Creature> creatures = new ArrayList<Creature>();

	Party(int myIndex, String myName) {
		this.index = myIndex;
		this.name = myName;
	}
	
	public Creature getCreatureByIndex(int myIndex) {
		Creature myCreature = null;
		for (Creature creature: creatures) {
			if (myIndex == creature.getIndex()) {
				myCreature = creature;
			}
		}
		return myCreature;
	}
	
	public void addCreature(Creature myCreature) {
		creatures.add(myCreature);
	}
	
	public ArrayList<Creature> getCreatures() {
		return creatures;
	}
	
	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		String partyString = " Party: " + name + "\n";
		for(Creature creature: creatures){
			partyString += creature.toString();
		}
		return partyString;
	}
}
