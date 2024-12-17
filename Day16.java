import java.io.*;
import java.util.*;

/**
 * Program for day 16 of Advent of Code.
 */
public class Day16 {
    
    // grid to hold the maze
    static ArrayList<ArrayList<String>> maze = new ArrayList<ArrayList<String>>();
    // arraylist to hold the direction vectors
    static ArrayList<Integer[]> directions = new ArrayList<Integer[]>();
    // hashmap where the position and direction are the key, and
    //  the value is the cost to reach that position and direction
    static HashMap<String, Integer> costsMap = new HashMap<String, Integer>();
    // hashset to hold the positions along the optimum path(s)
    static HashSet<String> bestPath = new HashSet<String>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * an N x M grid of characters, # representing a wall, . representing an
     * empty space, S representing the starting position, and E representing
     * the ending position.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input16.txt");
        Scanner in = new Scanner(inFile);
        Integer[] start = new Integer[2];
        Integer[] end = new Integer[2];
        int rowNum = 0;
        while (in.hasNextLine()) {
            ArrayList<String> curr = new ArrayList<String>();
            String[] temp = in.nextLine().split("");
            int colNum = 0;
            for (String s : temp){
                curr.add(s);
                if(s.equals("S")){
                    start[0] = rowNum;
                    start[1] = colNum;
                }
                if (s.equals("E")){
                    end[0] = rowNum;
                    end[1] = colNum;
                }
                colNum++;
            }
            maze.add(curr);
            rowNum++;
        }
        in.close();
        directions.add(new Integer[] {0,1});
        directions.add(new Integer[] {1,0});
        directions.add(new Integer[] {0,-1});
        directions.add(new Integer[] {-1,0});

