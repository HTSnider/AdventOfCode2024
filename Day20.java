import java.io.*;
import java.util.*;

/**
 * Program for day 20 of Advent of Code 2024.
 */
public class Day20 {

    // Grid to hold a 2d maze, with # being walls, . being empty spaces, 
    //      S the starting space, and E the ending space
    public static ArrayList<ArrayList<String>> maze = new ArrayList<ArrayList<String>>();
    // Grid to hold the distance from the current position to the end position, or -1 
    //      for unreachable positions
    public static ArrayList<ArrayList<Integer>> endTimeGrid = new ArrayList<ArrayList<Integer>>();
    // Grid to hold the distance from the current position to the start positino, or -1
    //      for unreachable positions
    public static ArrayList<ArrayList<Integer>> startTimeGrid = new ArrayList<ArrayList<Integer>>();
    // A HashMap to hold possible cheats of length 2 or less, where each cheat is identified
    //      by its starting and ending positions, and the value is the distance from the 
    //      start of the maze to the end when using this cheat as a shortcut
    public static HashMap<String, Integer> cheatMap = new HashMap<String, Integer>();
    // A HashMap to hold possible cheats, where each cheat is identified by its starting
    //      and ending positions, and the value is the distance from the start of the maze
    //      to the end when using this cheat as a shortcut
    public static HashMap<String, Integer> longCheatMap = new HashMap<String, Integer>();
    // An arraylist to hold vectors for each horizontal and vertical direction
    public static ArrayList<Integer[]> directions = new ArrayList<Integer[]>();
    // An arraylist to hold vectors for each possible movement within 20 steps
    public static ArrayList<Integer[]> cheatDirections = new ArrayList<Integer[]>();
    // A HashMap to hold possible cheats of length 20 or less, where each cheat is identified
    //      by its starting and ending positions, and the value is the distance from the 
    //      start of the maze to the end when using this cheat as a shortcut
    public static ArrayList<Integer[]> longCheatDirections = new ArrayList<Integer[]>();

    /**
     * Main method which intakes and parses the input file, which consists of
     * N lines of M characters, where each # represents a wall, each . 
     * represents an empty space, and S and E represent the starting and 
     * ending positions of the maze.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input20.txt");
        Scanner in = new Scanner(inFile);
        Integer[] start = new Integer[2];
        Integer[] end = new Integer[2];
        int i = 0;
        while(in.hasNextLine()){
            ArrayList<String> curr = new ArrayList<String>();
            String[] temp = in.nextLine().split("");
            for (int j = 0; j < temp.length; j++){
                curr.add(temp[j]);
                if (temp[j].equals("S")){
                    start[0] = i;
                    start[1] = j;
                }
                if (temp[j].equals("E")){
                    end[0] = i;
                    end[1] = j;
                }
            }
            maze.add(curr);
            i++;
        }
        in.close();
        initDirections();
        initTimeGrids();

        // Part 1
        System.out.println(part1(start, end));
        // Part 2
        System.out.println(part2(start, end));
    }

    /**
     * Utility function.
     * Populates the direction lists with direction vectors.
     */
    public static void initDirections(){
        directions.add(new Integer[] {0, 1});
        directions.add(new Integer[] {1, 0});
        directions.add(new Integer[] {0, -1});
        directions.add(new Integer[] {-1, 0});

        cheatDirections.add(new Integer[] {0, 2});
        cheatDirections.add(new Integer[] {0, -2});
        cheatDirections.add(new Integer[] {2, 0});
        cheatDirections.add(new Integer[] {-2, 0});
        cheatDirections.add(new Integer[] {1, 1});
        cheatDirections.add(new Integer[] {1, -1});
        cheatDirections.add(new Integer[] {-1, 1});
        cheatDirections.add(new Integer[] {-1, -1});

        for (int i = -20; i <= 20; i++){
            for (int j = -(20 - Math.abs(i)); j <= 20 - Math.abs(i); j++){
                longCheatDirections.add(new Integer[] {i,j});
            }
        }
    }

    /**
     * Utility function
     * Initializes the Time grids to be N x M grids of values -1, where
     * N is the number of rows in the maze, and M is the number of columns
     * in the maze.
     */
    public static void initTimeGrids(){
        for (int i = 0; i < maze.size(); i++){
            ArrayList<Integer> curr = new ArrayList<Integer>();
            ArrayList<Integer> currEnd = new ArrayList<Integer>();
            for (int j = 0; j < maze.get(0).size(); j++){
                curr.add(-1);
                currEnd.add(-1);
            }
            startTimeGrid.add(curr);
            endTimeGrid.add(currEnd);
            
        }
    }

