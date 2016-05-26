/*
 * FILE: 	Creature.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of artifacts and treasures which creature has
 */

package game;

import java.util.ArrayList;

public class Creature {
	private int index;
	private String name;
	private String type;
	private int party;
	private int empathy;
	private int fearValue;
	private int carryingCapacity;
	
	ArrayList<Treasure> treasures = new ArrayList<Treasure>();
	ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
	
	Creature(int myIndex,String myType, String myName, int myParty, int e, int fv, int cc) {
		this.index = myIndex;
		this.type = myType;
		this.name = myName;
		this.party = myParty;
		this.empathy = e;
		this.fearValue = fv;
		this.carryingCapacity = cc;
	}
	
	public int getParty() {
		return party;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public ArrayList<Treasure> getTreasures() {
		return treasures;
	}
	
	public ArrayList<Artifact> getArtifacts() {
		return artifacts;
	}
	
	public Artifact getArtifactByIndex(int myIndex) {
		Artifact myArtifact = null;
		for(Artifact artifact: artifacts){
			if (artifact.getIndex() == myIndex) {
				myArtifact = artifact;
			}
		}
		return myArtifact;
	}
	
	public Artifact getArtifactByName(String myName) {
		Artifact myArtifact = null;
		for(Artifact artifact: artifacts){
			if (artifact.getName() == myName) {
				myArtifact = artifact;
			}
		}
		return myArtifact;
	}
	
	public String getArtifactsByType(String myType) {
		String myArtifacts = "";
		for(Artifact artifact: artifacts){
			if (artifact.getType() == myType) {
				myArtifacts += artifact.toString();
			}
		}
		return myArtifacts;
	}
	
	public Treasure getTreasureByIndex(int myIndex) {
		Treasure myTreasure = null;
		for(Treasure treasure: treasures){
			if (treasure.getIndex() == myIndex) {
				myTreasure = treasure;
			}
		}
		return myTreasure;
	}
	
	public String getTreasureByType(String myType) {
		String myTreasure = "";
		for(Treasure treasure: treasures){
			if (treasure.getType() == myType) {
				myTreasure += treasure.toString();
			}
		}
		return myTreasure;
	}
	
	public void addTreasure(Treasure myTreasure) {
		treasures.add(myTreasure);
	}
	
	public void addArtifact(Artifact myArtifact) {
		artifacts.add(myArtifact);
	}
	
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
