import java.io.*;
import java.util.*;

/**
 * Program for day 10 of Advent of Code.
 */
public class Day10 {
    /**
     * Main method that intakes and parses the input file,
     * which consists of N lines of M digits.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input10.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<ArrayList<Integer>> island= new ArrayList<ArrayList<Integer>>();
        while (in.hasNextLine()) {
            ArrayList<Integer> curr = new ArrayList<Integer>();
            for (String s: in.nextLine().split("")){
                curr.add(Integer.parseInt(s));
            }
            island.add(curr);
        }
        in.close();

        System.out.println(part1(island));
        System.out.println(part2(island));
    }

    /**
     * Solution for part 1.
     * On a topographical map of an island, a trail is a continuous path from 0 to 9,
     * where the height at each step is 1 higher than the previous. Steps may only be 
     * taken vertically or horizontallu. The trailhead is any point where the height 
     * is 0, and each trailhead is given a score based on the number of distinct 
     * positions with height 9 it can reach. The output is the sum of those scores.
     * @param island An ArrayList of ArrayLists of Integers, each being a single digit
     * between 0 and 9 representing the height of the position.
     * @return trailCount The sum of the number of distinct positions with height 9 
     * that can be reached from the trailheads.
     */
    public static int part1(ArrayList<ArrayList<Integer>> island){
        ArrayList<int[]> starts = findStarts(island);
        int trailCount = 0;
        for (int[] start: starts){
            trailCount += countTrails(island, start);
        }
        return trailCount;
    }

    /**
     * Helper function for both parts.
     * Finds the positions on the topographical map with height 0.
     * @param island An ArrayList of ArrayLists of Integers, each being a single digit
     * between 0 and 9 representing the height of the position.
     * @return starts An ArrayList of int[], where each value is 
     * [lineNumber, columnNumber, 0]
     */
    public static ArrayList<int[]> findStarts(ArrayList<ArrayList<Integer>> island){
        ArrayList<int[]> starts = new ArrayList<int[]>();
        for (int i=0; i<island.size(); i++){
            for (int j=0; j<island.get(0).size(); j++){
                if(island.get(i).get(j)==0){
                    starts.add(new int[] {i,j,0});
                }
            }
        }
        return starts;
    }

    /**
     * Helper function for part 1.
     * For a given trailhead, finds the number of distinct positions of height 9 that
     * can be found by taking only horizontal or vertical steps where the new position
     * is 1 higher.
     * @param island An ArrayList of ArrayLists of Integers, each being a single digit
     * between 0 and 9 representing the height of the position.
     * @param start The starting position, [lineNumber, columnNumber, 0]
     * @return The number of distinct positions of height 9 that can be reached.
     */
    public static int countTrails(ArrayList<ArrayList<Integer>> island, int[] start){
        ArrayList<int[]> positions = new ArrayList<int[]>();
        positions.add(start);
        HashSet<String> visited9s = new HashSet<String>();
        while(positions.size()>0){
            int[] currPos = positions.remove(0);
            // check up
            if(0<=currPos[0]-1 && currPos[0]-1<island.size() && 0<=currPos[1] && currPos[1]<island.get(0).size()){
                if( island.get(currPos[0]-1).get(currPos[1])==currPos[2]+1){
                    if(currPos[2]+1==9){
                        if(!visited9s.contains(String.valueOf(currPos[0]-1)+"-"+String.valueOf(currPos[1]))){
                            visited9s.add(String.valueOf(currPos[0]-1)+"-"+String.valueOf(currPos[1]));
                        }
                    }else{
                        positions.add(new int[] {currPos[0]-1, currPos[1], currPos[2]+1});
                    }
                }
            }
            // check down
            if(0<=currPos[0]+1 && currPos[0]+1<island.size() && 0<=currPos[1] && currPos[1]<island.get(0).size()){
                if( island.get(currPos[0]+1).get(currPos[1])==currPos[2]+1){
                    if(currPos[2]+1==9){
                        if(!visited9s.contains(String.valueOf(currPos[0]+1)+"-"+String.valueOf(currPos[1]))){
                            visited9s.add(String.valueOf(currPos[0]+1)+"-"+String.valueOf(currPos[1]));
                        } 
                    }else{
                        positions.add(new int[] {currPos[0]+1, currPos[1], currPos[2]+1});
                    }   
                }
            }
            // check left
            if(0<=currPos[0] && currPos[0]<island.size() && 0<=currPos[1]-1 && currPos[1]-1<island.get(0).size()){
                if( island.get(currPos[0]).get(currPos[1]-1)==currPos[2]+1){
                    if(currPos[2]+1==9 ){ 
                        if(!visited9s.contains(String.valueOf(currPos[0])+"-"+String.valueOf(currPos[1]-1))){
                            visited9s.add(String.valueOf(currPos[0])+"-"+String.valueOf(currPos[1]-1));
                        }
                    }else{
                        positions.add(new int[] {currPos[0], currPos[1]-1, currPos[2]+1});
                    }  
                }
            }
            // check right
            if(0<=currPos[0] && currPos[0]<island.size() && 0<=currPos[1]+1 && currPos[1]+1<island.get(0).size()){
                if( island.get(currPos[0]).get(currPos[1]+1)==currPos[2]+1){
                    if(currPos[2]+1==9){
                        if (!visited9s.contains(String.valueOf(currPos[0])+"-"+String.valueOf(currPos[1]+1))){
                            visited9s.add(String.valueOf(currPos[0])+"-"+String.valueOf(currPos[1]+1));
                        }
                    }else{
                        positions.add(new int[] {currPos[0], currPos[1]+1, currPos[2]+1});
                    }
                }
            }
        }
        
        return visited9s.size();
    }
    
