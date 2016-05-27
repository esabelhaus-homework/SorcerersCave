/*
 * FILE: 	Creature.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of artifacts and treasures which creature has
 */

package game;

import java.util.ArrayList;

public class Creature {
	// assign private instance variables to be populated
	private int index;
	private String name;
	private String type;
	private int party;
	private int empathy;
	private int fearValue;
	private int carryingCapacity;
	private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
	private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
	
	// Constructor
	// requires index, type, name, party index, empathy value, fear value, and carrying capacity
	// values can be null aside from index, type, name, and party
	Creature(int myIndex, String myType, String myName, int myParty, int e, int fv, int cc) {
		this.index = myIndex;
		this.type = myType;
		this.name = myName;
		this.party = myParty;
		this.empathy = e;
		this.fearValue = fv;
		this.carryingCapacity = cc;
	}
	
	// get party index
	public int getParty() {
		return party;
	}
	
	// get index
	public int getIndex() {
		return index;
	}
	
	// get name
	public String getName() {
		return name;
	}
	
	// get type
	public String getType() {
		return type;
	}
	
	// expose treasures without exposing private object
	public ArrayList<Treasure> getTreasures() {
		return treasures;
	}
	
	// expose artifacts without exposing private object
	public ArrayList<Artifact> getArtifacts() {
		return artifacts;
	}
	
	// helper method to get a single artifact be index
	public Artifact getArtifactByIndex(int myIndex) {
		for(Artifact artifact: artifacts){
			if (myIndex == artifact.getIndex()) {
				return artifact;
			}
		}
		return null;
	}
	
	// helper method to return single artifact by name
	public Artifact getArtifactByName(String myName) {
		for(Artifact artifact: artifacts){
			if (artifact.getName().equals(myName)) {
				return artifact;
			}
		}
		return null;
	}
	
	// helper method to return all artifacts of a type carried
	// by creature
	public String getArtifactsByType(String myType) {
		String myArtifacts = "";
		for(Artifact artifact: artifacts){
			if (artifact.getType().equals(myType)) {
				myArtifacts += artifact.toString();
			}
		}
		return myArtifacts;
	}
	
	// helper method to get a single treasure item by index
	public Treasure getTreasureByIndex(int myIndex) {
		for(Treasure treasure: treasures){
			if (myIndex == treasure.getIndex()) {
				return treasure;
			}
		}
		return null;
	}
	
	// helper method to get all treasures by type carried
	// by creature
	public String getTreasureByType(String myType) {
		String myTreasure = "";
		for(Treasure treasure: treasures){
			if (treasure.getType() == myType) {
				myTreasure += treasure.toString();
			}
		}
		System.out.println(myTreasure.toString());
		return myTreasure;
	}
	
	// add treasure to creature
	public void addTreasure(Treasure myTreasure) {
		treasures.add(myTreasure);
	}
	
	// add artifact to creature
	public void addArtifact(Artifact myArtifact) {
		artifacts.add(myArtifact);
	}
	
	// return creature string broken by new lines
	// and space delimited
	public String toString() {
		String creatureString = "  Creature:" + 
			"\n    Features:\n      Name - " + name + 
			"\n      Type: " + type + 
		    "\n      Empathy - " + String.valueOf(empathy) + 
		    "\n      Fear value - " + String.valueOf(fearValue) + 
		    "\n      Carrying capacity - " + carryingCapacity + 
		    "\n    Treasures:\n";
		for(Treasure treasure: treasures){
			creatureString += treasure.toString();
		}
		creatureString += "    Artifacts:\n";
		for(Artifact artifact: artifacts){
			creatureString += artifact.toString();
		}
		return creatureString;
	}
}
