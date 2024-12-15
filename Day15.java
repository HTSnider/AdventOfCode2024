import java.io.*;
import java.util.*;

/**
 * Program for day 15 of Advent of Code.
 */
public class Day15{

    // Grid to store warehouse 1 map.
    static ArrayList<ArrayList<String>> grid = new ArrayList<ArrayList<String>>();
    // List of movement commands.
    static ArrayList<String> movements = new ArrayList<String>();
    // Hashmap of direction arrows [^>v<] to direction vectors
    static HashMap<String, Integer[]> directions = new HashMap<String, Integer[]>();
    // Grid to store warehouse 2 map.
    static ArrayList<ArrayList<String>> grid2 = new ArrayList<ArrayList<String>>();
    // Set of objects that must be moved to complete a single movement.
    static LinkedHashSet<String> moveQueue = new LinkedHashSet<String>();
    // Set of spaces that have been checked for availability for the current movement.
    static HashSet<String> checked = new HashSet<String>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * the initial N x M grid of the warehouse, then an empty line, then Z lines of 
     * movement instructions.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input15.txt");
        Scanner in = new Scanner(inFile);
        Integer[] startPosition = new Integer[2]; 
        Integer[] startPosition2 = new Integer[2];
        int yStart = 0;
        String temp = in.nextLine();
        while (!temp.equals("")){
            int xStart = 0;
            int xStart2 = 0;
            ArrayList<String> curr = new ArrayList<String>();
            ArrayList<String> curr2 = new ArrayList<String>();
            for (String s : temp.split("")){
                if (s.equals("@")){
                    startPosition[0] = yStart;
                    startPosition2[0] = yStart;
                    startPosition[1] = xStart;
                    startPosition2[1] = xStart2;
                    curr.add(s);
                    curr2.add(s);
                    curr2.add(".");
                }
                else if(s.equals("O")){
                    curr.add(s);
                    curr2.add("[");
                    curr2.add("]");
                    
                }
                else{
                    curr.add(s);
                    curr2.add(s);
                    curr2.add(s);
                }
                xStart++;
                xStart2 += 2;
            }
            grid.add(curr);
            grid2.add(curr2);
            temp = in.nextLine();
            yStart++;
        }   
        while(in.hasNextLine()){
            temp = in.nextLine();
            for (String s: temp.split("")){
                movements.add(s);
            }
        }
        in.close();
        directions.put("^", new Integer[] {-1, 0});
        directions.put(">", new Integer[] {0, 1});
        directions.put("v", new Integer[] {1, 0});
        directions.put("<", new Integer[] {0, -1});

