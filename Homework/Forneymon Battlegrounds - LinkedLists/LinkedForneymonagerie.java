package main.forneymon.arena;

import java.util.Objects;
import main.forneymon.fmtypes.*;

// Halle Vogelpohl

/**
 * Collections of Forneymon ready to fight in the arena!
 */
public class LinkedForneymonagerie implements ForneymonagerieInterface {

    // Fields
    // -----------------------------------------------------------
    private Node sentinel;
    private int size, modCount;
    
    
    // Constructor
    // -----------------------------------------------------------
    public LinkedForneymonagerie () {
        this.size = this.modCount = 0;
        this.sentinel = new Node(null);
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
    }
   
    // Methods
    // -----------------------------------------------------------
    /**
     * Checks if the given LinkedForneymonagerie is empty
     * @return Return if this LinkedForneymonagerie is empty
     */
    public boolean empty () {
        return this.size == 0;
    }
    
    /*
     * Checks the number of Forneymon in the collection by returning the size of the collection.
     */   
    public int size () {
        return this.size;
    }
    
    /**
     * Returns true if toAdd was newly added to the LinkedForneymonagerie and
     * false otherwise.
     * @param toAdd is the Forneymon type the user wants to add to the LinkedForneymonagerie.
     * @return if the given Forneymon is of a new type and added to the end of the LinkedForneymonagerie.
     */  
    public boolean collect (Forneymon toAdd) { 
        var fmIndex = getTypeIndex(toAdd.getFMType());
        var fmToCheck = get(fmIndex);
        
        if (fmIndex != -1) {
            if (fmToCheck == toAdd) {
                return false;
            } else {
                fmToCheck.addLevels(toAdd.getLevel());
                modCount++;
                return false;
            }  
        }
        append(toAdd);
        size++;
        modCount++;
        return true;
    }
    
    /**
     * Removes the Forneymon of the given subtype from the
     * LinkedForneymonagerie and returns true. If the given fmType doesn't
     * exist then returns false.
     * @param fmType The type of Forneymon to be removed from the collection.
     * @return Returns true if the given Forneymon is being released, false otherwise.
     */
    public boolean releaseType (String fmType) {
        if (containsType(fmType)) {
            remove(getTypeIndex(fmType));
            return true;
        }
        return false;
    }
    
    /**
     * Returns the Forneymon at the given index in the LinkedForneymonagerie, if valid.
     * @param index The index being checked for the Forneymon
     * @return The Forneymon at the given index
     */ 
    public Forneymon get (int index) {
        var currentIndex = 0;
        for (Node n = this.sentinel.next; n != this.sentinel; n = n.next, currentIndex++) {
            if (currentIndex == index) {
                return n.fm;
            }
        }
        return null;
    }
    
    /**
     * Removes and returns the Fornyemon at the given index, if valid.
     * @param index The index of the Forneymon to be removed.
     * @return The Forneymon that was removed from the specified index.
     */
    public Forneymon remove (int index) {
        var removed = get(index);
        indexValidityCheck(index);  
        removeNode(index);
        this.size--;
        modCount++;
        return removed;
    }
    
    /**
     * Returns the index of a Forneymon with the given fmType. Returns -1 if 
     * the type isn't found.
     * @param fmType The Forneymon type checking if found in the LinkedForneymonagerie
     * @return The index where the Forneymon type is in the collection, or -1 if the
     * Forneymon type isn't found.
     */
    public int getTypeIndex (String fmType) {
        int index = 0;
        for (Node n = this.sentinel.next; n != this.sentinel; n = n.next, index++) {
            if (n.fm.getFMType().equals(fmType)) {
                return index;
            }
        }
        return -1;
    }
    
    /**
     * Checks if a given Forneymon exists within the LinkedForneymonagerie.
     * @param toCheck is the Forneymon type, as a String, specified by the user.
     * @return Returns true if the given Forneymon type is found within the LinkedForneymonagerie, false otherwise
     */
    public boolean containsType (String toCheck) {
        if (getTypeIndex(toCheck) == -1) { 
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Trades the contents of this LinkedForneymonagerie and another LinkedForneymonagerie
     * @param other Another LinkedForneymonagerie to which the current LinkedForneymonagerie is creating a deep copy.
     */
    public void trade (LinkedForneymonagerie other) {

        LinkedForneymonagerie temp = new LinkedForneymonagerie();
        
        temp.size = this.size;
        temp.modCount = this.modCount;
        temp.sentinel = this.sentinel;
        this.size = other.size;
        this.modCount = other.modCount;
        this.sentinel = other.sentinel;
        other.size = temp.size;
        other.modCount = temp.modCount;
        other.sentinel = temp.sentinel;
        
        this.modCount++;
        other.modCount++;
    }
    
    /**
     * @param fmType The type of Forneymon that is being rearranged.
     * @param index The index where the Forneymon is being moved.
     */
    public void rearrange (String fmType, int index) {
        indexValidityCheck(index);
        Node fmToMove = new Node (remove(getTypeIndex(fmType)));

        insertAt(fmToMove, index);     
        this.size++;
        modCount++;
    }
    
    /**
     * Returns a new Iterator on the LinkedForneymonagerie that begins on the first Node in the sequence.
     * @return The new Iterator
     */
    public LinkedForneymonagerie.Iterator getIterator () {
        if (empty()) {
            throw new IllegalStateException();
        }      
        return new Iterator(this);
    }
    
    /**
     * Clones a new LinkedForneymonagerie from the existing LinkedForneymonagerie
     * @return The new LinkedForneymonagerie cloned as a deep copy from the existing LinkedForneymonagerie
     */
    @Override
    public LinkedForneymonagerie clone () {
        LinkedForneymonagerie clone = new LinkedForneymonagerie();
        
        for (Node n = this.sentinel.next; n != this.sentinel; n = n.next) {
            clone.collect(n.fm.clone());
        }
        clone.modCount = this.modCount;
        return clone;
    }
    
    /**
     * Checks if another LinkedForneymonagerie is equal to the current LinkedForneymonagerie
     * @param other The other LinkedForneymonagerie which is being checked for equality with the 
     * current LinkedForneymonagerie
     * @return Returns true if the two LinkedForneymonagerie are equal
     */
    @Override
    public boolean equals (Object other) {
        LinkedForneymonagerie fmOther = ((LinkedForneymonagerie) other);
        for (Node n = this.sentinel.next, nOther = fmOther.sentinel.next; n != this.sentinel; n = n.next, nOther = nOther.next) {
            if (!n.fm.equals(nOther.fm)) { 
                return false;
            }
        }
        return true; 
    }
    
    @Override
    public int hashCode () {
        return Objects.hash(this.sentinel, this.size, this.modCount);
    }
    
    @Override
    public String toString () {
        String[] result = new String[size];
        int i = 0;
        for (Node curr = this.sentinel.next; curr != this.sentinel; curr = curr.next, i++) {
            result[i] = curr.fm.toString();
        }
        return "[ " + String.join(", ", result) + " ]";
    }
    
    
    // Private helper methods
    // -----------------------------------------------------------
   
    /**
     * Checks if a given index is invalid if
     * - index < 0
     * - index >= size
     * @param index The index being checked for validity:
     *  Must be less than the size and at least 0.
     */
    private void indexValidityCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException();
        }
    }
    
