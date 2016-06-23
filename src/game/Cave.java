/*
 * FILE: 	Cave.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Game container object, stores all parties
 */

package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JPanel;

public class Cave {
	// assign private instance variables to be populated after creation 
	private ArrayList<Party> parties = new ArrayList<Party>();
	private ArrayList<Treasure> undiscoveredTreasure = new ArrayList<Treasure>();
	private ArrayList<Artifact> undiscoveredArtifacts = new ArrayList<Artifact>();
	private ArrayList<Creature> detatchedCreatures = new ArrayList<Creature>();
	private ArrayList<Job> jobs = new ArrayList<Job>();
		
	private JPanel jobPanel = null;
	
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
	Cave(Scanner sfin, JPanel myJobPanel) {

		jobPanel = myJobPanel;
		// read attributes from file
		readFile(sfin);

		// close file
		sfin.close();


	}

	// Read in lines of data file, add cave elements in the process
	void readFile(Scanner sf) {
		// create hashmap of cave elements for efficient creation of multi-tree structure
		HashMap<Integer, CaveElement> caveElements = new HashMap<Integer, CaveElement>();
		Scanner line;
		String strLine;
		while (sf.hasNext()) {
			strLine = sf.nextLine().trim();
			System.out.println(strLine);
			if (strLine.length() == 0) continue;
			if (strLine.contains("\\")) continue;
			line = new Scanner (strLine).useDelimiter ("\\s*:\\s*");

			//switch based off of the incoming parameters
			switch(strLine.charAt(0)) {
			case 'p' : 
				Party p = addParty(line); 
				caveElements.put(p.index, p);
				break;
			case 'c' : 
				Creature c = addCreature(caveElements, line);
				caveElements.put(c.index, c);
				break;
			case 't' : 
				addTreasure(caveElements, line);
				break;
			case 'a' : 
				addArtifact(caveElements, line);
				break;
			case 'j' : 
				addJob(caveElements, line);
				break; // jobs not yet supported
			}
		}
	}

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

	public ArrayList<Creature> getDetechedCreatures() {
		return detatchedCreatures;
	}
	
	public ArrayList<Job> getJobs() {
		return jobs;
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

	// add detached creature
	public void addDetachedCreature(Creature detatched) {
		detatchedCreatures.add(detatched);
	}

	// create new party, assign to cave
	// p:<index>:<name>
	public Party addParty(Scanner thisItem) {

		// Create new party and add it to the cave
		Party myParty = new Party(thisItem);
		addParty(myParty);

		// return party to be added to HashMap
		return myParty;
	}

	// create new creature assign to party
	// c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
	public Creature addCreature(HashMap<Integer, CaveElement> theseElements, Scanner thisItem) {

		// create creature from attributes
		Creature myCreature = new Creature(thisItem);

		// find party within HashMap from created creature
		Party myParty = (Party) theseElements.get(myCreature.getPartyIndex());

		// add creature to cave or party
		if (myParty == null) {
			addDetachedCreature(myCreature);
		} else {
			myParty.addCreature(myCreature);
			myCreature.addParty(myParty);
		}

		// return creature to be added to HashMap
		return myCreature;

	}

	// create new treasure, assign appropriately
	// t:<index>:<type>:<creature>:<weight>:<value>
	public void addTreasure(HashMap<Integer, CaveElement> theseElements, Scanner thisItem) {

		// create treasure from attributes
		Treasure myTreasure = new Treasure(thisItem);

		// find creature within HashMap from created treasure
		Creature myCreature = (Creature) theseElements.get(myTreasure.getCreature());

		// add treasure to cave or creature
		if (myCreature == null) {
			addUndiscoveredTreasure(myTreasure);
		} else {
			myCreature.addTreasure(myTreasure);
		}

	}

	// create new artifact, assign appropriately
	// a:<index>:<type>:<creature>[:<name>]
	public void addArtifact(HashMap<Integer, CaveElement> theseElements, Scanner thisItem) {

		// create artifact from attributes
		Artifact myArtifact = new Artifact(thisItem);

		// find creature within HashMap from created artifact
		Creature myCreature = (Creature) theseElements.get(myArtifact.getCreature());

		if (myCreature == null) {
			addUndiscoveredArtifact(myArtifact);
		} else {
			myCreature.addArtifact(myArtifact);
		}

	}

	// j:<index>:<name>:<creature index>:<time>:[:<required artifact type>:<number>]
	public void addJob(HashMap<Integer, CaveElement> theseElements, Scanner thisItem) {
		Job myJob = new Job(theseElements, jobPanel, thisItem);
		jobs.add(myJob);
	}
}
