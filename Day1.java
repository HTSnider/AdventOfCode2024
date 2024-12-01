import java.io.*;
import java.util.*;

/**
 * Program for day 1 of Advent of Code 2024.
 */
public class Day1 {
    /**
     * Main method which intakes and parses the input file, "input01.txt",
     * which consists of pairs of integers separated by a variable number of spaces.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ArrayList<Integer>  leftList = new ArrayList<Integer>();
        ArrayList<Integer>  rightList = new ArrayList<Integer>();

        File inputFile = new File("src\\input01.txt");
        Scanner inputReader = new Scanner(inputFile);
        String temp;
        String[] splitTemp = {"",""};
        while(inputReader.hasNextLine()){
            temp = inputReader.nextLine();
            splitTemp = temp.split(" +");
            //System.out.print("$ " + temp + " # " + splitTemp[0] + " | " + splitTemp[1]);
            leftList.add(Integer.parseInt(splitTemp[0]));
            rightList.add(Integer.parseInt(splitTemp[1]));
        }
        inputReader.close();

        //int ans = part1(leftList, rightList);
        int ans = part2(leftList, rightList);
        System.out.println(ans);
    }

    /**
     * Solution for part 1. 
     * Sorts both lists and computes the sum of the differences between entries
     * @param left The first list, consisting of all left elements of the pairs
     * @param right The second list, consisting of all right elements of the pairs
     * @return sumdiff The sum of the absolute difference between each list, when sorted from lowest to highest
     */
    static int part1(ArrayList<Integer> left, ArrayList<Integer> right){
        Collections.sort(left);
        Collections.sort(right);
        int sumdiff = 0;
        for(int i=0; i<left.size(); i++){
            sumdiff += Math.abs(left.get(i) - right.get(i));
        }
        return sumdiff;
    }

    /**
     * Solution for part 2.
     * Finds how many times each unique entry in the left list appears in the right list,
     *  then finds the sum of their products.
     * @param left The first list, consisting of all left elements of the pairs
     * @param right The second list, consisting of all right elements of the pairs
     * @return diffScore The sum of the product of each integer entry and its frequency
     */
    static int part2(ArrayList<Integer> left, ArrayList<Integer> right){
        HashMap<Integer, Integer> freqChart = new HashMap<Integer, Integer>();
        for(int i=0; i<left.size(); i++){
            if (!freqChart.containsKey(left.get(i))){
                freqChart.put(left.get(i),0);
            }
        }
        for(int i=0; i<right.size(); i++){
            if (freqChart.containsKey(right.get(i))){
                freqChart.put(right.get(i), freqChart.get(right.get(i))+1);
            }
        }
        int diffScore = 0;
        Iterator<Map.Entry<Integer, Integer>> it = freqChart.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>)it.next();
            diffScore += (Integer) pair.getKey() * (Integer) pair.getValue();
        }
        return diffScore;
    }
}
