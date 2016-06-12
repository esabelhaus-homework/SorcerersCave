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
    public int compare(CaveElement t1, CaveElement t2) {
        if( ((Treasure) t1).getWeight() >  ((Treasure) t2).getWeight())
            return 1;
        else if( ((Treasure) t1).getWeight() <  ((Treasure) t2).getWeight())
            return -1;
        else
            return 0;
    }
	
}