        System.out.println(part1(startPosition));
        //printGrid2();
        System.out.println(part2(startPosition2));
        printGrid2();
    }

    /**
     * Solution for part 1.
     * The given warehouse grid consists of 1 robot @, immovable walls #, and
     * movable boxes O. For each movement command, the robot attempts to move 
     * in that direction, pushing any boxes in the same direction if they are 
     * not blocked by a wall. After all movement commands are processed, each 
     * box is given a GPS coordinate. Find the sum of the GPS coordinates.
     * @param startPosition An Integer[] [startingRow, startingColumn]
     * @return  The sum of the GPS coordinates of the boxes.
     */
    public static Long part1(Integer[] startPosition){
        Integer[] position = new Integer[] {startPosition[0], startPosition[1]};
        for (String move : movements){
            if(tryMove(position, move)){
                position[0] += directions.get(move)[0];
                position[1] += directions.get(move)[1];
            }
        }
        return getGPSSum();
    }

    /**
     * Helper function for part 1.
     * Recursively tries to move each movable object until a movement fails
     * (hits a wall) or succeeds (moves into an empty space), then moves 
     * each object in the given direction if the movement succeeded.
     * @param position An Integer[] [startingRow, startingColumn]
     * @param dir A String [^>v<], the key to the corresponding direction
     * vector
     * @return true if the object can be moved, or false if not
     */
    public static boolean tryMove(Integer[] position, String dir){
        Integer[] nextPosition = new Integer[] {position[0] + directions.get(dir)[0], 
        position[1] + directions.get(dir)[1]};
        String target = grid.get(nextPosition[0]).get(nextPosition[1]);
        if (target.equals(".")){
            grid.get(nextPosition[0]).set(nextPosition[1], grid.get(position[0]).get(position[1]));
            grid.get(position[0]).set(position[1], ".");
            return true;
        } else if (target.equals("#")){
            return false;
        } else{
            if (tryMove(nextPosition, dir)){
                grid.get(nextPosition[0]).set(nextPosition[1], grid.get(position[0]).get(position[1]));
                grid.get(position[0]).set(position[1], ".");
                return true;
            }
            return false;
        }
    }

    /**
     * Helper function for part 1.
     * Finds the GPS coordinate of each box, calculated as the
     * distance from the top of the map * 100 + the distance 
     * from the left of the map. Then finds the sum.
     * @return sum The sum of the GPS coordinates of each box.
     */
    public static Long getGPSSum(){
        Long sum = 0L;
        for (int i = 0; i < grid.size(); i++){
            for (int j = 0; j < grid.get(0).size(); j++){
                if (grid.get(i).get(j).equals("O")){
                    sum += i * 100 + j;
                }
            }
        }
        return sum;
    }

    /**
     * Solution for part 2.
     * The given warehouse map is twice as wide, with each wall and
     * empty space doubling, the robot gaining another empty space to 
     * its right, and each box becoming a larger box "[]", taking up
     * two horizontal spaces. Afterperforming the same movements, find
     * the sum of the GPS coordinates of the boxes.
     * @param startPosition An Integer[] [startingRow, startingColumn]
     * @return  The sum of the GPS coordinates of the boxes.
     */
    public static Long part2(Integer[] startPosition){
        Integer[] position = new Integer[] {startPosition[0], startPosition[1]};
        
        for (String dir : movements){
            if (canMove(position, dir)){
                try{
                    move(moveQueue, dir);
                }catch(Exception e){
                    printGrid2();
                    System.exit(0);
                }
                position[0] += directions.get(dir)[0];
                position[1] += directions.get(dir)[1];
            }
            moveQueue.clear();
            checked.clear();
        }
        return getGPSSum2();
    }

    /**
     * Helper function for part 2.
     * Determines recursively if the object at the given position
     * can move in the given direction, and adds its position to 
     * a LinkedHashSet moveQueue to be moved if all possible movements
     * succeed. Also adds each space to a HashSet to prevent repeated 
     * checking.
     * @param position An Integer[] [startingRow, startingColumn]
     * @param dir A String [^>v<], the key to the corresponding 
     * direction vector
     * @return  true if the movement would be possible, or false
     * otherwise
     */
    public static boolean canMove(Integer[] position, String dir){
        String self = grid2.get(position[0]).get(position[1]);
        Integer[] nextPosition = new Integer[] {position[0] + directions.get(dir)[0], position[1] + directions.get(dir)[1]};
        String hashString = positionToHashString(position);
        // have we checked this before?
        if( checked.contains(hashString)){
            return moveQueue.contains(hashString);
        }else{
            checked.add(hashString);
        }
        
        if(self.equals("@")){ //case: robot
            if(canMove(nextPosition, dir)){ 
                moveQueue.add(hashString);
                return true;
            }

        } else if(self.equals("[")){// case: box 
            Integer[] boxPart2 = new Integer[] {position[0], position[1]+1};
            
            // have we checked this before? second half of box
            String box2HashString = positionToHashString(boxPart2);
            if (checked.contains(box2HashString)){
                return moveQueue.contains(box2HashString);
            }else{
                checked.add(box2HashString);
            }

            if(dir.equals(">") && canMove(new Integer[] {boxPart2[0] + directions.get(dir)[0], boxPart2[1] + directions.get(dir)[1]}, dir)){
                if(!moveQueue.contains(box2HashString)){

                    moveQueue.add(box2HashString);
                }
                if(!moveQueue.contains(hashString)){
                    moveQueue.add(hashString);
                }
                return true;
            }else if(canMove(nextPosition, dir) && 
                canMove(new Integer[] {boxPart2[0]+directions.get(dir)[0], boxPart2[1]+directions.get(dir)[1]}, dir)){ 
                if(!moveQueue.contains(hashString)){
                    moveQueue.add(hashString);
                }
                if(!moveQueue.contains(box2HashString)){
                    moveQueue.add(box2HashString);
                }
                return true;
            }
        } else if(self.equals("]")){ // case box, 
            Integer[] boxPart2 = new Integer[] {position[0], position[1]-1};
            
            // have we checked this before? second half of box
            String box2HashString = positionToHashString(boxPart2);
            if (checked.contains(box2HashString)){
                return moveQueue.contains(box2HashString);
            }else{
                checked.add(box2HashString);
            }

            if(dir.equals("<") && canMove(new Integer[] {boxPart2[0] + directions.get(dir)[0], boxPart2[1] + directions.get(dir)[1]}, dir)){
                if(!moveQueue.contains(box2HashString)){
                    moveQueue.add(box2HashString);
                }
                if(!moveQueue.contains(hashString)){
                    moveQueue.add(hashString);
                }
                return true;
            }else if(canMove(nextPosition, dir) && 
                canMove(new Integer[] {boxPart2[0]+directions.get(dir)[0], boxPart2[1]+directions.get(dir)[1]}, dir)){ 
                if(!moveQueue.contains(hashString)){
                    moveQueue.add(hashString);
                }
                if(!moveQueue.contains(box2HashString)){
                    moveQueue.add(box2HashString);
                }
                return true;
            }
        } else if(self.equals(".")){ // case empty space: automatic success
            return true;
        }
        else if(self.equals("#")){ // case wall: automatic failure
            return false;
        }
        return false;
    }

    /**
     * Helper function for part 2.
     * Converts a position array into a string for hashing.
     * @param position An Integer[] [rowNumber, columnNumber]
     * @return A String of the array elements concatenated.
     */
    public static String positionToHashString(Integer[] position){
        return String.valueOf(position[0]) +":"+ String.valueOf(position[1]);
    }

    /**
     * Helper function for part 2.
     * Converts a hashstring into a position array.
     * @param hashString A string integer1:integer2
     * @return An Integer[] [rowNumber, columnNumber]
     */
    public static Integer[] hashStringToPosition(String hashString){
        String[] temp = hashString.split(":");
        return new Integer[] {Integer.parseInt(temp[0]), Integer.parseInt(temp[1])};
    }

    /**
     * Helper function for part 2.
     * Moves each object in the moveQueue LinkedHashSet in the given
     * direction. The recursive way the objects are added allows each
     * movement to succeed.
     * @param objects A LinkedHashSet of hash Strings, each representing
     * the position vector of an object.
     * @param dir A String [^>v<], the key to the corresponding direction 
     * vector.
     */
    public static void move(LinkedHashSet<String> objects, String dir){
        for (String hash : objects){
            Integer[] position = hashStringToPosition(hash);
            Integer[] newPosition = new Integer[] {position[0] + directions.get(dir)[0],
                    position[1] + directions.get(dir)[1]};
            String obj = grid2.get(position[0]).get(position[1]);
            grid2.get(newPosition[0]).set(newPosition[1], obj);
            grid2.get(position[0]).set(position[1], ".");
        }
    }

    /**
     * Helper function for part 2.
     * Finds the GPS coordinates of each box in the second warehouse
     * grid, and returns their sum.
     * @return sum The sum of the GPS coordinates
     */
    public static Long getGPSSum2(){
        Long sum = 0L;
        for (int i = 0; i < grid2.size(); i++){
            for (int j = 0; j < grid2.get(0).size(); j++){
                if (grid2.get(i).get(j).equals("[")){
                    sum += i * 100 + j;
                }
            }
        }
        return sum;
    }

    /**
     * Diagnostic function for part 2.
     * Prints the second warehouse grid to the console.
     */
    public static void printGrid2(){
        for (int i = 0; i < grid2.size(); i++){
            for (int j = 0; j < grid2.get(0).size(); j++){
                System.out.print(grid2.get(i).get(j));
            }
            System.out.println();
        }
    }
}