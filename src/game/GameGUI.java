/*
 * FILE: 	GameGUI.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Read attribute file, populate objects, display game
 */
package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// GUI and driver to test objects
public class GameGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// Initiate GUI methods
	protected static JFrame frame;
	public static JRadioButton button = new JRadioButton();
	
	public static Cave sorcerersCave = new Cave();
	
	public static void main(String[] args) throws IOException {
		
		// instantiate file chooser in current directory
		JFileChooser chooser = new JFileChooser(System.getProperty("."));
		
		int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
            chooser.getSelectedFile().getName());
            try {
                Scanner sfin = new Scanner (chooser.getSelectedFile());
                while (sfin.hasNext()) {
                	String strLine = sfin.nextLine().trim();
                	System.out.println(strLine);
                	if(!strLine.equals("\n"))
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
        		    			addParty(data);
        		    			break;
        		    		case "c": 
        		    			addCreature(data);
        		    			break;
        		    		case "t": 
        		    			addTreasure(data);
        		    			break;
        		    		case "a": 
        		    			addArtifact(data);
        		    			break;
        		    		case "j": 
        		    			// jobs not yet supported
        		    			break;
        		    	}
        		    }
                }
                
                sfin.close();
            }
            catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "File not found.");
             } // end try/catch
        } // end if file chosen

		// Button and search text for search by field in menu bar
		final JButton searchButton = new JButton("  Search By...  ");
		JTextField searchText = new JTextField();
		
	    // Picker box for type of search
		final JComboBox<?> searchPickerBox = new JComboBox<Object>( new Object[]{
			"name",
			"type", 
			"index"}
	    );
		
