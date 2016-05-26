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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Initiate GUI methods
	protected static JFrame frame;
	public static JRadioButton button = new JRadioButton();
	
	public static Cave sorcerersCave = new Cave();
	
	public static void main(String[] args) throws IOException {
		
		// instantiate file chooser in current directory
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));

		// display file chooser
		chooser.showOpenDialog(chooser);
		
		String filePath = chooser.getSelectedFile().getName();
		
		// open file stream of chosen file
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

		// Button and search text for search by field in menu bar
		final JButton searchButton = new JButton("  Search By...  ");
		JTextField searchText = new JTextField("monster || 1111");
		
	    // Picker box for type of search
		final JComboBox<?> pickerBox = new JComboBox<Object>( new Object[]{
			"name",
			"type", 
			"index"}
	    );
		
		// action listener on search button
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String searchString = searchText.getText();
				if ("" == searchString) {
					JOptionPane.showMessageDialog(frame, "YOU MUST SPECIFY A SEARCH STRING");
				}
				// Switch on search type based off picker box
				System.out.println(pickerBox.getSelectedItem().toString());
				switch (pickerBox.getSelectedItem().toString()) {
				    case "name":
				    	JOptionPane.showMessageDialog(frame, getCaveItemByName(searchString));
				    case "type":
				    	JOptionPane.showMessageDialog(frame, getCaveItemByType(searchString));
				    case "index":
				    	int myIndex = Integer.parseInt(searchString);
				    	JOptionPane.showMessageDialog(frame, getCaveItemByIndex(myIndex));
		        }
			}
		});
		
		// Menu bar to tie the whole search feature together
		JMenuBar menuBar = new JMenuBar();
        menuBar.add(searchButton);
        menuBar.add(searchText);
        menuBar.add(pickerBox);
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
	
	public GameGUI(Cave myCave) {		
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        ImageIcon icon = createImageIcon("img/Elder_wand.png");
        
        
        
        for (Party myParty: myCave.getParties()) {
        	JComponent panel = makePartyPanel(myParty.toString());
        	tabbedPane.addTab(myParty.getName(), icon, panel);
        }
        JComponent undiscovered = makeUndiscoveredPanel(myCave);
        tabbedPane.addTab("Undiscovered Items", icon, undiscovered);
        
        setLayout (new BorderLayout ());
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(800,400));
        validate ();
    }
	
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

	protected JComponent makePartyPanel(String thisParty) {
	    JTextArea partay = new JTextArea(20,20);
	    partay.setWrapStyleWord(true);
	    partay.setLineWrap(true);
	    partay.setEditable(false);
	    partay.setText(thisParty);
	    JScrollPane jsp = new JScrollPane (partay);
	    return jsp;
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
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
	private static void addParty(String attributes[]) {
		int index = Integer.parseInt(attributes[1]);
		String name = attributes[2];
		Party myParty = new Party(index, name);
		
		sorcerersCave.addParty(myParty);
	}

	// create new creature assign to party
	// c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
	private static void addCreature(String attributes[]) {
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
		Party myParty = sorcerersCave.getPartyByIndex(party);
		
		// add creature to its party
		myParty.addCreature(myCreature);
		
	}

	// create new treasure, assign appropriately
	// t:<index>:<type>:<creature>:<weight>:<value>
	private static void addTreasure(String attributes[]) {
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
	private static void addArtifact(String attributes[]) {
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
	
	// Search for item by index
	public static String getCaveItemByIndex(int index) {
		String caveItem = ""; 
		switch (String.valueOf(Math.abs((long)index)).charAt(0)) {
			case '1':
				caveItem = sorcerersCave.getPartyByIndex(index).toString();
				break;
			case '2':
				for(Party party: sorcerersCave.getParties()) {
					if (!(null == party.getCreatureByIndex(index))) {
						caveItem += party.getCreatureByIndex(index).toString();
					} 
				}
				break;
			case '3':
				for(Party party: sorcerersCave.getParties()) {
					for(Creature creature: party.getCreatures()){
						if (!(null == creature.getTreasureByIndex(index))) {
						    caveItem += creature.getTreasureByIndex(index);
						}
					} 
				}
				break;
			case '4':
				for(Party party: sorcerersCave.getParties()) {
					for(Creature creature: party.getCreatures()){
						if (!(null == creature.getArtifactByIndex(index))) {
						    caveItem += creature.getArtifactByIndex(index);
						}
					} 
				}
				break;
		}
//		
//		if ("" == caveItem) {
//			caveItem = "No Results Found In Search!";
//		}
		return caveItem;
	}
	
	public static String getCaveItemByName(String name) {
		String caveItem = "";
		for(Party party: sorcerersCave.getParties()){
			if (party.getName() == name){
				caveItem += party.toString();
			}
			for(Creature creature: party.getCreatures()){
				if (creature.getName() == name) {
					caveItem += creature.toString();
				}
				for(Artifact artifact: creature.getArtifacts()){
					if (artifact.getName() == name){
						caveItem += artifact.toString();
					}
				}
			}
		}
		
//		if ("" == caveItem) {
//			caveItem = "No Results Found In Search!";
//		}
		
		return caveItem;
	}
	
	public static String getCaveItemByType(String type) {
		String caveItem = "";
		for(Party party: sorcerersCave.getParties()){
			for(Creature creature: party.getCreatures()){
				if (creature.getType() == type) {
					caveItem += creature.toString();
				}
				for(Treasure treasure: creature.getTreasures()){
					if(treasure.getType() == type){
						caveItem += treasure.toString();
					}
				}
				for(Artifact artifact: creature.getArtifacts()){
					if(artifact.getType() == type) {
						caveItem += artifact.toString();
					}
				}
			}
		}
		
		return caveItem;
	}
}

