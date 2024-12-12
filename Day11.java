import java.io.*;
import java.util.*;
import java.math.BigInteger;

/**
 * Program for day 11 of Advent of Code 2024
 */
public class Day11 {
    // HashMap to cache the results of a single step on a single stone.
    static HashMap<Long, ArrayList<Long>> blinkMemo = new HashMap<Long, ArrayList<Long>>();
    
    /**
     * Main method that intakes the input file, which consists of
     * N space-separated integers on a single line.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input11.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<Long> stones = new ArrayList<Long>();
        String temp = in.nextLine();
        for (String s: temp.split(" ")){
            stones.add(Long.valueOf(s));
        }
        in.close();
        System.out.println(part1(stones));
        System.out.println(iterate75(stones));
    }

    /**
     * Solution for part 1.
     * Given a list of stones marked with numbers, and the rules by which they change at each step
     * (blink), find the number of stones after 25 steps.
     * Rules: 0 -> 1, numbers with an even number of digits are spli in half (string operation), 
     * other stones have their number multiplied by 2024.
     * @param stones An ArrayList of the starting stones at the start, each Stone's number being kept
     * as a Long to prevent overflow issues.
     * @return stoneCount, the number of stones after 25 steps.
     */
    public static int part1(ArrayList<Long> stones){
        int stoneCount = 0;
        for (Long stone: stones){
            stoneCount += blink25(stone);
        }
        return stoneCount;
    }

    /**
     * Helper function for part 1.
     * Starts with a single stone, and processes its changes over 25 steps, including its offshoots.
     * Stores each stone in the iteration in an ArrayDeque<Long>, and processes each stone once per
     * loop, for 25 loops. Makes use of cached single-stone changes to save time.
     * @param stone A Long, the value marked on the starting stone.
     * @return The number of stones that result after 25 steps.
     */
    public static int blink25(Long stone){
        ArrayDeque<Long> stones = new ArrayDeque<Long>();
        stones.add(stone);
        for (int i = 0; i<25; i++){
            ArrayDeque<Long> nextIter = new ArrayDeque<Long>();
            while(!stones.isEmpty()){
                Long currStone = stones.removeFirst();
                if(blinkMemo.containsKey(currStone)){
                    nextIter.addAll(blinkMemo.get(currStone));
                }else{
                    ArrayList<Long> stoneResult = blink(currStone);
                    blinkMemo.put(currStone, stoneResult);
                    nextIter.addAll(stoneResult);
                }
            }
            stones = nextIter;
        }
        return stones.size();
    }

    /**
     * Helper function for part 1.
     * Processes a single step of change on a single stone.
     * Return values cached in blinkMemo by the function blink25.
     * @param stone A Long, the value marked on a single stone.
     * @return outStones, an ArrayList<Long>, containing 1 or two Longs representing the changed stone.
     */
    public static ArrayList<Long> blink(Long stone){
        ArrayList<Long> outStones = new ArrayList<Long>();
        if(stone==0L){
            outStones.add(1L);
            return outStones;
        }
        String stoneString = Long.toString(stone);
        int stoneLen = stoneString.length();
        if(stoneLen%2==0){
            outStones.add(Long.valueOf(stoneString.substring(0, stoneLen/2)));
            outStones.add(Long.valueOf(stoneString.substring(stoneLen/2, stoneLen)));
            return outStones;
        }
        else{
            outStones.add(stone*2024);
            return outStones;
        }
    }

    /**
     * Solution for part 2.
     * Uses a completely different method than part 1, as otherwise the spacial complexity
     * would be too high. Stores stones in a hashmap, where the key is the stone's marking 
     * as a Long, and the value is the number of stones with that marking.
     * Finds the number of stones after 75 steps.
     * @param stones An ArrayList<Long> of stones.
     * @return stoneCount, The number of stones that result from 75 steps, as a BigInteger.
     */
    public static BigInteger iterate75(ArrayList<Long> stones){
        HashMap<Long, BigInteger> input = new HashMap<Long, BigInteger>();
        for(Long stone: stones){
            if(!input.containsKey(stone)){
                input.put(stone,BigInteger.valueOf(1));
            }else{
                input.replace(stone, input.get(stone).add(BigInteger.valueOf(1)));
            }
        }

        HashMap<Long, BigInteger> currIter = new HashMap<Long, BigInteger>();
        for (int i=0; i<75; i++){
            currIter.clear();
            for (Long key : input.keySet()){
                // accounts for removal of old stones
                BigInteger num = input.get(key);
                if (!currIter.containsKey(key)){
                    currIter.put(key, BigInteger.valueOf(0L));
                }
                currIter.replace(key, currIter.get(key).add(num.negate()));
                // adds new stones for each blink
                if(key.equals(0L)){
                    if(!currIter.containsKey(1L)){
                        currIter.put(1L, BigInteger.valueOf(0));
                    }
                    currIter.replace(1L, currIter.get(1L).add(num));
                }
                else if (key.toString().length()%2==0){
                    String left = key.toString().substring(0,key.toString().length()/2);
                    String right = key.toString().substring(key.toString().length()/2);
                    if(!currIter.containsKey(Long.valueOf(left))){
                        currIter.put(Long.valueOf(left), BigInteger.valueOf(0));
                    }
                    if(!currIter.containsKey(Long.valueOf(right))){
                        currIter.put(Long.valueOf(right), BigInteger.valueOf(0));
                    }
                    currIter.replace(Long.valueOf(left), currIter.get(Long.valueOf(left)).add(num));
                    currIter.replace(Long.valueOf(right), currIter.get(Long.valueOf(right)).add(num));
                }
                else{
                    if (!currIter.containsKey(key*2024L)){
                        currIter.put(key*2024L, BigInteger.valueOf(0));
                    }
                    currIter.replace(key*2024L, currIter.get(key*2024L).add(num));
                }
            }

            // update input dict
            for (Long key : currIter.keySet()){
                if(input.containsKey(key)){
                    input.replace(key, input.get(key).add(currIter.get(key)));
                }else{
                    input.put(key, currIter.get(key));
                }
            }
            

            // cleanup 
            Set<Long> keys = Set.copyOf(input.keySet());
            for (Long key: keys){
                if (input.get(key).equals(BigInteger.valueOf(0))){
                    input.remove(key);
                }
            }
        }
        // count stones in final line
        BigInteger stoneCount = BigInteger.valueOf(0);
        for (Long key: input.keySet()){
            stoneCount = stoneCount.add(input.get(key));
        }
        return stoneCount;
    }

}
