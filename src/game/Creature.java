/*
 * FILE: 	Creature.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Storage and retrieval of artifacts and treasures which creature has
 */

package game;

import java.util.ArrayList;
import java.util.Scanner;

public class Creature extends CaveElement {
	// assign private instance variables to be populated
	private String name;
	private String type;
	private int age;
	private int height;
	private int weight;
	private int party;
	private double empathy;
	private double fearValue;
	private double carryingCapacity;
	private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
	private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
	
	// Constructor
	// requires index, type, name, party index, empathy value, fear value, and carrying capacity
	// values can be null aside from index, type, name, and party
	// values for age, height, and weight will be added after creation through GUI
	Creature(Scanner itemScanner) {
		itemScanner.next();
		this.index = itemScanner.nextInt();
		this.type = itemScanner.next();
		this.name = itemScanner.next();
		this.party = itemScanner.nextInt();
		this.empathy = itemScanner.nextDouble();
		this.fearValue = itemScanner.nextDouble();
		this.carryingCapacity = itemScanner.nextDouble();
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
	
	//get fear value
	public double getFear() {
		return fearValue;
	}

	// get empathy
	public double getEmpathy() {
		return empathy;
	}

	// get carrying capacity
	public double getCarryingCapacity() {
		return carryingCapacity;
	}

	// get age
	public int getAge() {
		return age;
	}
	
	// get age
	public int getHeight() {
		return height;
	}
		
	// get age
	public int getWeight() {
		return weight;
	}
	
	// set age
	public void setAge(int myAge) {
		this.age = myAge;
	}
	
	// set height
	public void setHeight(int myHeight) {
		this.height = myHeight;
	}
		
	// set weight
	public void setWeight(int myWeight) {
		this.weight = myWeight;
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
			if (treasure.getType().equals(myType)) {
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
			"\n    Features: " + 
			"\n      Name: " + name +
			"\n      Age: " + String.valueOf(age) +
			"\n      Height: " + String.valueOf(height) +
			"\n      Weight: " + String.valueOf(weight) +
			"\n      Type: " + type + 
		    "\n      Empathy: " + String.valueOf(empathy) + 
		    "\n      Fear value: " + String.valueOf(fearValue) + 
		    "\n      Carrying capacity: " + carryingCapacity + 
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
