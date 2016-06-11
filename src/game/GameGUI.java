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
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

// GUI and driver to test objects
public class GameGUI extends JPanel {

	private static final long serialVersionUID = 1L;

	// Initiate GUI methods
	JFrame frame = new JFrame();

	public Cave sorcerersCave = new Cave();

	// make text area for search result population
	private JTextArea searchResult = new JTextArea(20,20);

	public static void main(String[] args) throws IOException {
		GameGUI gui = new GameGUI();
	}

	// Read in lines of data file, add cave elements in the process
	void readFile(Scanner sf) {
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
			case 'j' : break; // jobs not yet supported
			}
		}
	}

	// Public constructor, used to build out visual elements for inspecting each party in the cave
	// as well as viewing undiscovered treasure if there is any
	public GameGUI() {
		frame = new JFrame("Sorcerers Cave");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Scanner sfin = null;
		// instantiate file chooser in current directory
		JFileChooser chooser = new JFileChooser(System.getProperty("."));

		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " +
					chooser.getSelectedFile().getName());
			try {
				sfin = new Scanner (chooser.getSelectedFile());
			}
			catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "File not found.");
			}
		}

		// read attributes from file
		readFile(sfin);

		// close file
		sfin.close();

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		ImageIcon partyIcon = createImageIcon("img/party.png");
		ImageIcon charIcon = createImageIcon("img/orc.gif");
		ImageIcon itemIcon = createImageIcon("img/wand.png");
		ImageIcon searchIcon = createImageIcon("img/search.png");


		// add search pane as first tab in tabbed pane
		JComponent searchResults = makeSearchPanel(sorcerersCave);
		tabbedPane.addTab("Search The Cave", searchIcon, searchResults);

		//Iterate over every party, detached creature and undiscovered item
		for (Party myParty: sorcerersCave.getParties()) {
			JComponent panel = makePartyPanel(myParty);
			tabbedPane.addTab(myParty.getName(), partyIcon, panel);
		}

		JComponent detached = makeDetachedCreaturePanel(sorcerersCave);
		tabbedPane.addTab("Wandering Creatures", charIcon, detached);

		JComponent undiscovered = makeUndiscoveredPanel(sorcerersCave);
		tabbedPane.addTab("Undiscovered Items", itemIcon, undiscovered);

		
		
		JTree tree = new JTree(createNodes("My Cave"));
	    JScrollPane treeView = new JScrollPane(tree);
	    tabbedPane.addTab("Tree View", partyIcon, treeView);
		
		//Create and set up the window.
		frame.setLayout(new BorderLayout ());
		frame.add(tabbedPane, BorderLayout.CENTER);
		frame.setPreferredSize(new Dimension(800,400));

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	

	
	protected JComponent makeSearchPanel(Cave thisCave) {
		// JPanel container for search panel
		JPanel searchPanel = new JPanel();

		// Button and search text for search by field in top menu bar
		final JButton searchButton = new JButton("  Search By...  ");
		JTextField searchText = new JTextField();

		// Picker box for type of search
		final JComboBox<?> searchPickerBox = new JComboBox<Object>( new Object[]{
				"name",
				"type", 
		"index"}
				);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(searchButton);
		menuBar.add(searchPickerBox);
		menuBar.add(searchText);
		menuBar.add(Box.createHorizontalGlue());

		searchResult.setWrapStyleWord(true);
		searchResult.setLineWrap(true);
		searchResult.setEditable(false);
		searchResult.setText("");
		JScrollPane jsp = new JScrollPane(searchResult);

		// action listener on search button
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String searchString = searchText.getText();
				String resultString = "";
				if ("".equals(searchString)) {
					JOptionPane.showMessageDialog(frame, "YOU MUST SPECIFY A SEARCH STRING");
					return;
				}
				// Switch on search type based off picker box
				System.out.println(searchPickerBox.getSelectedItem().toString());
				String myType = searchPickerBox.getSelectedItem().toString();

				if (myType == "name") {
					System.out.println("name");
					resultString = getCaveItemByName(searchString);
				}

				if (myType == "type") {
					System.out.println("type");
					resultString = getCaveItemByType(searchString);
				}

				if (myType == "index") {
					System.out.println("index");
					int myIndex = Integer.parseInt(searchString);
					if ( (myIndex < 10000) || (myIndex > 60000) ){
						System.out.println("index out of bounds");
						JOptionPane.showMessageDialog(frame, "Index must be between 10000 and 59999");
						return;
					}
					resultString = getCaveItemByIndex(myIndex);
				}

				System.out.println(resultString);

				searchResult.setText(resultString);

				return;
			}
		});

		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(menuBar, BorderLayout.NORTH);
		searchPanel.add(jsp, BorderLayout.CENTER);

		return searchPanel;

	}

	// insert all undiscovered items into a non editable text panel
	protected JComponent makeUndiscoveredPanel(Cave thisCave) {
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
	protected JComponent makeDetachedCreaturePanel(Cave thisCave) {
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
	protected JComponent makePartyPanel(Party thisParty) {
		JTextArea partay = new JTextArea(20,20);
		partay.setWrapStyleWord(true);
		partay.setLineWrap(true);
		partay.setEditable(false);
		partay.setText(thisParty.toString());
		JScrollPane jsp = new JScrollPane (partay);
		return jsp;
	}

	// Returns an ImageIcon, or null if the path was invalid.
	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GameGUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	// sort type picker box
	private JComboBox<String> sortTypePickerBox;
	// Picker box for sort parameter
	private JComboBox<String> sortParamPickerBox;
	
	
	private JComponent makeSortMenu(JComponent thisComponent, Party thisParty) {

		// Button and text for sort by field in side menu bar
		JButton sortButton = new JButton("  Sort By...  ");

		// Sorting radio buttons to toggle sort type

		String[] creatureSelection = {"Age", "Empathy", "Fear", "Height", "Name", "Weight"};
		String[] treasureSelection = {"Value", "Weight"};
		
		sortTypePickerBox = new JComboBox<String>();
		sortTypePickerBox.addItem("Creature");
		sortTypePickerBox.addItem("Treasure");
		sortParamPickerBox = new JComboBox<String>();
		
		sortTypePickerBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if("creature" == sortTypePickerBox.getSelectedItem().toString().toLowerCase()) {
					DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>( creatureSelection );
					sortParamPickerBox.setModel(model);
				} else {
					DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>( treasureSelection );
					sortParamPickerBox.setModel(model);
				}
			}
		});

		JMenuBar menuBar = new JMenuBar();

		menuBar.add(Box.createHorizontalGlue());
		
		return null;
	}

	protected JComponent createCreaturePanel(Creature thisCreature) {

		return null;
	}

	// create new party, assign to cave
	// p:<index>:<name>
	public Party addParty(Scanner thisItem) {

		// Create new party and add it to the cave
		Party myParty = new Party(thisItem);
		sorcerersCave.addParty(myParty);

		// return party to be added to HashMap
		return myParty;
	}

	// create new creature assign to party
	// c:<index>:<type>:<name>:<party>:<empathy>:<fear>:<carrying capacity>
	public Creature addCreature(HashMap<Integer, CaveElement> theseElements, Scanner thisItem) {

		// create creature from attributes
		Creature myCreature = new Creature(thisItem);

		// find party within HashMap from created creature
		Party myParty = (Party) theseElements.get(myCreature.getParty());

		// add creature to cave or party
		if (myParty == null) {
			sorcerersCave.addDetachedCreature(myCreature);
		} else {
			myParty.addCreature(myCreature);
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
			sorcerersCave.addUndiscoveredTreasure(myTreasure);
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
			sorcerersCave.addUndiscoveredArtifact(myArtifact);
		} else {
			myCreature.addArtifact(myArtifact);
		}

	}

	public DefaultMutableTreeNode createNodes(String title) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(title);
		DefaultMutableTreeNode pn, cn;
		for(Party p: sorcerersCave.getParties()) {       
			pn = new DefaultMutableTreeNode(p.getName());
			top.add(pn);              
			for(Creature c: p.getCreatures()) { 
				cn = new DefaultMutableTreeNode(c.getName());
				pn.add(cn); 
				for(Treasure t: c.getTreasures())
					cn.add(new DefaultMutableTreeNode("T: " + t.getType()));
				for(Artifact a: c.getArtifacts())
					cn.add(new DefaultMutableTreeNode("A: " + a.getType()));
			} // end for each creature
		} // end for each party
		return top;
	} // end method createNodes


	/*
	 *  Sort by methods
	 */

	public Party sortCreaturesByName(Party myParty) {
		// Sort creatures using name comparator
		Collections.sort(myParty.getCreatures(), new CreatureNameComparator());

		// return sorted party
		return myParty;
	}

	public Party sortCreaturesByHeight(Party myParty) {
		// Sort creatures using name comparator
		Collections.sort(myParty.getCreatures(), new CreatureHeightComparator());

		// return sorted party
		return myParty;
	}

	public Party sortCreaturesByAge(Party myParty) {
		// Sort creatures using name comparator
		Collections.sort(myParty.getCreatures(), new CreatureAgeComparator());

		// return sorted party
		return myParty;
	}

	public Party sortCreaturesByWeight(Party myParty) {
		// Sort creatures using name comparator
		Collections.sort(myParty.getCreatures(), new CreatureWeightComparator());

		// return sorted party
		return myParty;
	}

	public Party sortCreaturesByEmpathy(Party myParty) {
		// Sort creatures using name comparator
		Collections.sort(myParty.getCreatures(), new CreatureEmpathyComparator());

		// return sorted party
		return myParty;
	}

	public Party sortCreaturesByFear(Party myParty) {
		// Sort creatures using name comparator
		Collections.sort(myParty.getCreatures(), new CreatureFearComparator());

		// return sorted party
		return myParty;
	}

	public Party sortTreasureByValue(Party myParty) {
		Collections.sort(myParty.getCreatures(), new TreasureValueComparator());

		return myParty;
	}

	public Party sortTreasureByWeight(Party myParty) {
		Collections.sort(myParty.getCreatures(), new TreasureWeightComparator());

		return myParty;
	}

	// Helper method to return the first digit of an integer for
	// simplification of switch case in resolving the cave item subset
	public int firstDigit(int x) {
		if (x == 0) return 0;
		x = Math.abs(x);
		return (int) Math.floor(x / Math.pow(10, Math.floor(Math.log10(x))));
	}

	// Search for item by index
	public String getCaveItemByIndex(int index) {
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
	public String getCaveItemByName(String name) {
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
	public String getCaveItemByType(String type) {
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