    /**
     * Solution for part 1.
     * In a 2d maze, a runner can, one time only, activate a cheat, to enable 
     * them to pass through walls for 2 steps. Find the amount of steps saved 
     * by each possible cheat over the shortest non-cheating path, and return
     * the count of the cheats that save at least 100 steps.
     * @param start An Integer[] [startingRow, startingColumn]
     * @param end An Integer[] [endingRow, endingColumn]
     * @return cheatCount The number of cheats that save at least 100 steps
     */
    public static int part1(Integer[] start, Integer[] end){
        int cheatCount = 0;
        // find shortest path from each open space
        getShortestPathsFromEnd(end);
        getShortestPathsFromStart(start);
        // find shortest path from start
        int raceLen = endTimeGrid.get(start[0]).get(start[1]);

        // for each walkable spot, find each way to use a cheat to save time
        //      hash each cheat by start and end position to prevent duplicates, with
        //      value being the new length of the race
        for (int i = 0; i < maze.size(); i++){
            for (int j = 0; j < maze.get(0).size(); j++){
                if (!maze.get(i).get(j).equals("#")){
                    findCheats(new Integer[] {i, j}, raceLen);
                }  
            }
        }
        // Count the cheats that result in a time save greater than 100
        for (Map.Entry<String, Integer> e : cheatMap.entrySet()){
            if (raceLen - e.getValue() >= 100){
                cheatCount++;
            }
        }
        return cheatCount;
    }

    /**
     * Helper function for both parts.
     * Finds the shortest path from the end position to each possible position
     * using Dijkstra's algorithm, and records them in the endTimeGrid.
     * @param end
     */
    public static void getShortestPathsFromEnd(Integer[] end){
        // find distance to end from each spot, using dijkstra
        PriorityQueue<Integer[]> processQueue = new PriorityQueue<Integer[]>(
            (Integer[] a1, Integer[] a2) -> {return a1[2].compareTo(a2[2]);});
        processQueue.add(new Integer[] {end[0], end[1], 0});
        HashSet<String> visited = new HashSet<String>();
        while(!processQueue.isEmpty()){
            Integer[] curr = processQueue.poll();
            endTimeGrid.get(curr[0]).set(curr[1], curr[2]);
            visited.add(positionToHashString(curr));
            for(Integer[] dir : directions){
                Integer[] nextStep = new Integer[] {curr[0] + dir[0], curr[1] + dir[1], curr[2] + 1};
                String nextHash = positionToHashString(nextStep);
                if(validPosition(nextStep) && !visited.contains(nextHash)){
                    processQueue.add(nextStep);
                }
            }
        }
    }

    /**
     * Helper function for both parts.
     * Finds the shortest path from the starting position to each possible position
     * using Dijkstra's algorithm, and records them in the startTimeGrid.
     * @param start
     */
    public static void getShortestPathsFromStart(Integer[] start){
        // find distance to end from each spot, using dijkstra
        PriorityQueue<Integer[]> processQueue = new PriorityQueue<Integer[]>(
            (Integer[] a1, Integer[] a2) -> {return a1[2].compareTo(a2[2]);});
        processQueue.add(new Integer[] {start[0], start[1], 0});
        HashSet<String> visited = new HashSet<String>();
        while(!processQueue.isEmpty()){
            Integer[] curr = processQueue.poll();
            startTimeGrid.get(curr[0]).set(curr[1], curr[2]);
            visited.add(positionToHashString(curr));
            for(Integer[] dir : directions){
                Integer[] nextStep = new Integer[] {curr[0] + dir[0], curr[1] + dir[1], curr[2] + 1};
                String nextHash = positionToHashString(nextStep);
                if(validPosition(nextStep) && !visited.contains(nextHash)){
                    processQueue.add(nextStep);
                }
            }
        }
    }

    /**
     * Helper function
     * Converts a position vector to a String for hashing, where the hash is
     * "rowNumber:columnNumber"
     * @param position An Integer[] [rowNumber, columnNumber]
     * @return "rowNumber:columnNumber"
     */
    public static String positionToHashString(Integer[] position){
        return String.valueOf(position[0]) + ":" + String.valueOf(position[1]); 
    }

