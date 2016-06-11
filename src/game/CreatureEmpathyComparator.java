/*
 * FILE: 	CreatureEmpathyComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of creature empathy
 */

package game;

import java.util.Comparator;

public class CreatureEmpathyComparator implements Comparator<CaveElement> {

    @Override
    public int compare(CaveElement c1, CaveElement c2) {
        if ( ((Creature) c1).getEmpathy() >  ((Creature) c2).getEmpathy())
            return 1;
        else if ( ((Creature) c1).getEmpathy() <  ((Creature) c2).getEmpathy())
            return -1;
        else
            return 0;
    }
}
