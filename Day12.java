import java.io.*;
import java.util.*;

/**
 * Program for day 12 of Advent of Code 2024
 */
public class Day12 {

    // HashSet to ensure each plot is assigned to only 1 region, exactly once
    static HashSet<String> crawled = new HashSet<String>();
    // ArrayList of direction vectors, populated in main
    static ArrayList<Integer[]> direction = new ArrayList<Integer[]>();
    // Grid of plots, each String a single uppercase letter 
    static ArrayList<ArrayList<String>> field = new ArrayList<ArrayList<String>>();
    // List of regions, populated in part 1. Each set contains the concatenated coordinates
    // of contiguous plots with the same letter code.
    static ArrayList<HashSet<String>> regions = new ArrayList<HashSet<String>>();

    /**
     * Main method that intakes and parses the input, which consists of
     * an N x M grid of upper-case letters.
     * Populates the field class variable from the input, and also populates
     * the direction list.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input12.txt");
        Scanner in = new Scanner(inFile);
        while(in.hasNextLine()){
            ArrayList<String> line = new ArrayList<String>();
            for (String plot : in.nextLine().split("")){
                line.add(plot);
            }
            field.add(line);
        }
        in.close();

        direction.add(new Integer[] {0,1});
        direction.add(new Integer[] {1,0});
        direction.add(new Integer[] {0,-1});
        direction.add(new Integer[] {-1,0});

        System.out.println(part1());
        System.out.println(part2());
    }

    /**
     * Solution for part 1.
     * Divides the given field into regions of contiguous plots of the same letter code.
     * Then the cost to fence each region is given by the perimeter of the region, multiplied
     * by the number of plots within it. Then returns the sum of these costs.
     * Region perimeter includes the perimeter of any regions inside it.
     * @return cost The sum of the cost to fence each region.
     */
    public static int part1(){
        int cost = 0;
        // find regions
        for (int i=0; i<field.size(); i++){
            for (int j=0; j<field.get(0).size(); j++){
                String coord = String.valueOf(i)+"-"+String.valueOf(j);
                if (!crawled.contains(coord)){
                    regions.add(crawlRegion(new Integer[]{i,j}));
                }
            }
        }
        
        // get cost per region
        for (HashSet<String> reg : regions){
            cost += costRegion(reg);
        }
        return cost;
    }

    /**
     * Helper function for both parts.
     * From a given starting plot, finds the set of all plots contiguous to that 
     * plot with the same letter code. Contiguous includes horizontal and vertical
     * only, not diagonal.
     * @param start An array [lineNumber, columnNumber]
     * @return region A HashSet of the coordinates of each plot within the region,
     * concatenated together.
     */
    public static HashSet<String> crawlRegion(Integer[] start){
        HashSet<String> region = new HashSet<String>();
        region.add(String.valueOf(start[0])+"-"+String.valueOf(start[1]));
        crawled.add(String.valueOf(start[0])+"-"+String.valueOf(start[1]));
        String regionType = field.get(start[0]).get(start[1]);
        int fieldLines = field.size();
        int fieldColumns = field.get(0).size();
        ArrayDeque<Integer[]> processDeque = new ArrayDeque<Integer[]>();
        processDeque.add(start);
        while(!processDeque.isEmpty()){
            Integer[] position = processDeque.removeFirst();
            for (Integer[] dir : direction){
                Integer[] newPlot = {position[0]+dir[0], position[1]+dir[1]};
                if (0 <= newPlot[0] && newPlot[0] < fieldLines && 0 <= newPlot[1] && newPlot[1] < fieldColumns){
                    String plotCoords = String.valueOf(newPlot[0])+"-"+String.valueOf(newPlot[1]);
                    if (field.get(newPlot[0]).get(newPlot[1]).equals(regionType) && !region.contains(plotCoords)){
                        region.add(plotCoords);
                        crawled.add(plotCoords);
                        processDeque.addLast(newPlot);
                    }
                }
            }
        }
        return region;
    }

    /**
     * Helper function for part 1.
     * Finds the area and perimeter of a region, and returns their product.
     * @param region The HashSet of the coordinates of all plots within the region, each
     * concatenated together.
     * @return cost The cost to fence the given region.
     */
    public static int costRegion(HashSet<String> region){
        int cost = region.size();
        int perimeter = 0;
        String regionType="";
        for (String coords : region){
            String[] coordArr = coords.split("-");
            int lineNum = Integer.parseInt(coordArr[0]);
            int colNum = Integer.parseInt(coordArr[1]);
            if(regionType.equals("")){
                regionType = field.get(lineNum).get(colNum);
            } 
            for (Integer[] dir : direction){
                Integer[] newPlot = {lineNum+dir[0], colNum+dir[1]};
                if (0 <= newPlot[0] && newPlot[0] < field.size() &&
                0 <= newPlot[1] && newPlot[1] < field.get(0).size()){
                    if(!field.get(newPlot[0]).get(newPlot[1]).equals(regionType)){
                        perimeter++;
                    }
                }
                else{
                    perimeter++;
                }
            }
        }

        return cost*perimeter;
    }

    /**
     * Solution for part 2.
     * The cost for fencing the regions is changed to the area of the region 
     * multiplied by the number of sides. Note that plots that meet at a diagonal
     * are still considered separate sides.
     * @return The new cost to fence all the regions.
     */
    public static int part2(){
        int cost = 0;

        
        // get cost per region
        for (HashSet<String> reg : regions){
            cost += discountCostRegion(reg);
        }
        return cost;
    }


