/*
 * FILE: 	Cave.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Game container object, stores all parties
 */

package game;

import java.util.ArrayList;

public class Cave {
	private ArrayList<Party> parties = new ArrayList<Party>();
	private ArrayList<Treasure> undiscoveredTreasure = new ArrayList<Treasure>();
	private ArrayList<Artifact> undiscoveredArtifacts = new ArrayList<Artifact>();

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
	
	Cave() { }
	
	public ArrayList<Party> getParties() {
		return parties;
	}
	
	public ArrayList<Artifact> getUndiscoveredArtifacts() {
		return undiscoveredArtifacts;
	}
	
	public ArrayList<Treasure> getUndiscoveredTreasure() {
		return undiscoveredTreasure;
	}
	
	public Party getPartyByIndex(int myIndex) {
		Party myParty = null;
		for (Party party: parties) {
			if (myIndex == party.getIndex()) {
				myParty = party;
			}
		}
		return myParty;
	}
	
	public void addParty(Party myParty) {
		parties.add(myParty);
	}
	
	public void addUndiscoveredTreasure(Treasure hidden) {
		undiscoveredTreasure.add(hidden);
	}
	
	public void addUndiscoveredArtifact(Artifact hidden) {
		undiscoveredArtifacts.add(hidden);
	}
}
