/*
 * FILE: 	TreeNodeObject.java
 * DATE:  	06/23/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Store tree node object with CaveElement index
 */
package game;

public class TreeNodeObject {
	private int index;
	private String label;

	// public constructor
	public TreeNodeObject(int thisIndex, String thisLabel) {
		this.index = thisIndex;
		this.label = thisLabel;
	}
	
	// returns CaveElement index for this tree node
	public int getIndex() {
		return index;
	}
	
	// displays the label proved within the JTree view
	@Override
	public String toString() {
	    return this.label;
	}
}
