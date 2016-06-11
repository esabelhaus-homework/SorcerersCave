/*
 * FILE: 	CreatureWeightComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of creature weight
 */

package game;

import java.util.Comparator;

public class CreatureWeightComparator implements Comparator<CaveElement> {
	@Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Creature) c1).getWeight() >  ((Creature) c2).getWeight())
            return 1;
        else if ( ((Creature) c1).getWeight() <  ((Creature) c2).getWeight())
            return -1;
        else
            return 0;
    }
}
