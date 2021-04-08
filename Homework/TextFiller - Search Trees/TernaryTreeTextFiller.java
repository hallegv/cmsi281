package main.textfill;

import java.util.*;

import org.w3c.dom.Node;

import tree.bst.BinarySearchTree.BSTNode;

/**
 * A ternary-search-tree implementation of a text-autocompletion
 * trie, a simplified version of some autocomplete software.
 * @author Halle Vogelpohl
 */
public class TernaryTreeTextFiller implements TextFiller {

    // -----------------------------------------------------------
    // Fields
    // -----------------------------------------------------------
    private TTNode root;
    private int size;
    
    // -----------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------
    public TernaryTreeTextFiller () {
        this.root = null;
        this.size = 0;
    }
    
    
    // -----------------------------------------------------------
    // Methods
    // -----------------------------------------------------------
    
    /**
     * Returns the number of stored terms inside of the TextFiller
     * @return Returns the number of stored terms inside of the TextFiller
     */
    public int size () {
        return this.size;
    }

    /**
     * @return Returns true if the TextFiller has no search terms stored, false otherwise
     */
    public boolean empty () {
        return this.root == null;
    }
    
    /**
     * Adds the given search term toAdd to the TextFiller
     * @param toAdd String to add to the TextFiller
     */
    public void add (String toAdd) {
        normalizeTerm(toAdd);
        if (contains(toAdd)) { return; }
        this.root = add(this.root, toAdd);
        this.size++;
    }
    
    /**
     * Returns true if the given String query exists within the TextFiller, false otherwise.
     * @param query String query that we're checking for existence in the Ternary Tree
     * @return Returns true if the query exists in the Ternary Tree, false otherwise
     */
    public boolean contains (String query) {
        return contains(this.root, query);       
    }
    
    /**
     * Returns the first search term contained in the TextFiller that possesses the query as a prefix.
     * If the given query is a prefix for NO search term, return null.
     * @param query The String query being checked
     * @return Returns the first search term containing the prefix of the query
     */
    public String textFill (String query) {
        return textFill(this.root, query);
    }
    
    /**
     * @return Returns an ArrayList of Strings of the alphabetically sorted search terms within this TextFiller.
     */
    public List<String> getSortedList () {
        return getSortedList(this.root, "", new ArrayList<String>());
    }
        
    // -----------------------------------------------------------
    // Helper Methods
    // -----------------------------------------------------------

    /**
     * Adds given String toAdd to the TextFiller
     * @param current The current TTNode
     * @param toAdd The String being added to the TextFiller
     * @return A reference to the Node containing the recently added String toAdd
     */
    private TTNode add (TTNode current, String toAdd) {
        if (current == null) {
            current = new TTNode(toAdd.charAt(0), toAdd.length() == 1, 0);
            addString(current, toAdd.substring(1));
            return current;
        } 
        
        int compare = compareChars(current.letter, toAdd.charAt(0));
        
        if (compare == 0) {
            if (toAdd.length() == 1) {
                current.wordEnd = true;
            } else {
                current.mid = add(current.mid, toAdd.substring(1));
            }
        }
        if (compare > 0) {
            current.left = add(current.left, toAdd);
        }
        if (compare < 0) {
            current.right = add(current.right, toAdd);
        }
        return current;
    }
     
    /**
     * Returns true if the TextFiller contains the query word, false otherwise
     * @param current The current TTNode
     * @param query The String query being checked
     * @return Returns true if the TextFiller contains the query word, false otherwise
     */
    private boolean contains (TTNode current, String query) {
       current = findQuery(this.root, query);
       return current != null && current.wordEnd;
    }
    
    /**
     * Adds the given String toAdd to the TextFiller down the middle references of the given TTNode current
     * @param current The current TTNode
     * @param toAdd The String being added to the TextFiller
     */
    private void addString(TTNode current, String toAdd) {
        char[] word = toAdd.toCharArray();
        for (char c : word) {
            current.mid = new TTNode(c, false, 0);
            current = current.mid;
        }
        current.wordEnd = true;
    }
    
    /**
     * Returns the first search term contained in the TextFiller that possesses the given prefix.
     * @param current The current TTNode
     * @param prefix The String prefix being checked
     * @return Return a String of the first search term contained in the TextFiller that possesses the given prefix.
     */
    private String textFill(TTNode current, String prefix) {       
        if (contains(normalizeTerm(prefix))) { return prefix; }  
        
        current = findQuery(current, prefix);
        
        if (current == null) { return null; }
        
        String suffix = "";
        while (!current.wordEnd) {
            current = current.mid;
            suffix += current.letter;
        }
        return prefix + suffix;
    }
    
    /**
     * Finds the String query in the TextFiller
     * @param current The current TTNode
     * @param query The String query whose last character is contained in the node returned
     * @return The node containing the last character of the String query
     */
    private TTNode findQuery(TTNode current, String query) {
        normalizeTerm(query);
        if (current == null) { return null; }
        
        int compare = compareChars(current.letter, query.charAt(0));

        if (compare == 0) {
            if (query.length() == 1) {
                return current;
            }             
            return findQuery(current.mid, query.substring(1));            
        }
        if (compare > 0) {
            return findQuery(current.left, query);
        }
        if (compare < 0) {
            return findQuery(current.right, query);
        }
        return null;
    }
       
