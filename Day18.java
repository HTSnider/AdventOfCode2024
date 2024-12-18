import java.io.*;
import java.util.*;

/**
 * Program for day 18 of Advent of Code.
 */
public class Day18 {
    
    // A list of the coordinates of the bytes expected to fall, in order.
    static ArrayList<Integer[]> byteFallList = new ArrayList<Integer[]>();
    // The grid on which the bytes fall.
    static String[][] grid = new String[71][71];
    // An arraylist of direction vectors.
    static ArrayList<Integer[]> directions = new ArrayList<Integer[]>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * N lines, each with 2 comma separated integers between 0 and 70, 
     * representing the coordinates a byte will fall on.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input18.txt");
        Scanner in = new Scanner(inFile);
        while(in.hasNextLine()){
            String[] temp = in.nextLine().split(",");
            byteFallList.add(new Integer[] {Integer.parseInt(temp[0]), Integer.parseInt(temp[1])});
        }
        in.close();
        
        // part 1 answer
        System.out.println(part1());

        // part 2 answer
        int lastByte = part2();
        System.out.println(lastByte);
        System.out.println(byteFallList.get(lastByte)[0] +","+ byteFallList.get(lastByte)[1]);
        
        // part 2 verification
        initGrid();
        dropFirstXBytes(lastByte);
        System.out.println(walkGrid());
        initGrid();
        dropFirstXBytes(lastByte+1);
        System.out.println(walkGrid());
    }

    /**
     * Solution for part 1.
     * Bytes are falling and blocking the way on a 71x71 grid. After 1028 
     * bytes have fallen, find the shortest path from [0, 0] to [70, 70].
     * @return The length of the shortest path.
     */
    public static int part1(){
        initGrid();
        initDirections();
        dropFirstXBytes(1028);
        //printGrid();
        return walkGrid();
    }

    /**
     * Helper function.
     * Initializes the grid to all empty spaces, represented by ".".
     */
    public static void initGrid(){
        for (int i = 0; i <= 70; i++){
            for (int j = 0; j <= 70; j++){
                grid[i][j]= ".";
            }
        }
    }

    /**
     * Helper function.
     * Fills the directions list with direction vectors in the 
     * horizontal and vertical directions.
     */
    public static void initDirections(){
        directions.clear();
        directions.add(new Integer[] {0,1});
        directions.add(new Integer[] {1,0});
        directions.add(new Integer[] {0,-1});
        directions.add(new Integer[] {-1,0});
    }

    /**
     * Helper function.
     * Adds the first numBytes bytes to the grid.
     * @param numBytes The number of bytes to drop on the grid.
     */
    public static void dropFirstXBytes(int numBytes){
        for (int i = 0; i < numBytes; i++){
            Integer[] dropByte = byteFallList.get(i);
            grid[dropByte[0]][dropByte[1]]="#";
        }
    }

    /**
     * Helper function.
     * Uses BFS to find the shortest path from [0, 0] to [70, 70].
     * @return The length of the shortest path, or -1 if no path exists.
     */
    public static int walkGrid(){
        int[] start = {0,0};
        int[] end = {70,70};

        ArrayDeque<Integer[]> processQueue = new ArrayDeque<Integer[]>();
        processQueue.add(new Integer[] {start[0], start[1], 0});
        HashSet<String> visited = new HashSet<String>();

        while(!processQueue.isEmpty()){
            Integer[] curr = processQueue.pollFirst();
            String currHash = String.valueOf(curr[0]) +":"+ String.valueOf(curr[1]);
            if(visited.contains(currHash)){
                continue;
            }
            visited.add(currHash);
            for (Integer[] dir : directions){
                Integer[] newStep = new Integer[] {curr[0]+dir[0], curr[1]+dir[1]};
                if(newStep[0] == end[0] && newStep[1] == end[1]){
                    return curr[2]+1;
                }
                if (checkOpen(newStep)){
                    processQueue.addLast(new Integer[] {newStep[0], newStep[1], curr[2]+1});
                }
            }
        }
        return -1;
    }

    /**
     * Helper function.
     * Checks if the given coordinate is within the grid, and then
     * checks if the grid coordinate is not blocked by a byte "#".
     * @param coord An Integer[] [rowNumber, columnNumber].
     * @return true if the coordinates are within the grid and not "#",
     * or false otherwise.
     */
    public static boolean checkOpen(Integer[] coord){
        if (!(0 <= coord[0] && coord[0] <=70 && 0 <= coord[1] && coord[1] <= 70)){
            return false;
        }
        if (grid[coord[0]][coord[1]].equals("#")){
            return false;
        }
        return true;
    }

    /**
     * Diagnostic function.
     * Outputs the state of the grid to the console. "'" represents an 
     * open space, and "#" represents a blocked space.
     */
    public static void printGrid(){
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[0].length; j++){
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Solution for part 2.
     * Find the coordinates of the first block that would prevent you
     * from reaching the exit. Uses binary sort.
     * @return The index of the first block that would make the end
     * unreachable.
     */
    public static int part2(){
        int minByte = 0;
        int maxByte = byteFallList.size()-1;
        int numDropped;
        int numSteps;
        HashSet<Integer> checked = new HashSet<Integer>();
        while(true){
            numDropped = (maxByte + minByte)/2;
            if(checked.contains(numDropped)){
                break;
            }
            checked.add(numDropped);
            initGrid();
            dropFirstXBytes(numDropped);
            numSteps = walkGrid();
            if(numSteps == -1){
                maxByte = numDropped-1;
            }else{
                minByte = numDropped;
            }
        }
        return minByte;
    }
}