//		// Button and search text for search by field in menu bar
//		final JButton sortButton = new JButton("  Sort By...  ");
//		
//	    // Picker box for type of search
//		final JComboBox<?> sortPickerBox = new JComboBox<Object>( new Object[]{
//			"name",
//			"type", 
//			"index"}
//	    );
		
		// action listener on search button
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String searchString = searchText.getText();
				String searchResult = "";
				if ("".equals(searchString)) {
					JOptionPane.showMessageDialog(frame, "YOU MUST SPECIFY A SEARCH STRING");
					return;
				}
				// Switch on search type based off picker box
				System.out.println(searchPickerBox.getSelectedItem().toString());
				String myType = searchPickerBox.getSelectedItem().toString();
				
				if (myType == "name") {
				    System.out.println("name");
				    searchResult = getCaveItemByName(searchString);
				}
				
				if (myType == "type") {
					System.out.println("type");
			    	searchResult = getCaveItemByType(searchString);
				}
				
				if (myType == "index") {
				   	System.out.println("index");
				   	int myIndex = Integer.parseInt(searchString);
				   	if ( (myIndex < 10000) || (myIndex > 60000) ){
				   		System.out.println("index out of bounds");
				   		JOptionPane.showMessageDialog(frame, "Index must be between 10000 and 59999");
				   		return;
				   	}
				   	searchResult = getCaveItemByIndex(myIndex);
		        }
				
				System.out.println(searchResult);
				
				JOptionPane.showMessageDialog(frame, searchResult);
				return;
			}
		});
		
		
		
		// Menu bar to tie the whole search feature together
		JMenuBar menuBar = new JMenuBar();
        menuBar.add(searchButton);
        menuBar.add(searchPickerBox);
        menuBar.add(searchText);
        menuBar.add(Box.createHorizontalGlue());
		
		//Create and set up the window.
        JFrame frame = new JFrame("Sorcerers Cave");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(new GameGUI(sorcerersCave), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
	}
	
	// Public constructor, used to build out visual elements for inspecting each party in the cave
	// as well as viewing undiscovered treasure if there is any
	public GameGUI(Cave myCave) {		
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        ImageIcon icon = createImageIcon("img/Elder_wand.png");
        
        //Iterate over every party, detached creature and undiscovered item
        for (Party myParty: myCave.getParties()) {
        	JComponent panel = makePartyPanel(myParty.toString());
        	tabbedPane.addTab(myParty.getName(), icon, panel);
        }
        
        JComponent detached = makeDetachedCreaturePanel(myCave);
        tabbedPane.addTab("Wandering Creatures", icon, detached);
        
        JComponent undiscovered = makeUndiscoveredPanel(myCave);
        tabbedPane.addTab("Undiscovered Items", icon, undiscovered);
        
        setLayout (new BorderLayout ());
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(800,400));
        validate ();
    }
	
	// insert all undiscovered items into a non editable text panel
	private JComponent makeUndiscoveredPanel(Cave thisCave) {
		String undiscovered = "";
		for (Artifact myArtifact: thisCave.getUndiscoveredArtifacts()) {
			undiscovered += myArtifact.toString();
		}
		
		for (Treasure myTreasure: thisCave.getUndiscoveredTreasure()){
			undiscovered += myTreasure;
		}
		
		JTextArea uPanel = new JTextArea(20,20);
		uPanel.setWrapStyleWord(true);
		uPanel.setLineWrap(true);
		uPanel.setEditable(false);
		uPanel.setText(undiscovered);
	    JScrollPane jsp = new JScrollPane (uPanel);
		return jsp;
	}
	
	// insert all undiscovered items into a non editable text panel
	private JComponent makeDetachedCreaturePanel(Cave thisCave) {
		String detached = "";
		for (Creature myCreature: thisCave.getDetechedCreatures()) {
			detached += myCreature.toString();
		}
		
		JTextArea uPanel = new JTextArea(20,20);
		uPanel.setWrapStyleWord(true);
		uPanel.setLineWrap(true);
		uPanel.setEditable(false);
		uPanel.setText(detached);
	    JScrollPane jsp = new JScrollPane (uPanel);
		return jsp;
	}

	// insert party information into a non editable text panel
	protected JComponent makePartyPanel(String thisParty) {
	    JTextArea partay = new JTextArea(20,20);
	    partay.setWrapStyleWord(true);
	    partay.setLineWrap(true);
	    partay.setEditable(false);
	    partay.setText(thisParty);
	    JScrollPane jsp = new JScrollPane (partay);
	    return jsp;
	}
	
	// Returns an ImageIcon, or null if the path was invalid.
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GameGUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	// create new party, assign to cave
	// p:<index>:<name>
	public static void addParty(String attributes[]) {
		int index = Integer.parseInt(attributes[1]);
		String name = attributes[2];
		Party myParty = new Party(index, name);
		
		sorcerersCave.addParty(myParty);
	}

	// create new creature assign to party
	// c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
	public static void addCreature(String attributes[]) {
		// set instance variables from incoming array of strings
		int index = Integer.parseInt(attributes[1]);
		String type = attributes[2];
		String name = attributes[3];
		int party = Integer.parseInt(attributes[4]);
		int empathy = Integer.parseInt(attributes[5]);
		int fear = Integer.parseInt(attributes[6]);
		int carryingCapacity = Integer.parseInt(attributes[7]);
		
		// create creature from attributes
		Creature myCreature = new Creature(index, type, name, party, empathy, fear, carryingCapacity);
		
		// find creatures party
		if (party == 0){
			sorcerersCave.addDetachedCreature(myCreature);
		} else {
			Party myParty = sorcerersCave.getPartyByIndex(party);
			// add creature to its party
			myParty.addCreature(myCreature);
		}
	}

	// create new treasure, assign appropriately
	// t:<index>:<type>:<creature>:<weight>:<value>
	public static void addTreasure(String attributes[]) {
		// set instance variables from incoming array of strings
		int index = Integer.parseInt(attributes[1]);
		String type = attributes[2];
		int creature = Integer.parseInt(attributes[3]);
		double weight = Double.parseDouble(attributes[4]);
		int value = Integer.parseInt(attributes[5]);
		
		// create treasure from attributes
		Treasure myTreasure = new Treasure(index, type, creature, weight, value);
		
		// determine if treasure is claimed by a creature
		if (creature == 0) {
			sorcerersCave.addUndiscoveredTreasure(myTreasure);
		} else {
			for (Party myParty: sorcerersCave.getParties()) {
				if (!(null == myParty.getCreatureByIndex(creature))) {
					myParty.getCreatureByIndex(creature).addTreasure(myTreasure);
				}
			}
		}
	}

	// create new artifact, assign appropriately
	// a:<index>:<type>:<creature>[:<name>]
	public static void addArtifact(String attributes[]) {
		// set instance variables from incoming array of strings
		int index = Integer.parseInt(attributes[1]);
		String type = attributes[2];
		int creature = Integer.parseInt(attributes[3]);
		String name = null;
		// assign name or leave null
		if (attributes.length > 4){
			name = attributes[4];
		}		

		Artifact myArtifact = new Artifact(index, type, creature, name);
	
		// determine if treasure is claimed by a creature
		if (creature == 0) {
			sorcerersCave.addUndiscoveredArtifact(myArtifact);
		} else {
			for (Party myParty: sorcerersCave.getParties()) {
				if (!(null == myParty.getCreatureByIndex(creature))) {
					myParty.getCreatureByIndex(creature).addArtifact(myArtifact);
				}
			}
		}
	}
	
	// Helper method to return the first digit of an integer for
	// simplification of switch case in resolving the cave item subset
	public static int firstDigit(int x) {
	    if (x == 0) return 0;
	    x = Math.abs(x);
	    return (int) Math.floor(x / Math.pow(10, Math.floor(Math.log10(x))));
	}
	
	// Search for item by index
	public static String getCaveItemByIndex(int index) {
		String caveItem = "";
		int i = firstDigit(index);
		switch (i) {
			// for a party
			case 1:
				if (!(sorcerersCave.getPartyByIndex(index) == null)){
					caveItem = sorcerersCave.getPartyByIndex(index).toString();
				}
				break;
			// for a creature
			case 2:
				for(Party party: sorcerersCave.getParties()) {
					if (!(null == party.getCreatureByIndex(index))) {
						caveItem += party.getCreatureByIndex(index).toString();
					} 
				}
				for(Creature creature: sorcerersCave.getDetechedCreatures()) {
					if (index == creature.getIndex()) {
						caveItem += creature.toString();
					}
				}
				break;
			// for treasures
			case 3:
				for(Party party: sorcerersCave.getParties()) {
					for(Creature creature: party.getCreatures()){
						if (!(null == creature.getTreasureByIndex(index))) {
						    caveItem += creature.getTreasureByIndex(index).toString();
						}
					} 
				}
				for(Creature creature: sorcerersCave.getDetechedCreatures()) {
					for(Treasure treasure: creature.getTreasures()){
						if (index == treasure.getIndex()) {
							caveItem += treasure.toString();
						}
					}
				}
				for(Treasure treasure: sorcerersCave.getUndiscoveredTreasure()){
					if (index == treasure.getIndex()) {
						caveItem += treasure.toString();
					}
				}
				break;
			// for artifacts
			case 4:
				for(Party party: sorcerersCave.getParties()) {
					for(Creature creature: party.getCreatures()){
						if (!(null == creature.getArtifactByIndex(index))) {
						    caveItem += creature.getArtifactByIndex(index).toString();
						}
					} 
				}
				for(Creature creature: sorcerersCave.getDetechedCreatures()) {
					for(Artifact artifact: creature.getArtifacts()) {
						if (index == artifact.getIndex()) {
							caveItem += artifact.toString();
						}
					}
				}
				for(Artifact artifact: sorcerersCave.getUndiscoveredArtifacts()) {
					if (index == artifact.getIndex()) {
						caveItem += artifact.toString();
					}
				}
				break;
			// Jobs are not supported but will be later
		}
		
		// standardize response when nothing was found with search
		if ("" == caveItem) {
			caveItem = "No Results Found In Search!";
		}
		
		return caveItem;
	}
	
	// Get a cave item by name string
	public static String getCaveItemByName(String name) {
		String caveItem = "";
		// iterated over all parties in the cave
		for(Party party: sorcerersCave.getParties()){
			if (party.getName().equals(name)){
				caveItem += party.toString();
			}
			// iterate over all creatures in the party
			for(Creature creature: party.getCreatures()){
				if (creature.getName().equals(name)) {
					caveItem += creature.toString();
				}
				// iterated over all artifacts by creature
				for(Artifact artifact: creature.getArtifacts()){
					if (artifact.getName().equals(name)){
						caveItem += artifact.toString();
					}
				}
				// treasure does not contain a name attribute
			}
		}
		for (Creature creature: sorcerersCave.getDetechedCreatures()) {
			if (creature.getName().equals(name)){
				caveItem += creature.toString();
			}
		}
		
		for (Artifact artifact: sorcerersCave.getUndiscoveredArtifacts()) {
			if (artifact.getName().equals(name)) {
				caveItem += artifact.toString();
			}
		}
		
		// standardizes response when no item returned
		if ("" == caveItem) {
			caveItem = "No Results Found In Search!";
		}
		
		return caveItem;
	}
	
	// Get item by type
	public static String getCaveItemByType(String type) {
		String caveItem = "";
		// iterate over all parties in cave
		for(Party party: sorcerersCave.getParties()){
			// iterate over all creatures in party
			for(Creature creature: party.getCreatures()){
				if (creature.getType().equals(type)) {
					caveItem += creature.toString();
				}
				// iterate over treasure on creature
				for(Treasure treasure: creature.getTreasures()){
					if(treasure.getType().equals(type)){
						caveItem += treasure.toString();
					}
				}
				// iterate over artifacts on creature
				for(Artifact artifact: creature.getArtifacts()){
					if(artifact.getType().equals(type)) {
						caveItem += artifact.toString();
					}
				}
			}
		}
		for (Creature creature: sorcerersCave.getDetechedCreatures()) {
			if (creature.getType().equals(type)){
				caveItem += creature.toString();
			}
			for (Artifact artifact: creature.getArtifacts()){
				if (artifact.getType().equals(type)){
					caveItem += artifact.toString();
				}
			}
			for (Treasure treasure: creature.getTreasures()) {
				if (treasure.getType().equals(type)) {
					caveItem += treasure.toString();
				}
			}
		}
		
		for (Artifact artifact: sorcerersCave.getUndiscoveredArtifacts()) {
			if (artifact.getType().equals(type)) {
				caveItem += artifact.toString();
			}
		}
		
		for (Treasure treasure: sorcerersCave.getUndiscoveredTreasure()) {
			if (treasure.getType().equals(type)) {
				caveItem += treasure.toString();
			}
		}
		
		// standardize response when item not found
		if ("" == caveItem) {
			caveItem = "No Results Found In Search!";
		}
		
		return caveItem;
	}
}