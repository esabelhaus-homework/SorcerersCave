/*
 * FILE: 	CreatureFearComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of creature fear
 */

package game;

import java.util.Comparator;

public class CreatureFearComparator implements Comparator<CaveElement> {

    @Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Creature) c1).getFear() >  ((Creature) c2).getFear())
            return 1;
        else if ( ((Creature) c1).getFear() <  ((Creature) c2).getFear())
            return -1;
        else
            return 0;
    }

}
