import java.io.*;
import java.util.*;

/**
 * Program for day 8 of Advent of Code 2024
 */
public class Day8 {

    /**
     * Main method that intakes and parses the input file,
     * which consists of an N x M grid of '.' to represent empty space,
     * and alphanumeric characters and symbols to represent antenni of 
     * different frequencies.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input08.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<String[]> cityMap = new ArrayList<String[]>();
        while(in.hasNextLine()){
            cityMap.add(in.nextLine().split(""));
        }
        in.close();

        System.out.println(part1(deepCopy(cityMap)));
        System.out.println(part2(deepCopy(cityMap)));
    }

    /**
     * Solution to part 1.
     * Returns the number of antinodes created on the grid by pairs of same-frequency antenni.
     * Each pair of antenni with the same frequency (the same alphanumeric character
     * or symbol) produces 2 antinodes, a distance vector in the opposite direction from each antenna.
     * Antinodes can occur on any space in the grid that doesn't already have an antinode.
     * @param cityMap An ArrayList of String[], each element of the String[] consisting of
     * a single alphanumeric character, symbol, or dot.
     * @return nodeCount The number of antinodes produced within the provided grid.
     */
    public static int part1(ArrayList<String[]> cityMap){
        int nodeCount = 0;
        // Find the types of antenni and their locations
        HashMap<String, ArrayList<int[]>> antenni = findAntenni(cityMap);

        // for each pair of a single type, try to place antinodes
        for (String key : antenni.keySet()){
            if(antenni.get(key).size()<2){
                continue;
            }
            for (int first = 0; first < antenni.get(key).size()-1; first++){
                int[] firstPos = antenni.get(key).get(first);
                for (int second = first+1; second < antenni.get(key).size(); second++){
                    int[] secondPos = antenni.get(key).get(second);
                    int[] distanceVector = {firstPos[0]-secondPos[0], firstPos[1]-secondPos[1]};
                    try {
                        if (!cityMap.get(firstPos[0]+distanceVector[0])[firstPos[1]+distanceVector[1]].equals("#")){
                            cityMap.get(firstPos[0]+distanceVector[0])[firstPos[1]+distanceVector[1]] = "#";
                            nodeCount++;
                        }
                    } catch (Exception e) {
                        // do nothing if it fails
                    }
                    try {
                        if (!cityMap.get(secondPos[0]-distanceVector[0])[secondPos[1]-distanceVector[1]].equals("#")){
                            cityMap.get(secondPos[0]-distanceVector[0])[secondPos[1]-distanceVector[1]] = "#";
                            nodeCount++;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }

        return nodeCount;
    }

    /**
     * Helper function for both parts.
     * Creates a deep copy of the given cityMap, so each part can
     * write on their own grid as necessary
     * @param cityMap An ArrayList of String[], each element of the String[] consisting of
     * a single alphanumeric character, symbol, or dot.
     * @return an ArrayList of String[], each value being identical to the given cityMap
     */
    public static ArrayList<String[]> deepCopy(ArrayList<String[]> cityMap){
        ArrayList<String[]> copy = new ArrayList<String[]>();
        for (int i=0; i < cityMap.size(); i++){
            String[] line = new String[cityMap.get(0).length];
            for (int j=0; j<line.length; j++){
                line[j] = cityMap.get(i)[j];
            }
            copy.add(line);
        }
        return copy;
    }

    /**
     * Helper function for both sections.
     * Finds all antenna in the given grid, and saves their positions in a HashMap
     * where the key is the frequency symbol, and the value is an ArrayList of int[]
     * each int[] containing their coordinate indexes on the grid.
     * @param cityMap An ArrayList of String[], each element of the String[] consisting of
     * a single alphanumeric character, symbol, or dot.
     * @return antenni A HashMap of the positions of each antenna by frequency type.
     */
    public static HashMap<String, ArrayList<int[]>> findAntenni(ArrayList<String[]> cityMap){
        HashMap<String, ArrayList<int[]>> antenni = new HashMap<String, ArrayList<int[]>>();
        for (int i=0; i<cityMap.size(); i++){
            for (int j=0; j<cityMap.size(); j++){
                if (!cityMap.get(i)[j].equals(".")){
                    int[] coords = {i, j};
                    if (antenni.containsKey(cityMap.get(i)[j])){
                        antenni.get(cityMap.get(i)[j]).add(coords);
                    }else {
                        ArrayList<int[]> antennaSpots = new ArrayList<int[]>();
                        antennaSpots.add(coords);
                        antenni.put(cityMap.get(i)[j], antennaSpots);
                    }
                }
            }
        }
        return antenni;
    }

    /**
     * Solution for part 2.
     * Similar to part 1, but antinodes can now occur at any multiple 
     * of the distance vector from an antenna.
     * @param cityMap An ArrayList of String[], each element of the String[] consisting of
     * a single alphanumeric character, symbol, or dot.
     * @return nodeCount The number of antinodes created within the given grid.
     */
    public static int part2(ArrayList<String[]> cityMap){
        int nodeCount = 0;
        // Find the types of antenni and their locations
        HashMap<String, ArrayList<int[]>> antenni = findAntenni(cityMap);

        // for each pair of a single type, try to place antinodes
        for (String key : antenni.keySet()){
            if(antenni.get(key).size()<2){ // ignore if there's only 1 of an antenna
                continue;
            }
            for (int first = 0; first < antenni.get(key).size()-1; first++){
                int[] firstPos = antenni.get(key).get(first);
                for (int second = first+1; second < antenni.get(key).size(); second++){
                    int[] secondPos = antenni.get(key).get(second);
                    int[] distanceVector = {firstPos[0]-secondPos[0], firstPos[1]-secondPos[1]};
                    // check if distanceVector is 0, which shouldn't happen but it does
                    if (distanceVector[0] == 0 && distanceVector[1] == 0){
                        continue;
                    }
                    int vectorMult = 0;
                    while (0 <= firstPos[0]+vectorMult*distanceVector[0] && firstPos[0]+vectorMult*distanceVector[0] < cityMap.size()
                        && 0 <= firstPos[1]+vectorMult*distanceVector[1] && firstPos[1]+vectorMult*distanceVector[1] < cityMap.get(0).length) {
                        if (!cityMap.get(firstPos[0]+vectorMult*distanceVector[0])[firstPos[1]+vectorMult*distanceVector[1]].equals("#")){
                            cityMap.get(firstPos[0]+vectorMult*distanceVector[0])[firstPos[1]+vectorMult*distanceVector[1]] = "#";
                            nodeCount++;
                        }
                        vectorMult++;
                    }
                    vectorMult = 0;
                    while (0 <= secondPos[0]+vectorMult*distanceVector[0] && secondPos[0]+vectorMult*distanceVector[0] < cityMap.size()
                        && 0 <= secondPos[1]+vectorMult*distanceVector[1] && secondPos[1]+vectorMult*distanceVector[1] < cityMap.get(0).length) {
                        if (!cityMap.get(secondPos[0]+vectorMult*distanceVector[0])[secondPos[1]+vectorMult*distanceVector[1]].equals("#")){
                            cityMap.get(secondPos[0]+vectorMult*distanceVector[0])[secondPos[1]+vectorMult*distanceVector[1]] = "#";
                            nodeCount++;
                        }
                        vectorMult--;
                    }
                    
                }
            }
        }


        return nodeCount; 
    }
}
