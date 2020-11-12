package main.forneymon.arena;

import main.forneymon.fmtypes.Forneymon;

// Halle Vogelpohl

/**
 * Contains methods for facing off LinkedForneymonagerie against one another!
 */
public class LinkedForneymonArena {
    
    public static final int BASE_DAMAGE = 5;
    
    /**
     * Conducts a fight between two LinkedForneymonagerie, consisting of the following
     * steps, assisted by Iterators on each LinkedForneymonagerie:
     * <ol>
     *   <li>Forneymon from each LinkedForneymonagerie are paired to fight, in sequence
     *     starting from index 0.</li>
     *  <li>Forneymon that faint (have 0 or less health) are removed from their
     *    respective LinkedForneymonagerie.</li>
     *  <li>Repeat until one or both Forneymonagerie have no remaining Forneymon.</li>     
     * </ol>
     * @param fm1 One of the fighting LinkedForneymonagerie
     * @param fm2 One of the fighting LinkedForneymonagerie
     */
    public static void fight (LinkedForneymonagerie fm1, LinkedForneymonagerie fm2) { 
        LinkedForneymonagerie.Iterator fm1it = fm1.getIterator();
        LinkedForneymonagerie.Iterator fm2it = fm2.getIterator();
        
        while (!fm1.empty() && !fm2.empty()) { 
            Forneymon fmFighter1 = fm1it.getCurrent();
            Forneymon fmFighter2 = fm2it.getCurrent();
            
            fmFighter1.takeDamage(BASE_DAMAGE + fmFighter2.getLevel(), fmFighter2.getDamageType());           
            fmFighter2.takeDamage(BASE_DAMAGE + fmFighter1.getLevel(), fmFighter1.getDamageType());
            
            if (fmFighter1.getHealth() <= 0) { 
                fm1it.removeCurrent();

            }
            if (fmFighter2.getHealth() <= 0) { 
                fm2it.removeCurrent();
            }
            
            if (fm1.empty() || fm2.empty()) { break; }
            
            fm1it.next();
            fm2it.next();
        
        }        
    }
    
}
