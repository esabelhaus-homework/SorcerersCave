/*
 * FILE: 	Cave.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Game container object, stores all parties
 */

package game;

import java.util.ArrayList;

public class Cave {
	// assign private instance variables to be populated after creation 
	private ArrayList<Party> parties = new ArrayList<Party>();
	private ArrayList<Treasure> undiscoveredTreasure = new ArrayList<Treasure>();
	private ArrayList<Artifact> undiscoveredArtifacts = new ArrayList<Artifact>();

	// create string of the entire cave which is broken up by new lines
	// and space indented
	public String toString() {
		String caveString = "Sorcerers Cave:\n";
		for (Party party: parties) {
			caveString += party.toString();
		}
		
		caveString += "  Hidden Items:\n";
		
		for (Treasure treasure: undiscoveredTreasure) {
			caveString += treasure.toString();
		}
		
		for (Artifact artifact: undiscoveredArtifacts) {
			caveString += artifact.toString();
		}
		return caveString;
	}
	
	// constructor. no need to initialize anything as it is a simple container
	Cave() { }
	
	// return all parties without exposing private object
	public ArrayList<Party> getParties() {
		return parties;
	}
	
	// return all undiscovered artifacts without exposing private object
	public ArrayList<Artifact> getUndiscoveredArtifacts() {
		return undiscoveredArtifacts;
	}
	
	// return all undiscovered treasures without exposing private object
	public ArrayList<Treasure> getUndiscoveredTreasure() {
		return undiscoveredTreasure;
	}
	
	// helper method to find parties by index
	public Party getPartyByIndex(int myIndex) {
		for (Party party: parties) {
			if (myIndex == party.getIndex()) {
				return party;
			}
		}
		return null;
	}
	
	// add party to parties
	public void addParty(Party myParty) {
		parties.add(myParty);
	}
	
	// add undiscovered treasure
	public void addUndiscoveredTreasure(Treasure hidden) {
		undiscoveredTreasure.add(hidden);
	}
	
	// add undiscovered artifact
	public void addUndiscoveredArtifact(Artifact hidden) {
		undiscoveredArtifacts.add(hidden);
	}
}