    /**
     * Returns a List of the words in the TextFiller sorted in alphabetical order
     * @param current The current TTNode
     * @param placeholder String placeholder that collects the chars in each node to form words in the TextFiller 
     * @param result Returned List of the words in the TextFiller
     * @return result The List of words in the TextFiller
     **/
     private List<String> getSortedList(TTNode current, String placeholder, List<String> result) {        
         if (current == null) { return result; }        
         result = getSortedList(current.left, placeholder, result);
         if (current.wordEnd) { result.add(placeholder + current.letter); }
         result = getSortedList(current.mid, placeholder + current.letter, result);
         result = getSortedList(current.right, placeholder, result);
         return result;
     }
        
    /**
     * Normalizes a term to either add or search for in the tree,
     * since we do not want to allow the addition of either null or
     * empty strings within, including empty spaces at the beginning
     * or end of the string (spaces in the middle are fine, as they
     * allow our tree to also store multi-word phrases).
     * @param s The string to sanitize
     * @return The sanitized version of s
     */
    private String normalizeTerm (String s) {
        // Edge case handling: empty Strings illegal
        if (s == null || s.equals("")) {
            throw new IllegalArgumentException();
        }
        return s.trim().toLowerCase();
    }
       
    /**
     * Given two characters, c1 and c2, determines whether c1 is
     * alphabetically less than, greater than, or equal to c2
     * @param c1 The first character
     * @param c2 The second character
     * @return
     *   - some int less than 0 if c1 is alphabetically less than c2
     *   - 0 if c1 is equal to c2
     *   - some int greater than 0 if c1 is alphabetically greater than c2
     */
    private int compareChars (char c1, char c2) {
        return Character.toLowerCase(c1) - Character.toLowerCase(c2);
    } 
    
    // -----------------------------------------------------------
    // Extra Credit Methods
    // -----------------------------------------------------------
    
    /**
     * Adds the given search term toAdd with its associated priority int to the TextFiller
     * @param toAdd String to add to the TextFiller
     * @param priority Priority int associated with the String toAdd
     */
    public void add (String toAdd, int priority) {
        if (contains(normalizeTerm(toAdd))) { return; }
        this.root = add(this.root, toAdd, priority);
        this.size++;
    }
    
    /**
     * Returns the search term contained in the TextFiller that possesses the query as a prefix
     * and has the highest priority.
     * If the given query is a prefix for NO search term, returns null.
     * @param query The String query being checked
     * @return Returns the search term of the highest priority containing the prefix query
     */
    public String textFillPremium (String query) {
        if (contains(normalizeTerm(query))) { return query; }
        return textFillPremium(this.root, query, "");
    }
    
    // -----------------------------------------------------------
    // Extra Credit Helper Methods
    // -----------------------------------------------------------
    
    /**
     * Adds the given string to the TextFiller with its associated priority int
     * @param current The current node
     * @param toAdd The String to add to the TextFiller
     * @param priority The int priority associated with the added word
     */
    private void addStringPriority(TTNode current, String toAdd, int priority) {
        char[] word = toAdd.toCharArray();
        for (char c : word) {
            current.mid = new TTNode(c, false, priority);
            current.priority = priority;
            current = current.mid;
        }
        current.wordEnd = true;
        current.wordEndPriority = priority;
        current.priority = priority;
    }
    
    
    /**
     * Adds given String toAdd with its associated priority to the TextFiller
     * @param current The current TTNode
     * @param toAdd The String being added to the TextFiller
     * @param priority The int priority of the String
     * @return A reference to the Node containing the recently added String toAdd
     */
    private TTNode add(TTNode current, String toAdd, int priority) {
        normalizeTerm(toAdd);

        if (current == null) {
            current = new TTNode(toAdd.charAt(0), toAdd.length() == 1, priority);
            addStringPriority(current, toAdd.substring(1), priority);
            return current;
        } 
        
        int compare = compareChars(current.letter, toAdd.charAt(0));

        if (compare == 0) {
            current.mid = add(current.mid, toAdd.substring(1), priority);
        }
        if (compare > 0) {
            current.left = add(current.left, toAdd, priority);
        }
        if (compare < 0) {
            current.right = add(current.right, toAdd, priority);
        }
        return current;
    }
    
    /**
     * Returns the first search term contained in the TextFiller that possesses the query as a prefix.
     * @param current The current TTNode
     * @param prefix String prefix being checked
     * @param suffix String suffix of the word of highest priority that contains the prefix
     * @return Return a String of the highest priority word that possesses the query as a prefix.
     */
    private String textFillPremium(TTNode current, String prefix, String suffix) {
        current = findQuery(this.root, normalizeTerm(prefix));
        if (current == null) { return null; }
        int priority = findQuery(this.root, prefix).priority;

        while (current.wordEndPriority != priority) {
           if (current.mid != null && current.mid.priority >= priority) {
               current = current.mid;
           }
           if (current.right != null && current.right.priority >= priority) {
               current = current.right;
           }
           if (current.left != null && current.left.priority >= priority) {
               current = current.left;
           }
           suffix += current.letter;  
        }
        return prefix + suffix;
    }
    
    // -----------------------------------------------------------
    // TTNode Internal Storage
    // -----------------------------------------------------------
    
    /**
     * Internal storage of autocompleter search terms
     * as represented using a Ternary Tree with TTNodes
     */
    private class TTNode {
        
        boolean wordEnd;
        char letter;
        int priority,
            wordEndPriority;
        TTNode left, mid, right;
        
        /**
         * Constructs a new TTNode containing the given character
         * and whether or not it represents a word-end, which can
         * then be added to the existing tree.
         * @param c Letter to store at this node
         * @param w Whether or not this is a word-end
         */
        TTNode (char c, boolean w, int p) {
            letter  = c;
            wordEnd = w;
            priority = p;
        }
        
    }
    
}
