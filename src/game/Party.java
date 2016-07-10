/*
 * FILE: 	Party.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of creatures within party
 */

package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Party extends CaveElement {
	// assign private instance variables to be populated after creation
	private String name;	
	// pool to keep count of what resources are left by type
	private HashMap <String, Integer> resourcePool = new HashMap <String, Integer>();
	// all of the actual resources
	private ArrayList<Artifact> resources = new ArrayList<Artifact>();
	// all of the creatures in the party
	private ArrayList<Creature> creatures = new ArrayList<Creature>();

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
	
	// add a resource when to the party, update the pool count
	public void addResource(Artifact resource) {	
		resources.add(resource);
		if(resourcePool.containsKey(resource.getType())) {
			resourcePool.put(resource.getType(), resourcePool.get(resource.getType()) +1);
		} else {
			resourcePool.put(resource.getType(),1);
		}
	}

	// how may of this resource does the party have left
	public synchronized int resourceAvailableCountByType(String type){
		int result;
		if (resourcePool.get(type) == null) result = 0;
		else result = resourcePool.get(type);
		return result;
	}

	// reserve resources passed in as hash map
	// return arraylist of acquired resources
	// adjust pool size accordingly
	public synchronized ArrayList<Artifact> reserveResources(HashMap<String, Integer> resourcesNeeded) {
		int current;
		ArrayList<Artifact> myResources = new ArrayList<Artifact>(); 
		for(HashMap.Entry<String, Integer> entry : resourcesNeeded.entrySet()) {	
			if(resourcePool.get(entry.getKey()) == null) {
				current = 0;
			} else {
				current = resourcePool.get(entry.getKey());
				for (int h = 0; h < entry.getValue(); h++) {
					for (int i = 0; i < resources.size(); i++) {
						if (resources.get(i).getType().equals(entry.getKey())) {
							myResources.add(resources.get(i));
							resources.get(i).setState("In Use");
							resources.remove(i);
							break;
						}
					}
				}	
			}
			resourcePool.replace(entry.getKey(), current - entry.getValue());
		}
		return myResources;
	}

	// readjust the resource pool, and resources
	// accodring to arguments passed in by the worker
	public synchronized void releaseResources(HashMap<String, Integer> resourcesNeeded, ArrayList<Artifact> runtimeResources){
		int current;
		for(HashMap.Entry<String, Integer> entry: resourcesNeeded.entrySet()){
			if(resourcePool.get(entry.getKey()) == null)
				current = 0;
			else
				current = resourcePool.get(entry.getKey());
			resourcePool.replace(entry.getKey(),current + entry.getValue());
		}
		for (Artifact artifact: runtimeResources) {
			artifact.setState("Available");
		}
		resources.addAll(runtimeResources);
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