    private void removeNode (int index) {
        Node current = this.sentinel.next;
        
        while (index > 0) {
            current = current.next;
            index--;
        }    

        current.prev.next = current.next;
        current.next.prev = current.prev;
        current = current.prev;
    }
    
    /**
     * Appends a Forneymon to the end of the LinkedForneymonagerie
     * @param toAdd The Forneymon being appended to the LinkedForneymonagerie
     */
    private void append(Forneymon toAdd) {
        Node toAppend = new Node(toAdd);
        Node tail = this.sentinel.prev;
        
        tail.next = toAppend;
        toAppend.prev = tail;
        toAppend.next = this.sentinel;
        this.sentinel.prev = toAppend;
    }
    
    /**
     * Prepends a Forneymon to the beginning of the LinkedForneymonagerie
     * @param toAdd The Forneymon being prepended to the LinkedForneymonagerie
     */
    public void prepend (Forneymon toAdd) {
        Node newNode = new Node(toAdd);
        Node head = this.sentinel.next;
        
        newNode.next = head;
        newNode.prev = this.sentinel;
        head.prev = newNode;
        this.sentinel.next = newNode;
    }
    
    /**
     * Inserts a new Node at the given index
     * @param toAdd The new Node being inserted
     * @param index The index at which the Node is to be inserted
     */
    public void insertAt(Node toAdd, int index) {
        Node current = this.sentinel.next;
        
        while (index > 0) {
            current.prev = current;
            current = current.next;
            index--;
        }  
        current.prev.next = toAdd;
        toAdd.prev = current.prev;
        toAdd.next = current;
        current.prev = toAdd;
    }

    
    // Inner Classes
    // -----------------------------------------------------------
    
    public class Iterator {
        private LinkedForneymonagerie host;
        private Node current;
        private int itModCount;
        
        Iterator (LinkedForneymonagerie host) {
            this.host = host;
            this.current = host.sentinel.next;
            this.itModCount = host.modCount;
        }
        
        /**
         * 
         * @return Returns true if the Iterator is valid 
         * and its current.next is the host's Sentinel node, false otherwise.
         */
        public boolean atEnd () {
            return isValid() && (current.next == host.sentinel);
        }
        
        /**
         * 
         * @return Returns true if the Iterator is valid 
         * and its current.prev is the host's Sentinel node, false otherwise.
         */
        public boolean atStart () {
            return isValid() && (current.prev == host.sentinel);
        }
        
        /**
         * 
         * @return Returns true if this Iterator's itModCount agrees with that of its owner's modCount 
         * and if the host LinkedForneymonagerie has at least one element, false otherwise.
         */
        public boolean isValid () {
            return !host.empty() && (itModCount == host.modCount);
        }
        
        /**
         * 
         * @return The Forneymon stored in the Node that the Iterator is currently pointing at
         */
        public Forneymon getCurrent () {
            if (!isValid()) {
                throw new IllegalStateException();
            }
            return this.current.fm;
        }
        
        /**
         * Advances the Iterator's current reference 
         * to point to the next Node in the sequence.
         */
        public void next () {
            if (!isValid()) {
                throw new IllegalStateException();
            }
            if (atEnd()) {
                current = current.next.next;
            } else {
                current = current.next;
            }
        }
        
        /**
         * Advances the Iterator's current reference
         * to point to the previous Node in the sequence.
         */
        public void prev () {
            if (!isValid()) {
                throw new IllegalStateException();
            }
            if (atStart()) {
                current = current.prev.prev;
            } else {
                current = current.prev;
            }
        }
        
        /**
         * Removes the Node this Iterator references from the LinkedForneymonagerie
         * and moves the iterator to the Node preceding the one it deleted.
         * @return Returns a reference to the Forneymon of the removed Node
         */
        public Forneymon removeCurrent () {
            var fm = current.fm;
            if (!isValid()) {
                throw new IllegalStateException();
            }           
            removeNode(getTypeIndex(fm.getFMType()));
            prev();
            size--;
            itModCount++;
            modCount++;
            return fm;
        }
        
    }
    
    private class Node {
        Node next, prev;
        Forneymon fm;
        
        Node (Forneymon fm) {
            this.fm = fm;
        }
    }
    
}
