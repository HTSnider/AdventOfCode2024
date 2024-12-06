import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

/**
 * Program for Day 6 of Advent of Code 2024.
 */
public class Day6{

    /**
     * Main method that intakes and parses the input file,
     *  which consists of a NxM grid of characters [.#^],
     *  signifying open space, obstacles, and the guard, respectively.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input06.txt");
        int numLines = (int) Files.lines(Paths.get("src\\input06.txt")).count();
        Scanner in = new Scanner(inFile);
        String[][] area = new String[numLines][];
        int i = 0;
        int guardX=-1, guardY=-1;

        while(in.hasNextLine()){
            String temp = in.nextLine();
            area[i] = temp.split("");
            
            Matcher matcher = Pattern.compile("\\^").matcher(temp);
            if(matcher.find()){
                guardY = i;
                guardX = matcher.end()-1;
            }
            i++;
        }
        in.close();

        ArrayList<int[]> direction = new ArrayList<int[]>();
        direction.add(new int[] {-1,0});
        direction.add(new int[] {0,1});
        direction.add(new int[] {1,0});
        direction.add(new int[] {0,-1});

        System.out.println(guardX+ ", "+guardY);
        System.out.println(part1(deepCopy(area), guardX, guardY, direction));
        System.out.println(part2(area, guardX, guardY, direction));
    }

    /**
     * Helper funtion to create a copy of the area grid to alter in each part
     * @param target A 2d array of Strings, each 1 character long.
     * @return copy A 2d array of Strings, of the same size and contents as target
     */
    public static String[][] deepCopy(String[][] target){
        String[][] copy = new String[target.length][target[0].length];
        for (int i = 0; i<target.length; i++){
            for(int j=0; j<target[0].length; j++){
                copy[i][j] = target[i][j];
            }
        }
        return copy;
    }

    /**
     * Solution to part 1.
     * Determines the number of unique positions the guard occupies, if they start moving upward,
     * and turn right each time they hit an obstacle, until they exit the area.
     * @param area A 2d array of Strings, each consisting of a single character [.#^]
     * @param guardX The x-coordinate of the guards starting position
     * @param guardY The y-coordinate of the guards starting postiion
     * @param direction An ArrayList of arrays of integers, each being a direction unit vector (y,x) 
     * @return posCount The number of unique postions on the grid the guard occupies
     */
    public static int part1(String[][] area, int guardX, int guardY, ArrayList<int[]> direction){
        int posCount = 0;
        int dirNum = 0;

        int currX = guardX;
        int currY = guardY;
        
        try {
            while(true){
                if (!area[currY][currX].equals("X")){
                    area[currY][currX]="X";
                    posCount++;
                }
                if(area[currY + direction.get(dirNum)[0]][currX + direction.get(dirNum)[1]].equals("#")){
                    dirNum = dirNum==3 ? 0 : dirNum+1;
                    
                }
                else{
                    currY += direction.get(dirNum)[0];
                    currX += direction.get(dirNum)[1]; 
                }
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage()+ ", "+currX+" "+currY+", "+dirNum);
        }
        

        return posCount;
    }

    /**
     * Solution for part 2.
     * Determines how many different positions a single obstacle could be placed to \\
     * cause the guard to enter an infinite loop using the same inputs as part 1.
     * @param area A 2d array of Strings, each consisting of a single character [.#^]
     * @param guardX The x-coordinate of the guards starting position
     * @param guardY The y-coordinate of the guards starting postiion
     * @param direction An ArrayList of arrays of integers, each being a direction unit vector (y,x) 
     * @return loopCount The number of unique positions that an obstacle can be added to cause \\
     *          the guard to enter a loop
     */
    public static int part2(String[][] area, int guardX, int guardY, ArrayList<int[]> direction){
        int loopCount = 0;
        for (int y = 0; y<area.length; y++){
            for (int x=0; x<area[0].length; x++){
                if (willLoop(deepCopy(area), guardX, guardY, direction, x, y)){
                    loopCount++;
                    System.out.println("loop found at "+x+", "+y);
                }
            }
        }

        return loopCount;
    }

    /**
     * Helper function to determine if the guard will enter an infinite loop
     * if an obstacle is placed in a given position.
     * A loop is detected if the guard makes a turn in the same direction at the same position a second time.
     * @param area A 2d array of Strings, each consisting of a single character [.#^]
     * @param guardX The x-coordinate of the guards starting position
     * @param guardY The y-coordinate of the guards starting postiion
     * @param direction An ArrayList of arrays of integers, each being a direction unit vector (y,x) 
     * @param objX The x-coordinate of the added obstacle.
     * @param objY The y-coordinate of the added obstacle
     * @return true if the guard enters a loop, false otherwise
     */
    public static boolean willLoop(String[][] area, int guardX, int guardY, ArrayList<int[]> direction, int objX, int objY){
        Boolean loopFlag = false;
        int dirNum = 0;
        HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
        area[objY][objX]="#";

        while(0 <= guardY && guardY <area.length && 0 <= guardX && guardX < area[0].length ){
            try{
                if(area[guardY + direction.get(dirNum)[0]][guardX + direction.get(dirNum)[1]].equals("#")){
                    dirNum = dirNum==3 ? 0 : dirNum + 1;
                    String posKey = String.valueOf(guardY)+"-"+guardX+":"+dirNum;
                    if(visited.containsKey(posKey)){
                        return true;
                    }else{
                        visited.put(posKey,true);
                    }
                }
                else{
                    guardY += direction.get(dirNum)[0];
                    guardX += direction.get(dirNum)[1];
                }
            }catch(Exception e){
                break;
            }
        }

        return loopFlag;
    }

}