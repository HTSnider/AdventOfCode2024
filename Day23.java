import java.io.*;
import java.util.*;

/**
 * Program of day 23 of Advent of Code 2024.
 */
public class Day23 {

    // HashMap to hold the network, where each key is a computer, and each value is 
    // a set of the computers connected to that computer.
    public static HashMap<String, HashSet<String>> computers = new HashMap<String, HashSet<String>>();
    // A set to keep track of which computers have been checked already
    public static HashSet<String> visited = new HashSet<String>();
    // A set of all triples of interconnected computers, where 1 computer begins with t
    public static HashSet<String> triplesSet = new HashSet<String>();
    // A list of large interconnected sets of computers, where the first element is the largest.
    public static ArrayList<HashSet<String>> largestInterconnectedSets = new ArrayList<HashSet<String>>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * N lines, each with two alphabetical strings separated by a -.
     * Each alphabetical string is a computer label, and each pair is an
     * undirected connection between those two computers.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input23.txt");
        Scanner in = new Scanner(inFile);
        while (in.hasNextLine()){
            String[] temp = in.nextLine().split("-");
            HashSet<String> connections = computers.getOrDefault(temp[0], new HashSet<String>());
            connections.add(temp[1]);
            computers.put(temp[0], connections);
            connections = computers.getOrDefault(temp[1], new HashSet<String>());
            connections.add(temp[0]);
            computers.put(temp[1], connections);
        }
        in.close();

        System.out.println(part1());
        System.out.println(part2());
    }

    /**
     * Solution for part 1.
     * Find each unordered triple of three computers that are all interconnected, 
     * and count the ones where one of the computers starts with t.
     * @return The number of unique triples of interconnected computers where
     * one of the computers starts with t.
     */
    public static int part1(){
        int ans = 0;
        for (String comp : computers.keySet()){
            visited.add(comp);
            ans += checkTriple(comp);
        }

        return ans;
    }

    /**
     * Helper function for part 1.
     * Finds the number of unique interconnected triples of computers where one
     * begins with t, that include the given computer.
     * @param firstHash The name of the computer.
     * @return The number of unordered triples of interconnected computers 
     * including the input computer that contain a computer starting with t.
     */
    public static int checkTriple(String firstHash){
        int triplesWithT = 0;
        String secondHash;
        HashSet<String> first = computers.get(firstHash);
        HashSet<String> second;
        Iterator<String> compIterator = first.iterator();
        while(compIterator.hasNext()){
            secondHash = compIterator.next();
            if(visited.contains(secondHash)){
                continue;
            }
            second = computers.get(secondHash);
            Iterator<String> secondIterator = second.iterator();
            while(secondIterator.hasNext()){
                String thirdHash = secondIterator.next();
                if(thirdHash.equals(firstHash) || thirdHash.equals(secondHash)){
                    continue;
                }
                if(first.contains(thirdHash)){
                    // Then a triple exists
                    if(firstHash.startsWith("t") ||
                        secondHash.startsWith("t") ||
                        thirdHash.startsWith("t")){
                            triplesWithT ++;
                            ArrayList<String> triple = new ArrayList<String>();
                            triple.add(firstHash);
                            triple.add(secondHash);
                            triple.add(thirdHash);
                            Collections.sort(triple);
                            triplesSet.add(String.format("%s:%s:%s", triple.get(0), triple.get(1), triple.get(2)));
                    }
                }
            }
        }
        return triplesWithT/3;
    }

    /**
     * Solution for part 2.
     * Find the biggest set of interconnected computers, and their component
     * computers, in alphabetical order, concatenated with ','
     * @return
     */
    public static String part2(){
        visited.clear();
        HashSet<String> vertices = new HashSet<String>();
        vertices.addAll(computers.keySet());
        BronKerbosch(new HashSet<String>(), vertices, new HashSet<String>());;
        ArrayList<String> LANComps = new ArrayList<String>();
        largestInterconnectedSets.get(0).forEach((String s) -> LANComps.add(s));
        Collections.sort(LANComps);
        return String.join(",", LANComps);
    }

    /**
     * Helper function for part 2.
     * The Bron-Kerbosch recursive algorithm for finding the maximal clique, without
     * pivots.
     * @param R The current clique, as a set.
     * @param P The set of all computers that could be added to the current clique.
     * @param X The set of computers excluded from the current clique.
     */
    public static void BronKerbosch(HashSet<String> R, HashSet<String> P, HashSet<String> X){
        if (P.isEmpty() && X.isEmpty()){
            if(largestInterconnectedSets.isEmpty() || R.size()> largestInterconnectedSets.get(0).size()){
                largestInterconnectedSets.add(0, R);
            }
            return;
        }
        HashSet<String> candidates = new HashSet<String>();
        candidates.addAll(P);
        for (String v : candidates){
            HashSet<String> V = new HashSet<String>();
            if(visited.contains(v)){
                continue;
            }
            visited.add(v);
            V.add(v);
            HashSet<String> Nv = computers.get(v);
            BronKerbosch(union(R, V), intersect(P,Nv), intersect(X, Nv));
            P.remove(v);
            X.add(v);
        }

    }

    /**
     * Helper function for part 2.
     * Returns the union of the two sets.
     * @param set1 The first input set.
     * @param set2 The second input set.
     * @return A set containing all the elements of set1 and set2.
     */
    public static HashSet<String> union(HashSet<String> set1, HashSet<String> set2){
        HashSet<String> out = new HashSet<String>();
        out.addAll(set1);
        out.addAll(set2);
        return out;
    }

    /**
     * Helper function for part 2.
     * Returns the intersection of the two sets.
     * @param set1 The first input set.
     * @param set2 The second input set.
     * @return A set containing the elements of set1 that are also in set2.
     */
    public static HashSet<String> intersect(HashSet<String> set1, HashSet<String> set2){
        HashSet<String> out = new HashSet<String>();
        Iterator<String> it = set1.iterator();
        while(it.hasNext()){
            String curr = it.next();
            if (set2.contains(curr)){
                out.add(curr);
            }
        }
        return out;
    }

    /**
     * Helper function for part 2.
     * Returns the first set less the second set.
     * @param set1 The first input set.
     * @param set2 The second input set.
     * @return A set containing the elements of set1 that are not in set2.
     */
    public static HashSet<String> relativeCompliment(HashSet<String> set1, HashSet<String> set2){
        HashSet<String> out = new HashSet<String>();
        out.addAll(set1);
        out.removeAll(set2);
        return out;
    }
}
