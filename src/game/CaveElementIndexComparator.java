/*
 * FILE: 	CaveElementIndexComparator.java
 * DATE:  	06/10/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Enable uniform comparison of all cave elements by index
 */

package game;

import java.util.Comparator;

public class CaveElementIndexComparator implements Comparator<CaveElement> {

	@Override
    public int compare(CaveElement o1, CaveElement o2) {
		if (o1.getIndex() > o2.getIndex() )
            return 1;
        else if ( o1.getIndex() < o2.getIndex() )
            return -1;
        else
            return 0;
    }
	
}
