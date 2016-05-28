package game;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class TestSorcerersCave {
	
	public static void main(String args[]) throws IOException {
	
		String basePath = System.getProperty("user.dir");
		
		String filePath = basePath + "SmallSimpleCave.txt";
		
		FileInputStream fstream = new FileInputStream(filePath);
		
		// get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
		String strLine;
	
		//Read File Line By Line
		while ((strLine = br.readLine()) != null) {
		    //ignore blanks
		    if(!strLine.trim().equals("\n"))
		    {
		    	if (strLine.contains("/")) {
		    		// ignore comments
		    		continue;
		    	}
		    	String data[] = strLine.trim().split(":");
		    	// trim extra spaces for consistency sake
		    	for (int i = 0; i < data.length; i++){
		    	    data[i] = data[i].trim();
		    	}
		    	//switch based off of the incoming parameters
		    	switch(data[0]) {
		    		case "p": 
		    			GameGUI.addParty(data);
		    			break;
		    		case "c": 
		    			GameGUI.addCreature(data);
		    			break;
		    		case "t": 
		    			GameGUI.addTreasure(data);
		    			break;
		    		case "a": 
		    			GameGUI.addArtifact(data);
		    			break;
		    		case "j": 
		    			// jobs not yet supported
		    			break;
		    	}
		    }
		}
	}
	
	// Testing data
	//
	//  p:<index>:<name>
	//p : 10001 : Unity
	//  c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
	//c : 20001 : Woman   : Lucy   :10001 : 17 : 22 : 20
	//c : 20002 : Woman   : Jane   :10001 : 22 : 15 : 25
	//  t:<index>:<type>:<creature>:<weight>:<value>
	//t : 30001 : Gold : 20001 : 50 : 2000
	//t : 30002 : Gold :     0 : 75 : 5000
	//t : 30003 : Gems : 20002 : 10 : 10000
	//  a:<index>:<type>:<creature>[:<name>]
	//a : 40001 : Wand : 20001 : ElderWand
	
	@Test
	public void testingCave() {
		assert(GameGUI.sorcerersCave.getClass() == new Cave().getClass());
		
		assert(GameGUI.sorcerersCave.getUndiscoveredTreasure().get(0).getType().equals("Gold"));
		assert(GameGUI.sorcerersCave.getUndiscoveredTreasure().get(0).getIndex() == 30002);
		for(Party party: GameGUI.sorcerersCave.getParties()) {
			assert(party.getClass() == new Party(1, "foo").getClass());
			assert(party.getName().equals("Unity"));
			assert(party.getIndex() == 10001);
			
			for(Creature creature: party.getCreatures()){
				if(creature.getIndex() == 20001){
					assert(creature.getName().equals("Lucy"));
					for (Artifact artifact: creature.getArtifacts()){
						assert(artifact.getIndex() == 20001);
					}
					for (Treasure treasure: creature.getTreasures()){
						assert(treasure.getIndex() == 30001);
					}
				}
				if(creature.getIndex() == 20002){
					assert(creature.getName().equals("Jane"));
					assert(creature.getArtifacts().isEmpty());
					for (Treasure treasure: creature.getTreasures()){
						assert(treasure.getIndex() == 20001);
					}
				}
			}
		}
	}
}
