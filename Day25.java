import java.io.*;
import java.util.*;

/**
 * Program for day 25 of Advent of Code 2024.
 */
public class Day25 {
    // ArrayList of 2d String arrays, each containing a key schematic.
    public static ArrayList<ArrayList<ArrayList<String>>> keys = new ArrayList<ArrayList<ArrayList<String>>>();
    // ArrayList of 2d String arrays, each containing a lock schematic.
    public static ArrayList<ArrayList<ArrayList<String>>> locks = new ArrayList<ArrayList<ArrayList<String>>>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * multi-line grids of . and #, each grid separated by an empty line;
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input25.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<ArrayList<String>> curr = new ArrayList<ArrayList<String>>();
        while (in.hasNextLine()){
            String line = in.nextLine();
            if(!line.equals("")){
                ArrayList<String> currLine = new ArrayList<String>();
                for (String s : line.split("")){
                    currLine.add(s);
                }
                curr.add(currLine);
            }else{
                if(curr.get(0).get(0).equals("#")){
                    locks.add(new ArrayList<ArrayList<String>>(curr));
                }else{
                    keys.add(new ArrayList<ArrayList<String>>(curr));
                }
                curr = new ArrayList<ArrayList<String>>();
            }
        }
        if(curr.get(0).get(0).equals("#")){
            locks.add(new ArrayList<ArrayList<String>>(curr));
        }else{
            keys.add(new ArrayList<ArrayList<String>>(curr));
        }
        in.close();

        System.out.println(part1());
        printLockAndKey(locks.get(0), keys.get(0));
    }

    /**
     * Helper function for parsing the input, now unused.
     * Used to convert each key schematic into an array of Integers,
     * each being the height of the key in the corresponding column.
     * @param schematic A 2d array of . and #.
     */
    public static void parseKey(ArrayList<ArrayList<String>> schematic){
        ArrayList<Integer> key = new ArrayList<Integer>();
        for (int col = 0; col < schematic.get(0).size(); col++){
            for (int row = 0; row < schematic.size(); row++){
                if(schematic.get(row).get(col).equals("#")){
                    key.add(6-row);
                    break;
                }
            }
        }
        //keys.add(key);
    }

    /**
     * Helper function for parsing the input, now unused.
     * Used to convert each lock schematic into an array of Integers,
     * each being the height of the lcok in the corresponding column.
     * @param schematic A 2d array of . and #.
     */
    public static void parseLock(ArrayList<ArrayList<String>> schematic){
        ArrayList<Integer> lock = new ArrayList<Integer>();
        for (int col = 0; col < schematic.get(0).size(); col++){
            for (int row = 0; row < schematic.size(); row++){
                if(schematic.get(row).get(col).equals(".")){
                    lock.add(row-1);
                    break;
                }
            }
        }
        //locks.add(lock);
    }

    /**
     * Solution for part 1.
     * For the given keys and locks, find every key and lock pair
     * such that the key fits in the lock, ie the # in the grids 
     * do not overlap.
     * @return The number of key-lock pairs that do not overlap.
     */
    public static int part1(){
        int fitCount = 0;
        for(ArrayList<ArrayList<String>> lock : locks){
            for(ArrayList<ArrayList<String>> key: keys){
                if (keyFitsLock(lock, key)){
                    fitCount++;
                }
            }
        }
        return fitCount;
    }

    /**
     * Helper function for part 1.
     * Determines if the give key fits in the given lock.
     * @param lock The 2d lock grid, with # along the top row
     * @param key The 2d key grid, with # along the bottom row
     * @return false if any position is # on both grids, and true otherwise
     */
    public static boolean keyFitsLock(ArrayList<ArrayList<String>> lock, ArrayList<ArrayList<String>> key){
        for (int i = 0 ; i < lock.size(); i++){
            for (int j = 0; j < lock.get(0).size(); j++){
                if(lock.get(i).get(j).equals("#") && key.get(i).get(j).equals("#")){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Diagnostic function.
     * Prints a given lock and key schematic to the console.
     * @param lock A 2d grid of . and #
     * @param key A 2d grid of . and #
     */
    public static void printLockAndKey(ArrayList<ArrayList<String>> lock, ArrayList<ArrayList<String>> key){
        for (int i = 0; i < lock.size(); i++){
            for (int j = 0; j < lock.get(0).size(); j++){
                System.out.print(lock.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println();
        for(int i = 0; i < key.size(); i++){
            for (int j = 0; j < key.get(0).size(); j++){
                System.out.print(key.get(i).get(j));
            }
            System.out.println();
        }
    }
}
