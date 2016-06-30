/*
 * FILE: 	Party.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of creatures within party
 */

package game;

import java.util.ArrayList;
import java.util.Scanner;

public class Party extends CaveElement {
	// assign private instance variables to be populated after creation
	private String name;	
	private ArrayList<Creature> creatures = new ArrayList<Creature>();
	private ArrayList<Artifact> resourcePool = new ArrayList<Artifact>();
	
	// constructor, requires index and name
	Party(Scanner itemScanner) {
		itemScanner.next();
		this.index = itemScanner.nextInt();
		this.name = itemScanner.next();
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
	
	public ArrayList<Artifact> getKnownResourceByType(String myType) {
		// TODO create array list of all available objects of a type
		ArrayList<Artifact> theseResources = new ArrayList<Artifact>();
		for (Artifact myArtifact: resourcePool) {
			if (myArtifact.getType().equals(myType)) {
				theseResources.add(myArtifact);
			}
		}
		return theseResources;
	}
	
	// add creature to party
	public void addCreature(Creature myCreature) {
		creatures.add(myCreature);
	}
	
	public void addResource(Artifact myResource) {
		resourcePool.add(myResource);
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
