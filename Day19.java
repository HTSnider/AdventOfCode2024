import java.io.*;
import java.util.*;

/**
 * Program for day 19 of Advent of Code.
 */
public class Day19 {

    // Hashmap to store the individual towel patterns, by their first letter.
    public static HashMap<String, ArrayList<String>> towelPatterns = new HashMap<String, ArrayList<String>>();
    // List to store the target designs.
    public static ArrayList<String> targetPatterns = new ArrayList<String>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * a line of comma separated Strings, each representing a sequence of 
     * colors on a towel, followed by an empty line, followed by N lines of
     * Strings, each representing a target sequence of colors.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input19.txt");
        Scanner in = new Scanner(inFile);
        prepPatternHashMap();
        String patterns = in.nextLine();
        for (String pat : patterns.split(", ")){
            towelPatterns.get(pat.split("")[0]).add(pat);
        }
        sortTowels();

        in.nextLine();
        while (in.hasNextLine()){
            targetPatterns.add(in.nextLine());
        }
        in.close();

        System.out.println(part1());
        System.out.println(part2());
    }

    /**
     * Helper function.
     * Initializes the towel pattern hashmap to ensure each possible key
     * has a list to store sequences of colors beginning with that color.
     */
    public static  void prepPatternHashMap(){
        for(char c = 'a'; c <= 'z'; c++){
            ArrayList<String> curr = new ArrayList<String>();
            towelPatterns.put(String.valueOf(c), curr);
        }
    }

    /**
     * Solution for part 1.
     * Given an infinite number of each type of towel, each with a given 
     * color sequence, and a list of target color sequences, find the 
     * number of sequences that are possible to make using the component
     * towel sequences.
     * @return possibleCount The number of target sequences that are 
     * possible to make with the given towels.
     */
    public static int part1(){
        int possibleCount = 0;
        for (int i = 0; i < targetPatterns.size(); i++){
            if( patternPossible(i)){
                possibleCount++;
            }
        }
        return possibleCount;
    }
    
    /**
     * Helper function for part 1.
     * Determines if the given sequence is possible to construct by
     * concatenating the component sequences.
     * @param i The index of the target sequence to evaluate.
     * @return true if the sequence can be constructed, or false
     * otherwise.
     */
    public static boolean patternPossible(int i){
        String targetPattern = targetPatterns.get(i);
        int targetLen = targetPattern.length();
        ArrayDeque<Integer> builder = new ArrayDeque<Integer>();
        for (String piece : towelPatterns.get(targetPattern.split("")[0])){
            if(piece.equals(targetPattern.substring(0, piece.length()))){
                builder.add(piece.length());
            }
        }
        HashSet<Integer> lenReached = new HashSet<Integer>();
        while (!builder.isEmpty()){
            int currNum = builder.pollFirst();
            if(lenReached.contains(currNum)){
                continue;
            }
            lenReached.add(currNum);
            if(currNum == targetLen){
                return true;
            }
            else if(currNum < targetLen){
                String nextStart = targetPattern.split("")[currNum];
                for (String piece : towelPatterns.get(nextStart)){
                    if(currNum+piece.length() <= targetLen && piece.equals(targetPattern.substring(currNum, currNum + piece.length()))){
                        builder.addLast(currNum+piece.length());
                    }
                }
            }
        }
        return false;
    }

    /**
     * Helper function for part 2.
     * Determines the number of ways to construct each substring
     * of the target sequence, up to the whole sequence, and 
     * returns the number of ways to construct the input 
     * sequence.
     * @param i The index of the target sequence.
     * @return The number of ways to construct the given sequence.
     */
    public static Long waysPossible(int i){
        String targetPattern = targetPatterns.get(i);
        int targetLen = targetPattern.length();
        Long[] ways = new Long[targetLen+1];
        for (int len = 0; len <= targetLen; len++){
            ways[len] = 0L;
        }
        ways[0] = 1L;
        for (int letterNum = 0; letterNum < targetLen; letterNum++){
            String letter = targetPattern.split("")[letterNum];
            for (String piece : towelPatterns.get(letter)){
                if (letterNum + piece.length() <= targetLen && 
                    piece.equals(targetPattern.substring(letterNum, letterNum+piece.length()))){
                    ways[letterNum + piece.length()] += ways[letterNum];
                }
            }
        }
        return ways[targetLen];
    }

    /**
     * Utility function.
     * Sorts the arrays in the towelPattern hashmap.
     */
    public static void sortTowels(){
        for (String key : towelPatterns.keySet()){
            Collections.sort(towelPatterns.get(key));
        }
    }

    /**
     * Solution for part 2.
     * Given the same list of component color sequence towels, and 
     * the same list of target color sequences, determine how many
     * ways it is possible to make each target color sequence.
     * @return The sum of the number of ways to construct each
     * target color sequence from the component color sequences.
     */
    public static Long part2(){
        Long designPermutations = 0L;
        for (int i = 0; i < targetPatterns.size(); i++){
            designPermutations += waysPossible(i);
        }
        return designPermutations;
    }

}
