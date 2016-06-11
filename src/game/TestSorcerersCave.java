package game;

import java.io.IOException;

import org.junit.Test;

public class TestSorcerersCave {
	
	private static GameGUI testGUI;
	
	public static void main(String args[]) throws IOException {
	
        testGUI = new GameGUI();
	
	}
	
	// Testing data
	//
	//  p:<index>:<name>
	//p : 10001 : Unity
	//  c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
	//c : 20001 : Woman   : Lucy   :10001 : 17 : 22 : 20
	//c : 20002 : Woman   : Jane   :10001 : 22 : 15 : 25
	//c : 20003 : Worg	: Brandon : 0     : 30 : 21 : 0
	//  t:<index>:<type>:<creature>:<weight>:<value>
	//t : 30001 : Gold : 20001 : 50 : 2000
	//t : 30002 : Gold :     0 : 75 : 5000
	//t : 30003 : Gems : 20002 : 10 : 10000
	//  a:<index>:<type>:<creature>[:<name>]
	//a : 40001 : Wand : 20001 : ElderWand
	
	@Test
	public void testingCave() {
		assert(testGUI.sorcerersCave.getClass() == new Cave().getClass());
		
		assert(testGUI.sorcerersCave.getUndiscoveredTreasure().get(0).getType().equals("Gold"));
		assert(testGUI.sorcerersCave.getUndiscoveredTreasure().get(0).getIndex() == 30002);
		for(Party party: testGUI.sorcerersCave.getParties()) {
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
			for(Creature creature: testGUI.sorcerersCave.getDetechedCreatures()) {
				if (creature.getIndex() == 20003) {
					assert(creature.getName().equals("Brandon"));
					assert(creature.getArtifacts().isEmpty());
					assert(creature.getTreasures().isEmpty());
				}
			}
		}
	}
}