    /**
     * Helper function for part 2.
     * Finds the number of sides of the the given region, and returns 
     * the product of that with the area of the region.
     * @param region A HashSet of the concatenated coordinates of the
     * plots in the region.
     * @return The cost to fence the region.
     */
    public static int discountCostRegion(HashSet<String> region){
        int area = region.size();
        HashSet<String> horizontalEdges = new HashSet<String>();
        HashSet<String> verticalEdges = new HashSet<String>();
        // find edges of each type
        for (String coord : region){
            String[] temp = coord.split("-");
            int currLine = Integer.parseInt(temp[0]);
            int currCol = Integer.parseInt(temp[1]);
            for (int dirIx = 0; dirIx<4; dirIx++){
                Integer[] newPlot = new Integer[] {currLine+direction.get(dirIx)[0], currCol+direction.get(dirIx)[1]};
                if(!(0 <= newPlot[0] && newPlot[0] < field.size() && 0 <= newPlot[1] && newPlot[1] < field.get(0).size())){
                    if (dirIx%2==0){
                        verticalEdges.add(doubleArrToHashString(new Double[] {Double.parseDouble(Integer.toString(currLine)), Double.valueOf(currCol>newPlot[1]? currCol : newPlot[1])}));
                    }else{
                        horizontalEdges.add(doubleArrToHashString(new Double[] {Double.valueOf(currLine>newPlot[0]? currLine : newPlot[0]), Double.parseDouble(Integer.toString(currCol))}));
                    }
                    continue;
                }
                if (!(field.get(newPlot[0]).get(newPlot[1]).equals(field.get(currLine).get(currCol)))){
                    if (dirIx%2==0){
                        verticalEdges.add(doubleArrToHashString(new Double[] {Double.parseDouble(Integer.toString(currLine)), Double.valueOf(currCol>newPlot[1]?currCol:newPlot[1])}));
                    }else{
                        horizontalEdges.add(doubleArrToHashString(new Double[] {Double.valueOf(currLine>newPlot[0]?currLine : newPlot[0]), Double.parseDouble(Integer.toString(currCol))}));
                    }
                }
            }
        }

        int sides = 0;
        // find contiguous edges
        HashSet<String> visitedHoriEdges = new HashSet<String>();
        HashSet<String> visitedVertEdges = new HashSet<String>();
        for (String edge : Set.copyOf(horizontalEdges)){
            if(visitedHoriEdges.contains(edge)){
                continue;
            }
            horizontalEdges.remove(edge);
            visitedHoriEdges.add(edge);
            HashSet<String> side = new HashSet<String>();
            side.add(edge);
            Double i = Double.valueOf(1);
            Double[] edgeArr = hashStringToDoubleArr(edge);
            while(horizontalEdges.contains(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}))){
                horizontalEdges.remove(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}));
                side.add(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}));
                visitedHoriEdges.add(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}));
                i++;
            }
            i = Double.valueOf(-1);
            while(horizontalEdges.contains(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}))){
                horizontalEdges.remove(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}));
                side.add(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}));
                visitedHoriEdges.add(doubleArrToHashString(new Double[] {edgeArr[0], edgeArr[1]+i}));
                i--;
            }
            sides++;
        }

        for (String edge : Set.copyOf(verticalEdges)){
            if(visitedVertEdges.contains(edge)){
                continue;
            }
            verticalEdges.remove(edge);
            visitedVertEdges.add(edge);
            Double[] edgeArr = hashStringToDoubleArr(edge);
            HashSet<String> side = new HashSet<String>();
            side.add(edge);
            int i = 1;
            while(verticalEdges.contains(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}))){
                verticalEdges.remove(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}));
                side.add(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}));
                visitedVertEdges.add(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}));
                i++;
            }
            i = -1;
            while(verticalEdges.contains(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}))){
                verticalEdges.remove(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}));
                side.add(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}));
                visitedVertEdges.add(doubleArrToHashString(new Double[] {edgeArr[0]+i, edgeArr[1]}));
                i--;
            }
            sides++;
        }

        // account for corners within region
        for(String edge : Set.copyOf(visitedVertEdges)){
            Double[] up = hashStringToDoubleArr(edge);
            Double[] down = new Double[] {up[0]+1, up[1]};
            Double[] left = new Double[] {up[0]+1, up[1]-1};
            Double[] right = new Double[] {left[0], left[1]+1};
            if(visitedVertEdges.contains(doubleArrToHashString(up)) && visitedVertEdges.contains(doubleArrToHashString(down)) && 
            visitedHoriEdges.contains(doubleArrToHashString(left)) && visitedHoriEdges.contains(doubleArrToHashString(right))){
                sides +=2;
            }
        }
        return area * sides;
    }

    /**
     * Helper function for part 2.
     * Converts the hash string into a usable array of Double 
     * [lineNumber, columnNumber]
     * @param input The concatenated coordinates of the plot.
     * @return The coordinates as a Double[]
     */
    public static Double[] hashStringToDoubleArr(String input){
        String[] inputArr = input.split(":");
        return new Double[] {Double.parseDouble(inputArr[0]), Double.parseDouble(inputArr[1])};
    }

    /**
     * Helper function for part 2.
     * Converts the coordinates of a plot into a string for
     * hashing. 
     * @param input A Double[] [lineNumber, ColumnNumber].
     * @return The concatenated coordinates.
     */
    public static String doubleArrToHashString(Double[] input){
        return String.valueOf(input[0]) +":"+ String.valueOf(input[1]);
    }

}
