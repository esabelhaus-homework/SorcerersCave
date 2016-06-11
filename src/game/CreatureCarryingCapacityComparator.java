/*
 * FILE: 	CreatureCarryingCapacityComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of creature carrying capacity
 */

package game;

import java.util.Comparator;

public class CreatureCarryingCapacityComparator implements Comparator<CaveElement> {

	@Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Creature) c1).getCarryingCapacity() >  ((Creature) c2).getCarryingCapacity())
            return 1;
        else if ( ((Creature) c1).getCarryingCapacity() <  ((Creature) c2).getCarryingCapacity())
            return -1;
        else
            return 0;
    }
	
}
