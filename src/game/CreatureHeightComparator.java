/*
 * FILE: 	CreatureHeightComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of creature height
 */

package game;

import java.util.Comparator;

public class CreatureHeightComparator implements Comparator<CaveElement> {
	@Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Creature) c1).getHeight() >  ((Creature) c2).getHeight())
            return 1;
        else if ( ((Creature) c1).getHeight() <  ((Creature) c2).getHeight())
            return -1;
        else
            return 0;
    }
}
