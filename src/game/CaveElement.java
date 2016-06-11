/*
 * FILE: 	CaveElement.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Base object type for all cave elements
 */

package game;

public class CaveElement {
    protected int index;
    
    // Empty methods for adding objects to game
    //  objects. Only to be implemented in each
    //  child class when applicable.
    public void add(Party party) {}
    public void add(Creature creature) {}
    public void add(Treasure treasure) {}
    public void add(Artifact artifact) {}
    
    // Getters and setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
}