        System.out.println(part1(start));
        System.out.println(part2(end));
        printMaze();
    }

    /**
     * Solution for part 1.
     * Find the most optimum path through the maze, where each horizontal or 
     * vertical step costs 1, and each 90 degree turn costs 1000, and then 
     * find the cost of that optimum path.
     * @param start An Integer[] [startingRow, startingColumn]
     * @return  The minimum cost to reach the ending position.
     */
    public static int part1(Integer[] start){
        // object class for processing each space and direction
        class Node{
            Integer cost; // The cost to reach the position and direction
            int directionIx; // The index of the current direction
            int rowNum; // the row of the current position
            int columnNum; // the column of the current position
            Node(int cost, int directionIx, int rowNum, int columnNum){
                this.cost = cost;
                this.directionIx  = directionIx;
                this.rowNum = rowNum;
                this.columnNum = columnNum;
            }

            // outputs the hash string for the given position and direction
            public String toHashString(){
                return String.format("%d:%d:%d", this.rowNum, this.columnNum, this.directionIx);
            }

            // outputs the possible next postions and directions as Nodes
            public Node[] advance(){
                int leftDir = fixDir(this.directionIx-1);
                int rightDir = fixDir(this.directionIx+1);
                int backDir = fixDir(directionIx+2);
                Node goLeft = new Node(cost+1000, leftDir, rowNum, columnNum);
                Node goRight = new Node(cost+1000, rightDir, rowNum, columnNum);
                Node goBack = new Node(cost+2000, backDir, rowNum, columnNum);
                Node goForward = this.step(this.directionIx);
                return new Node[] {goForward, goLeft, goRight, goBack};
            }

            // outputs the next node in the given direction.
            public Node step(int direction){
                int turnCount = Math.min(Math.abs(this.directionIx - direction), Math.abs(this.directionIx - direction +4));
                int newRow = this.rowNum + directions.get(direction)[0];
                int newColumn = this.columnNum + directions.get(direction)[1];
                int newCost = this.cost + 1 + 1000*turnCount;
                return new Node(newCost, direction, newRow, newColumn);
            }
        }

        
        // Queue for processing each space and direction, sorted by lowest cost
        PriorityQueue<Node> processQueue = new PriorityQueue<Node>(
            new Comparator<Node>(){
                @Override
                public int compare(Node n1, Node n2){
                    return n1.cost.compareTo(n2.cost);
                }   
            }
        );

        processQueue.add(new Node(0,0,start[0], start[1]));
        Integer[] end = new Integer[] {-1,-1};


        while(!processQueue.isEmpty()){
            Node currNode = processQueue.poll();
            String curr = maze.get(currNode.rowNum).get(currNode.columnNum);
            if (curr.equals("E")){ // if the end is reached, save the coordinates and don't move on
                end[0] = currNode.rowNum;
                end[1] = currNode.columnNum;
                addCost(currNode.toHashString(), currNode.cost);
                continue;
            }
            if(!addCost(currNode.toHashString(), currNode.cost)){ // if new cost is more expensive, don't move on
                continue;
            }
            for (Node next : currNode.advance()){ // add each viable next node, after step or turn, to the queue
                if(!maze.get(next.rowNum).get(next.columnNum).equals("#")){
                        processQueue.add(next);
                    }
            }
        }

        // find the minimum cost of all directions that reach the end
        int minCost = Integer.MAX_VALUE;
        for (int dir = 0; dir < 4; dir++){
            String endHash = String.format("%d:%d:%d", end[0], end[1], dir);
            if(costsMap.containsKey(endHash)){
                minCost = Math.min(minCost, costsMap.get(endHash));
            }
        }
        return minCost;
    }

    /**
     * Helper function for both parts.
     * Ensures the direction index remains between 0 and 3 inclusive.
     * @param direction An integer
     * @return fixedDir A positive integer equal to the input modulo 4.
     */
    public static int fixDir(int direction){
        int fixedDir = direction;
        while (fixedDir<0){
            fixedDir += 4;
        }
        return fixedDir % 4;
    }

    /**
     * Helper function for part 1.
     * Attempts to add the given cost to the costsMap hashmap, and returns
     * whether the input value would be lower or not.
     * @param hashString A string, the position and direction index concatenated.
     * @param cost An Integer, the potential cost to reach the given position and 
     * direction.
     * @return true if the hashmap is changed, and false if not.
     */
    public static boolean addCost(String hashString, int cost){
        if (!costsMap.containsKey(hashString)){
            costsMap.put(hashString, cost);
            return true;
        }
        int currCost = costsMap.get(hashString);
        if (cost<currCost){
            costsMap.replace(hashString, cost);
            return true;
        }
        return false;
    }

    /**
     * Solution for part 2.
     * Knowing the costs to reach each position and direction, now find
     * the number of positions along the optimum path(s).
     * @param end An Integer[] [endingRow, endingColumn]
     * @return The number of unique positions along the optimum paths.
     */
    public static int part2(Integer[] end){
        ArrayDeque<Integer[]> processQueue = new ArrayDeque<Integer[]>();
        for (int dir : getBestEnd(end)){
            processQueue.add(new Integer[] {end[0], end[1], dir});
        }

        while(!processQueue.isEmpty()){
            Integer[] currPosition = processQueue.pollFirst();
            bestPath.add(String.format("%d:%d", currPosition[0], currPosition[1]));
            Integer[] nextDirs = bestDirection(currPosition);
            for (Integer dir : nextDirs){
                processQueue.addLast(new Integer[] {currPosition[0]-directions.get(dir)[0], currPosition[1]-directions.get(dir)[1], dir});
            }
        }
        return bestPath.size();
    }

    /**
     * Helper function for part 2.
     * Converts a hashstring into its component position and direction.
     * @param hashString A string of a position and direction concatenated.
     * @return An Integer[] [rowNumber, columnNumber, directionIndex]
     */
    public static Integer[] hashToPosition(String hashString){
        String[] temp = hashString.split(":");
        return new Integer[] {Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])};
    }

    /**
     * Helper function for part 2.
     * Given a position and direction, find the adjacent position and direction pair(s)
     * that are along the best path to input.
     * @param position An Integer[] [rowNumber, columnNumber, directionIndex]
     * @return  An Integer[], where each element is a direction index that leads to the
     * input position with the optimum cost.
     */
    public static Integer[] bestDirection(Integer[] position){
        ArrayList<Integer> bestDir = new ArrayList<Integer>();
        String baseHash = String.format("%d:%d:%d", position[0], position[1], position[2]);
        for(Integer i = 0; i < 4; i++){
            String testHash = String.format("%d:%d:%d", position[0], position[1], i);
            if (costsMap.containsKey(testHash) && 
                (costsMap.get(testHash)==costsMap.get(baseHash) ||
                 (i!= position[2] && costsMap.get(testHash)==costsMap.get(baseHash)-1000))){
                int nextRow = position[0] - directions.get(i)[0];
                int nextCol = position[1] - directions.get(i)[1];
                String nextHash = String.format("%d:%d:%d", nextRow, nextCol, i);
                if (costsMap.containsKey(nextHash) && costsMap.get(nextHash)== costsMap.get(testHash)-1 ){
                    bestDir.add(Integer.valueOf(i));
                }
                
            }
        }
        Integer[] out = new Integer[bestDir.size()];
        for(int i = 0; i < bestDir.size(); i++){
            out[i] = bestDir.get(i);
        }
        return out;
    }

    /**
     * Helper function for part 2.
     * Given a position, find the direction(s) that lead to the input position
     * that have an optimum cost.
     * @param end An Integer[] [endRow, endColumn]
     * @return An Integer[], each element being a direction index leading to
     * the input position.
     */
    public static Integer[] getBestEnd(Integer[] end){
        ArrayList<Integer> outDir = new ArrayList<Integer>();
        int minCost = Integer.MAX_VALUE;
        for (int i = 0; i < 4; i++){
            String hash = String.format("%d:%d:%d", end[0], end[1], i);
            if (costsMap.containsKey(hash)){
                if(costsMap.get(hash)==minCost){
                    outDir.add(i);
                }
                else if (costsMap.get(hash) < minCost){
                    minCost = costsMap.get(hash);
                    outDir.clear();
                    outDir.add(i);
                }
            }
        }
        Integer[] out = new Integer[outDir.size()];
        for (int dirNum = 0; dirNum < outDir.size(); dirNum++){
            out[dirNum] = outDir.get(dirNum);
        }
        return out;
    }

    /**
     * A diagnostic function for part 2.
     * Creates a copy of the maze grid, replaces each element along the optimum 
     * paths with O, and prints the resulting grid.
     */
    public static void printMaze(){
        ArrayList<ArrayList<String>> copy = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < maze.size(); i++){
            ArrayList<String> row = new ArrayList<String>();
            for(int j = 0; j < maze.get(0).size(); j++){
                row.add(maze.get(i).get(j));
            }
            copy.add(row);
        }

        for (String hash : bestPath){
            String[] temp = hash.split(":");
            copy.get(Integer.parseInt(temp[0])).set(Integer.parseInt(temp[1]), "O");
        }

        for (int i = 0; i < copy.size(); i++){
            for(int j = 0; j < copy.get(0).size(); j++){
                System.out.print(copy.get(i).get(j));
            }
            System.out.println();
        }
    }
}
