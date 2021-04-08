package main.wiki;

import java.util.*;

// Halle Vogelpohl

public class WikiWalker {
    
    private HashMap<String, HashMap<String, Integer>> siteMap;

    public WikiWalker() {
        this.siteMap = new HashMap<String, HashMap<String, Integer>>();
    }

    /**
     * Adds an article with the given name to the site map and associates the
     * given linked articles found on the page. Duplicate links in that list are
     * ignored, as should an article's links to itself.
     * 
     * @param articleName
     *            The name of the page's article
     * @param articleLinks
     *            List of names for those articles linked on the page
     */
    public void addArticle(String articleName, List<String> articleLinks) {
        siteMap.put(articleName, new HashMap<String, Integer>());
        for (int i = 0; i < articleLinks.size(); i++) { 
            siteMap.get(articleName).put(articleLinks.get(i), 0);
        }
    }

    /**
     * Determines whether or not, based on the added articles with their links,
     * there is *some* sequence of links that could be followed to take the user
     * from the source article to the destination.
     * 
     * @param src
     *            The beginning article of the possible path
     * @param dest
     *            The end article along a possible path
     * @return boolean representing whether or not that path exists
     */
    public boolean hasPath(String src, String dest) {
        return hasPath(src, dest, new ArrayList<String>());
    }
    
    /**
     * Private helper for public method hasPath.
     * Determines whether or not, based on the added articles with their links,
     * there is *some* sequence of links that could be followed to take the user
     * from the source article to the destination.
     * 
     * @param src
     *            The beginning article of the possible path
     * @param dest
     *            The end article along a possible path
     * @param visited
     *             ArrayList<String> of the visited articles
     * @return boolean representing whether or not that path exists
     */
    private boolean hasPath(String src, String dest, ArrayList<String> visited) {
        if (visited.contains(src)) { return false; }
        if (src.equals(dest)) { return true; }
        visited.add(src);
        if (siteMap.containsKey(src)) {
            for (Map.Entry<String, Integer> innerMap : siteMap.get(src).entrySet()) {
                String current = innerMap.getKey();
                if (current == dest || hasPath(current, dest, visited)) { return true; }
            }
        }
        return false;
    }

    /**
     * Increments the click counts of each link along some trajectory. For
     * instance, a trajectory of ["A", "B", "C"] will increment the click count
     * of the "B" link on the "A" page, and the count of the "C" link on the "B"
     * page. Assume that all given trajectories are valid, meaning that a link
     * exists from page i to i+1 for each index i
     * 
     * @param traj
     *            A sequence of a user's page clicks; must be at least 2 article
     *            names in length
     */
    public void logTrajectory(List<String> traj) {
        for (int i = 0; i < traj.size() - 1; i++) {
            String current = traj.get(i);
            String next = traj.get(i + 1);
            HashMap<String, Integer> innerMap = siteMap.get(current);
            innerMap.replace(next, innerMap.get(next) + 1);
        }
    }

    /**
     * Returns the number of clickthroughs recorded from the src article to the
     * destination article. If the destination article is not a link directly
     * reachable from the src, returns -1.
     * 
     * @param src
     *            The article on which the clickthrough occurs.
     * @param dest
     *            The article requested by the clickthrough.
     * @throws IllegalArgumentException
     *             if src isn't in site map
     * @return The number of times the destination has been requested from the
     *         source.
     */
    public int clickthroughs(String src, String dest) {
        if (!siteMap.containsKey(src)) { throw new IllegalArgumentException(); }
        if (!siteMap.get(src).containsKey(dest)) { return -1; }
        return siteMap.get(src).get(dest);
    }

    /**
     * Based on the pattern of clickthrough trajectories recorded by this
     * WikiWalker, returns the most likely trajectory of k clickthroughs
     * starting at (but not including in the output) the given src article.
     * Duplicates and cycles are possible outputs along a most likely trajectory. In
     * the event of a tie in max clickthrough "weight," this method will choose
     * the link earliest in the ascending alphabetic order of those tied.
     * 
     * @param src
     *            The starting article of the trajectory (which will not be
     *            included in the output)
     * @param k
     *            The maximum length of the desired trajectory (though may be
     *            shorter in the case that the trajectory ends with a terminal
     *            article).
     * @return A List containing the ordered article names of the most likely
     *         trajectory starting at src.
     */
    public List<String> mostLikelyTrajectory(String src, int k) {
        List<String> traj = new ArrayList<String>();
        String maxString = null;
        while (k > 0) {
            if (siteMap.containsKey(src)) {
                for (Map.Entry<String, Integer> innerMap : siteMap.get(src).entrySet()) {
                    String innerString = innerMap.getKey();
                    int maxValue = innerMap.getValue();
                    if (maxString == null || maxValue > siteMap.get(src).get(maxString)) {
                        maxString = innerString;
                    } else if (maxValue == siteMap.get(src).get(maxString)) {
                        maxString = (innerString.compareToIgnoreCase(maxString) < 0) ? innerString : maxString; 
                    }
                }
                traj.add(maxString);
                src = maxString;
                maxString = null;
                k--;
            } else {
                k = 0;
            }
        }
        return traj;
    }
    
}
