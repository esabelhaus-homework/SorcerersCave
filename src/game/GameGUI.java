/*
 * FILE: 	GameGUI.java
 * DATE:  	05/25/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Read attribute file, populate objects, display game
 */
package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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

	// create image icons to be used by whole application
	protected ImageIcon partyIcon = createImageIcon("img/party.png");
	protected ImageIcon charIcon = createImageIcon("img/orc.gif");
	protected ImageIcon itemIcon = createImageIcon("img/wand.png");
	protected ImageIcon searchIcon = createImageIcon("img/search.png");

	// embed all job views in this
	protected JPanel parentJobPanel = new JPanel();

	// create tree globally to make it accessible to
	// sorting menu
	protected JTree tree;
	
	// creates split pane globally to make it accessible
	// to the event handler on the tree
	protected JSplitPane treeSplitPane;
	
	public Cave sorcerersCave = null;

	// make text area for search result population
	private JTextArea searchResult = new JTextArea(20,20);

	private final Integer[] nums = new Integer[1000]; 

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		GameGUI gui = new GameGUI();
	}

	// non argument constructor for normal operation
	public GameGUI() {this (new String [0]);}

	// Public constructor, used to build out visual elements 
	// for inspecting each party in the cave
	// as well as viewing undiscovered treasure if there is any
	// allow string to be passed in for file selection during testing
	public GameGUI(String[] args) {

		// create instance variables for determining whether to use args or chosen file
		String fileName = null;
		JFileChooser chooser = null;
		Scanner sfin = null;

		// instantiate file chooser in current directory
		if (args.length > 0) {
			fileName = args[0];
		} else {
			chooser = new JFileChooser(".");
		}

		// try reading in the file and scanning it
		try {
			if (null == fileName){
				int returnVal = chooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
					sfin = new Scanner (chooser.getSelectedFile());
				}
			} else {
				System.out.println(fileName);
				sfin = new Scanner (new File (fileName));
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File not found.");
		}

		sorcerersCave = new Cave(sfin, parentJobPanel);

		parentJobPanel.setLayout (new GridLayout (0, 7));
		JScrollPane scrollingJobPanel = new JScrollPane (parentJobPanel);

		// instantiate frame
		frame = new JFrame("Sorcerers Cave");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create horizontal tabbed pane for navigation
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		// create array of integers for setting age, height, and weight in creature
		// modification view
		for (int i = 0; i < 1000; i++)
			nums[i] = i;

		// create tree view for view of whole cave
		// implement action listener to update treeViewSelected
		// to display the selected tree node 
		tree = new JTree(createNodes("My Cave"));
				
		tree.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent me) {
		    	doMouseClicked();
		    }
		});
		
		// create JPanel to house all tree related elements
		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BorderLayout());
		
		// tree sorting menu
		JComponent sortMenu = createSortMenu();
		treePanel.add(sortMenu, BorderLayout.NORTH);
		
		// create scroll panel to embed tree within
		JScrollPane treeView = new JScrollPane(tree);		
		// create tree view selected pane to a generic jtext area
		// give a hint about the tree functionality
		JComponent treeViewSelected = new JTextArea("Try selecting a node from the tree!");
		
		// embed tree and selection component into split pane
		treeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, treeViewSelected);
		
		// divide sides evenly at first
		treeSplitPane.setDividerLocation(400);
		
		// add split pane to tree panel
		treePanel.add(treeSplitPane, BorderLayout.CENTER);
		
		// add tab for cave explorer, which contains tree and sort menu
		tabbedPane.addTab("Cave Explorer", treePanel);
		
		// add search pane as first tab in tabbed pane
		JComponent searchResults = createSearchPanel();
		tabbedPane.addTab("Search The Cave", searchIcon, searchResults);

		// set preferred size of tabbed pane
		tabbedPane.setPreferredSize(new Dimension(800,400));

		// set preferred size of job panel
		scrollingJobPanel.setPreferredSize(new Dimension(800,200));

		// add tabbed pane and job panel to larger vertical split pane
		JSplitPane mainSplitPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, tabbedPane, scrollingJobPanel);

		// set divider to 2/3 and 1/3 respectively
		mainSplitPane.setDividerLocation(400);

		// set up the frame, add main split pane
		frame.setLayout(new BorderLayout ());
		frame.add(mainSplitPane, BorderLayout.CENTER);
		frame.setPreferredSize(new Dimension(800,600));

		// handle some random exceptions when swing closes
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	
	// mouse click handler for displaying tree node objects in right hand side of tree split pane
	protected void doMouseClicked() {
		// get the currently selected node
		// if it is root, just display text
		// otherwise, display the appropriate element in the right pane of the split pane
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	    if (null == node) {
	    	System.out.println("User clicked in empty space");
	    } else if (node.isRoot()) {
	    	treeSplitPane.setRightComponent(new JTextArea("You've selected the whole cave, too broad of a selection"));
	    	treeSplitPane.repaint();
	    	validate();
	    } else {
	    	TreeNodeObject to = (TreeNodeObject) node.getUserObject();
	    	CaveElement ce = getCaveElementByIndex(to.getIndex());
	    	treeSplitPane.setRightComponent(createCaveElementPanel(ce));
	    	validate();
	    }
	}
	
	// creates panel to be added as search tab
	protected JComponent createSearchPanel() {
		// JPanel container for search panel
		JPanel searchPanel = new JPanel();

		// Button and search text for search by field in top menu bar
		final JButton searchButton = new JButton("  Search By...  ");
		JTextField searchText = new JTextField();

		// Picker box for type of search
		final JComboBox<?> searchPickerBox = new JComboBox<Object>( new Object[]{
				"name",
				"type", 
				"index"
		});

		// create search bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(searchButton);
		menuBar.add(searchPickerBox);
		menuBar.add(searchText);
		menuBar.add(Box.createHorizontalGlue());

		// create search text pane
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
				String myType = searchPickerBox.getSelectedItem().toString();

				if (myType == "name") {
					resultString = getCaveItemByName(searchString).toString();
				}

				if (myType == "type") {
					resultString = getCaveItemByType(searchString).toString();
				}

				if (myType == "index") {
					int myIndex = Integer.parseInt(searchString);
					if ( (myIndex < 10000) || (myIndex > 60000) ){
						System.out.println("index out of bounds");
						JOptionPane.showMessageDialog(frame, "Index must be between 10000 and 59999");
						return;
					}
					CaveElement result = getCaveElementByIndex(myIndex);
					if (null == result) {
						resultString = "No Results Found In Search!";
					} else {
						resultString = result.toString();
					}
				}

				System.out.println(resultString);

				// reset text to show search results
				searchResult.setText(resultString);

				return;
			}
		});

		// add search elements to jpanel
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(menuBar, BorderLayout.NORTH);
		searchPanel.add(jsp, BorderLayout.CENTER);

		return searchPanel;

	}

	// helper method to allow simple creation
	// of cave element panel for tree view purposes
	protected JComponent createCaveElementPanel(CaveElement ce) {
		JComponent caveComponent = null;
		
		// get the class name of the cave element
		// split on the period in "class game.<CaveElement Class Name>"
		// switch based on the name, and return the appropriate JComponent for that element
		switch (ce.getClass().toString().split("\\.")[1]) {
			case "Party": caveComponent = createPartyPanel((Party) ce); break;
			case "Creature": caveComponent = createCreaturePanel((Creature) ce); break;
			case "Artifact": caveComponent = createItemPanel((Artifact) ce); break; 
			case "Treasure": caveComponent = createItemPanel((Treasure) ce); break;
		}
		return caveComponent;
	}
	

	// insert party information into a non editable text panel
	protected JComponent createPartyPanel(Party thisParty) {

		// JPanel container for party panel
		JPanel partyPanel = new JPanel();
		JTextArea partay = new JTextArea(20,20);
		partay.setWrapStyleWord(true);
		partay.setLineWrap(true);
		partay.setEditable(false);
		partay.setText(thisParty.toString());
		JScrollPane jsp = new JScrollPane (partay);

		partyPanel.setLayout(new BorderLayout());
		partyPanel.add(jsp, BorderLayout.CENTER);

		return partyPanel;
	}

	protected JComponent createCreaturePanel(Creature thisCreature) {
		// create container for input fields
		JPanel creaturePanel = new JPanel();
		creaturePanel.setLayout(new BorderLayout());

		// create button for updating fields
		JButton updateCreatureButton = new JButton("Update Creature");

		// create text fields and labels for updating attributes
		JComboBox<Integer> age = new JComboBox<Integer>(nums);
		JLabel ageLabel = new JLabel("Age: ");
		JComboBox<Integer> height = new JComboBox<Integer>(nums);
		JLabel heightLabel = new JLabel("Height: ");
		JComboBox<Integer> weight = new JComboBox<Integer>(nums);
		JLabel weightLabel = new JLabel("Weight: ");

		// create jpanel of creature update form
		JPanel updateCreature = new JPanel(new GridLayout(4, 2));

		// add elements to update form
		updateCreature.add(ageLabel);
		updateCreature.add(age);
		updateCreature.add(heightLabel);
		updateCreature.add(height);
		updateCreature.add(weightLabel);
		updateCreature.add(weight);
		updateCreature.add(updateCreatureButton);

		// text area for existing creature
		JTextArea myCreatureInfo = new JTextArea(20,10);
		myCreatureInfo.setWrapStyleWord(true);
		myCreatureInfo.setLineWrap(true);
		myCreatureInfo.setEditable(false);
		myCreatureInfo.setText(thisCreature.toString());

		// update creature info, refresh text
		updateCreatureButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				if ((Integer) age.getSelectedItem() > 0){
					thisCreature.setAge((Integer) age.getSelectedItem());
				}

				if ((Integer) height.getSelectedItem() > 0) {
					thisCreature.setHeight((Integer) height.getSelectedItem());
				}

				if ((Integer) weight.getSelectedItem() > 0) {
					thisCreature.setWeight((Integer) weight.getSelectedItem());
				}

				myCreatureInfo.setText(thisCreature.toString());
				validate();
				return;
			}
		});

		// Add creature info to scrolling panel
		JScrollPane myCreature = new JScrollPane(myCreatureInfo);

		// attach text scrolling panel to creature panel
		creaturePanel.add(myCreature, BorderLayout.CENTER);
		creaturePanel.add(updateCreature, BorderLayout.EAST);

		return creaturePanel;
	}
	
	// insert item info into a non editable text panel
	protected JComponent createItemPanel(CaveElement ce) {
		JTextArea uPanel = new JTextArea(20,20);
		uPanel.setWrapStyleWord(true);
		uPanel.setLineWrap(true);
		uPanel.setEditable(false);
		uPanel.setText(ce.toString());
		JScrollPane jsp = new JScrollPane (uPanel);
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

	// create a sort menu to be inserted on party all panels
	protected JComponent createSortMenu() {
		// create sorting label to tidy up the menu bar
		JLabel sortLabel = new JLabel(" Sort By... ");
		// sort type picker box
		JComboBox<String> sortTypePickerBox;
		// Picker box for sort parameter
		JComboBox<String> sortParamPickerBox;

		// Button and text for sort by field in side menu bar
		JButton sortButton = new JButton("Sort");

		// Sorting radio buttons to toggle sort type
		String[] typeSelection     = { "Creature", "Treasure" };
		String[] creatureSelection = {"Age", "Empathy", "Fear", "Height", "Name", "Weight"};
		String[] treasureSelection = {"Value", "Weight"};

		// sort picker boxes, initialized with creature selection
		sortTypePickerBox = new JComboBox<String>(typeSelection);
		sortParamPickerBox = new JComboBox<String>(creatureSelection);

		// action listener on sort picker box to update the fields by type
		sortTypePickerBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if("creature".equals(sortTypePickerBox.getSelectedItem().toString().toLowerCase())) {
					DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>( creatureSelection );
					sortParamPickerBox.setModel(model);
					validate();
				} else {
					DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>( treasureSelection );
					sortParamPickerBox.setModel(model);
					validate();
				}
			}
		});

		// action listener on sort button
		sortButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// ensure all selections are made before sorting
				if (sortTypePickerBox.getSelectedItem() == null) {
					JOptionPane.showMessageDialog(frame, "Please select Creature of Treasure"); 
					return;
				}
				if (sortParamPickerBox.getSelectedItem() == null) {
					JOptionPane.showMessageDialog(frame, "Please select a parameter to sort on"); 
					return;
				}

				// two leveled switch case to handle sorting efficiently based off
				// selected parameters in JComboBox defined above
				// also creates string to add as root item of tree when
				// recreated
				String caveSortName = "My Cave Sorted By: ";
				switch (sortTypePickerBox.getSelectedItem().toString().toLowerCase()) {
					case "creature":
						caveSortName += " Creature and ";
						switch (sortParamPickerBox.getSelectedItem().toString().toLowerCase()) {
							case "age"	   : 	
								sortCreaturesByAge();
								caveSortName += "Age";
								break;
							case "empathy" :	
								sortCreaturesByEmpathy();
								caveSortName += "Empathy";
								break;
							case "fear"	   : 	
								sortCreaturesByFear();
								caveSortName += "Fear";
								break;
							case "height"  : 	
								sortCreaturesByHeight();
								caveSortName += "Height";
								break;
							case "name"	   : 	
								sortCreaturesByName();
								caveSortName += "Name";
								break;
							case "weight"  :    
								sortCreaturesByWeight();
								caveSortName += "Weight";
								break;
						}
						break;
					case "treasure":
						caveSortName += " Treasures and ";
						switch (sortParamPickerBox.getSelectedItem().toString().toLowerCase()) {
							case "value"  : 
								sortTreasureByValue();
								caveSortName += "Value";
								break;
							case "weight" : 
								sortTreasureByWeight();
								caveSortName += "Weight";
								break;
						}
					break;
				}

				// create newly sorted tree
				tree = new JTree(createNodes(caveSortName));
				
				// add mouse listener to new tree
				tree.addMouseListener(new MouseAdapter() {
					@Override
				    public void mouseClicked(MouseEvent me) {
				    	doMouseClicked();
				    }
				});
				
				// add new tree to scroll panel
				JScrollPane treeView = new JScrollPane(tree);
				
				// update tree in gui
				treeSplitPane.setLeftComponent(treeView);
				
				// validate gui
				validate();
				return;
			}
		});

		// create sorting bar
		JMenuBar menuBar = new JMenuBar();

		// add created elements to sorting bar
		menuBar.add(sortLabel);
		menuBar.add(sortTypePickerBox);
		menuBar.add(sortParamPickerBox);
		menuBar.add(sortButton);
		menuBar.add(Box.createHorizontalGlue());

		return menuBar;
	}

	

	// Create tree view using custom tree node objects which contain the name and index
	// of the object in reference. Index will be used to render view of object within
	// the gui
	public DefaultMutableTreeNode createNodes(String title) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(title);
		DefaultMutableTreeNode pn, cn;
		// handle detached creatures
		for(Creature c: sorcerersCave.getDetechedCreatures()) {
			top.add(new DefaultMutableTreeNode(new TreeNodeObject(c.getIndex(), "Creature: " + c.getName())));
		}
		// handle undiscovered treasure
		for(Treasure t: sorcerersCave.getUndiscoveredTreasure()) {
			top.add(new DefaultMutableTreeNode(new TreeNodeObject(t.getIndex(), "Treasure: " + t.getType())));
		}
		// handle undiscovered artifacts
		for(Artifact a: sorcerersCave.getUndiscoveredArtifacts()) {
			top.add(new DefaultMutableTreeNode(new TreeNodeObject(a.getIndex(), "Artifact: " + a.getName())));
		}
		// handle all of the remaining items in the multi tree
		for(Party p: sorcerersCave.getParties()) {       
			pn = new DefaultMutableTreeNode(new TreeNodeObject(p.getIndex(), "Party: " + p.getName()));
			top.add(pn);
			for(Creature c: p.getCreatures()) { 
				cn = new DefaultMutableTreeNode(new TreeNodeObject(c.getIndex(), "Creature: " + c.getName()));
				pn.add(cn); 
				for(Treasure t: c.getTreasures())
					cn.add(new DefaultMutableTreeNode(new TreeNodeObject(t.getIndex(), "Treasure: " + t.getType())));
				for(Artifact a: c.getArtifacts())
					cn.add(new DefaultMutableTreeNode(new TreeNodeObject(a.getIndex(), "Artifact: " + a.getName())));
			}
		}
		return top;
	}

	/*
	 *  Sort by methods
	 */

	public void sortCreaturesByName() {
		// Sort creatures using name comparator
		for (Party myParty: sorcerersCave.getParties()){
			Collections.sort(myParty.getCreatures(), new CreatureNameComparator());
		}
	}

	public void sortCreaturesByHeight() {
		// Sort creatures using name comparator
		for (Party myParty: sorcerersCave.getParties()){
			Collections.sort(myParty.getCreatures(), new CreatureHeightComparator());
		}
	}

	public void sortCreaturesByAge() {
		// Sort creatures using name comparator
		for (Party myParty: sorcerersCave.getParties()){
			Collections.sort(myParty.getCreatures(), new CreatureAgeComparator());
		}
	}

	public void sortCreaturesByWeight() {
		// Sort creatures using name comparator
		for (Party myParty: sorcerersCave.getParties()){
			Collections.sort(myParty.getCreatures(), new CreatureWeightComparator());
		}
	}

	public void sortCreaturesByEmpathy() {
		// Sort creatures using name comparator
		for (Party myParty: sorcerersCave.getParties()){
			Collections.sort(myParty.getCreatures(), new CreatureEmpathyComparator());
		}
	}

	public void sortCreaturesByFear() {
		// Sort creatures using name comparator
		for (Party myParty: sorcerersCave.getParties()){
			Collections.sort(myParty.getCreatures(), new CreatureFearComparator());
		}
	}

	public void sortTreasureByValue() {
		// Sort treasure carried by creatures by their value
		for (Party myParty: sorcerersCave.getParties()){
			for (Creature creature: myParty.getCreatures()){
				Collections.sort(creature.getTreasures(), new TreasureValueComparator());
			}
		}
	}

	public void sortTreasureByWeight() {
		// Sort treasure carried by creatures by their weight
		for (Party myParty: sorcerersCave.getParties()){
			for (Creature creature: myParty.getCreatures()){
				Collections.sort(creature.getTreasures(), new TreasureWeightComparator());
			}
		}
	}

	// Helper method to return the first digit of an integer for
	// simplification of switch case in resolving the cave item subset
	public int firstDigit(int x) {
		if (x == 0) return 0;
		x = Math.abs(x);
		return (int) Math.floor(x / Math.pow(10, Math.floor(Math.log10(x))));
	}
	
	// Search for item by index
	public CaveElement getCaveElementByIndex(int index) {
		CaveElement caveItem = null;
		int i = firstDigit(index);
		switch (i) {
		// for a party
		case 1:
			if (!(sorcerersCave.getPartyByIndex(index) == null)){
				caveItem = (Party) sorcerersCave.getPartyByIndex(index);
			}
			break;
		// for a creature
		case 2:
			for(Party party: sorcerersCave.getParties()) {
				if (!(null == party.getCreatureByIndex(index))) {
					caveItem = (Creature) party.getCreatureByIndex(index);
				} 
			}
			for(Creature creature: sorcerersCave.getDetechedCreatures()) {
				if (index == creature.getIndex()) {
					caveItem = (Creature) creature;
				}
			}
			break;
		// for treasures
		case 3:
			for(Party party: sorcerersCave.getParties()) {
				for(Creature creature: party.getCreatures()){
					if (!(null == creature.getTreasureByIndex(index))) {
						caveItem = (Treasure) creature.getTreasureByIndex(index);
					}
				} 
			}
			for(Creature creature: sorcerersCave.getDetechedCreatures()) {
				for(Treasure treasure: creature.getTreasures()){
					if (index == treasure.getIndex()) {
						caveItem = (Treasure) treasure;
					}
				}
			}
			for(Treasure treasure: sorcerersCave.getUndiscoveredTreasure()){
				if (index == treasure.getIndex()) {
					caveItem = (Treasure) treasure;
				}
			}
			break;
		// for artifacts
		case 4:
			for(Party party: sorcerersCave.getParties()) {
				for(Creature creature: party.getCreatures()){
					if (!(null == creature.getArtifactByIndex(index))) {
						caveItem = (Artifact) creature.getArtifactByIndex(index);
					}
				} 
			}
			for(Creature creature: sorcerersCave.getDetechedCreatures()) {
				for(Artifact artifact: creature.getArtifacts()) {
					if (index == artifact.getIndex()) {
						caveItem = (Artifact) artifact;
					}
				}
			}
			for(Artifact artifact: sorcerersCave.getUndiscoveredArtifacts()) {
				if (index == artifact.getIndex()) {
					caveItem = (Artifact) artifact;
				}
			}
			break;
		case 5:
			for(Job job: sorcerersCave.getJobs()){
				if (index == job.getIndex()) {
					caveItem = (Job) job;
				}
			}
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