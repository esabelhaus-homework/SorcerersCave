/*
 * FILE: 	CreatureNameComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of names
 */

package game;

import java.util.Comparator;

public class CreatureNameComparator implements Comparator<CaveElement> {

	@Override
    public int compare(CaveElement c1, CaveElement c2) {
		return ( (Creature) c1).getName().compareTo( ((Creature) c2).getName());  
    }
	
}