    /**
     * Helper function
     * Converts a hash string to its position vector.
     * @param hashString A String "rowNumber:columnNumber"
     * @return An Integer[] [rowNumber, columnNumber]
     */
    public static Integer[] hashStringToPosition(String hashString){
        String[] temp = hashString.split(":");
        return new Integer[] {Integer.parseInt(temp[0]), Integer.parseInt(temp[1])};
    }

    /**
     * Helper function
     * Determines if a given position is within the bounds of the maze,
     * and not inside a wall.
     * @param position An Integer[] [rowNumber, columnNumber]
     * @return true if the position is walkable within the maze, or false otherwise
     */
    public static boolean validPosition(Integer[] position){
        int rowNum = position[0];
        int colNum = position[1];
        if (0 <= rowNum && rowNum < maze.size() &&
         0 <= colNum && colNum < maze.get(0).size() &&
         !maze.get(rowNum).get(colNum).equals("#")){
            return true;
         }
        return false;
    }

    /**
     * Helper function for part 1.
     * For the given position, finds possible cheats of length 2 or less starting 
     * at that position, and ending at a valid position, where the distance from 
     * the start of the maze to the end using this cheat is less than the given 
     * non-cheating length. Records these cheats in cheatMap.
     * @param position An Integer[] [rowNumber, columnNumber]
     * @param raceLen The length of the race when not cheating.
     */
    public static void findCheats(Integer[] position, Integer raceLen){
        int startDistance = startTimeGrid.get(position[0]).get(position[1]);
        for (Integer[] dir : cheatDirections){
            Integer[] nextPosition = new Integer[] {position[0] + dir[0], position[1] + dir[1]};
            if(!validPosition(nextPosition)){
                continue;
            }
            String nextHash = positionToHashString(nextPosition);
            String cheatHash = positionToHashString(position) + ":" + nextHash;
            int endDistance = endTimeGrid.get(nextPosition[0]).get(nextPosition[1]);
            if(!cheatMap.containsKey(cheatHash) && startDistance+endDistance+2 < raceLen){
                cheatMap.put(cheatHash, startDistance + endDistance + 2);
            }
        }
    }

    /**
     * Helper function for part 2.
     * For the given position, finds possible cheats of length 20 or less
     * starting at that position,  and ending at a valid position, where 
     * the distance from the start of the maze to the end using this cheat
     * is less than the given non-cheating length. Records these cheats in 
     * cheatMap.
     * @param position An Integer[] [rowNumber, columnNumber]
     * @param raceLen The length of the race when not cheating.
     */
    public static void findLongCheats(Integer[] position, Integer raceLen){
        int startDistance = startTimeGrid.get(position[0]).get(position[1]);
        for (Integer[] dir : longCheatDirections){
            Integer[] nextPosition = new Integer[] {position[0] + dir[0], position[1] + dir[1]};
            if(!validPosition(nextPosition)){
                continue;
            }
            String nextHash = positionToHashString(nextPosition);
            String cheatHash = positionToHashString(position) + ":" + nextHash;
            int endDistance = endTimeGrid.get(nextPosition[0]).get(nextPosition[1]);
            if(startDistance+endDistance + Math.abs(dir[0]) + Math.abs(dir[1]) < raceLen){
                longCheatMap.put(cheatHash, startDistance + endDistance + Math.abs(dir[0]) + Math.abs(dir[1]));
            }
        }
    }

    /**
     * Solution for part 2.
     * Find the number of cheats that save 100 or more steps when a 
     * cheat can be up to 20 steps in length.
     * @param start An Integer[] [startRow, startColumn]
     * @param end An Integer[] [endRow, endColumn]
     * @return The number of cheats that save 100 or more steps.
     */
    public static int part2(Integer[] start, Integer[] end){
        int cheatCount = 0;
        // find shortest path from start
        int raceLen = endTimeGrid.get(start[0]).get(start[1]);

        // for each walkable spot, find each way to use a cheat to save time
        //      hash each cheat by start and end position to prevent duplicates, with
        //      value being the new length of the race
        for (int i = 0; i < maze.size(); i++){
            for (int j = 0; j < maze.get(0).size(); j++){
                if (!maze.get(i).get(j).equals("#")){
                    findLongCheats(new Integer[] {i, j}, raceLen);
                }  
            }
        }
        // Count the cheats that result in a time save greater than 100
        for (Map.Entry<String, Integer> e : longCheatMap.entrySet()){
            if (raceLen - e.getValue() >= 100){
                cheatCount++;
            }
        }
        return cheatCount;
    }

}