    /**
     * Solution for part 2.
     * Similar to part 1, but the desired value is now the sum of then number of 
     * distinct paths from each trailhead to a position of height 9.
     * @param island An ArrayList of ArrayLists of Integers, each being a single digit
     * between 0 and 9 representing the height of the position.
     * @return trailRating The sum of the number of distinct paths from each trailhead 
     * ending at a position of height 9.
     */
    public static int part2(ArrayList<ArrayList<Integer>> island){
        int trailRating = 0;
        ArrayList<int[]> starts = findStarts(island);
        for (int[] start : starts){
            trailRating += rateTrail(island, start);
        }
        return trailRating;
    }

    /**
     * Helper function for part 2.
     * Finds the number of distinct paths from the trailhead to a position of height 9,
     * where each step must be horizontal or vertical, and the height of the new postion
     * must be 1 greater than the previous.
     * @param island An ArrayList of ArrayLists of Integers, each being a single digit
     * between 0 and 9 representing the height of the position.
     * @param start The starting position, [lineNumber, columnNumber, 0]
     * @return rating The number of distinct paths reachable from the given trailhead.
     */
    public static int rateTrail(ArrayList<ArrayList<Integer>> island, int[] start){
        int rating = 0;
        ArrayList<int[]> positions = new ArrayList<int[]>();
        positions.add(start);

        ArrayList<int[]> directions = new ArrayList<int[]>();
        directions.add(new int[] {-1,0});
        directions.add(new int[] {1,0});
        directions.add(new int[] {0,-1});
        directions.add(new int[] {0,1});

        while (positions.size()>0){
            int[] currPos = positions.remove(0);
            for (int[] dir : directions){
                if(0<=currPos[0]+dir[0] && currPos[0]+dir[0]<island.size() && 0<=currPos[1]+dir[1] && currPos[1]+dir[1]<island.get(0).size()){
                    if(island.get(currPos[0]+dir[0]).get(currPos[1]+dir[1])==currPos[2]+1){
                        if (currPos[2]+1==9){
                            rating += 1;
                        }else{
                            positions.add(new int[] {currPos[0]+dir[0], currPos[1]+dir[1], currPos[2]+1});
                        }
                    }
                }
            }
        }
        return rating;
    }
}
