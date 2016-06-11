/*
 * FILE: 	TreasureValueComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of treasure value
 */

package game;

import java.util.Comparator;

public class TreasureValueComparator implements Comparator<CaveElement> {

	@Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Treasure) c1).getValue() >  ((Treasure) c2).getValue())
            return 1;
        else if ( ((Treasure) c1).getValue() <  ((Treasure) c2).getValue())
            return -1;
        else
            return 0;
    }
	
}
