/*
 * FILE: 	TreasureWeightComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of treasure weight
 */

package game;

import java.util.Comparator;

public class TreasureWeightComparator implements Comparator<CaveElement> {

	@Override
    public int compare(CaveElement c1, CaveElement c2) {
        if( ((Treasure) c1).getWeight() >  ((Treasure) c2).getWeight())
            return 1;
        else if( ((Treasure) c1).getWeight() <  ((Treasure) c2).getWeight())
            return -1;
        else
            return 0;
    }
	
}
