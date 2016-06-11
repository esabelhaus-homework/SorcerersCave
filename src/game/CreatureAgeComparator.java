/*
 * FILE: 	CreatureAgeComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of creature age
 */

package game;

import java.util.Comparator;

public class CreatureAgeComparator implements Comparator<CaveElement> {
	@Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Creature) c1).getAge() >  ((Creature) c2).getAge())
            return 1;
        else if ( ((Creature) c1).getAge() <  ((Creature) c2).getAge())
            return -1;
        else
            return 0;
    }
}
