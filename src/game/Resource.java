/*
 * FILE: 	Resource.java
 * DATE:  	07/01/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	This class is used to store simple resource data within a job
 * 
 */
package game;

public class Resource {
	private String type;
	private int needed;
	private int have = 0;
	
	// name of artifact (type), number needed, initialize resource as un-met
	public Resource(String myType, int howMany) {
		this.type = myType;
		this.needed = howMany;
	}
	
	// are the resources met
	public boolean haveAllResources() {
		return have == needed;
	}
	
	public void addedOne() {
		have++;
	}
	
	public void removeResource() {
		have--;
	}
	
	public int howManyNeeded() {
		return needed;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return type + " : " + needed;
	}
}
