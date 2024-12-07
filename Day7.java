import java.io.*;
import java.util.*;

/**
 * Program for day 7 of Advent of Code 2024.
 */
public class Day7 {

    /**
     * Main method which intakes the input file and parses it, the input file consisting of
     * N lines, each comprised of a Long to the left of the colon, and a variable number of 
     * space separated integers to the right of it. There are always at least 2 integers.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input07.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<Long[]> calibrations = new ArrayList<Long[]>();
        while(in.hasNextLine()){
            String[] tempStrings = in.nextLine().split(":? ");
            Long[] tempInts = new Long[tempStrings.length];
            for(int i=0; i<tempStrings.length; i++){
                tempInts[i]=Long.parseLong(tempStrings[i]);
            }
            calibrations.add(tempInts);
        }
        in.close();

        //System.out.println(part1(calibrations));
        System.out.println(part2(calibrations));
    }

    /**
     * Solution for part 1.
     * Finds the sum of all calibration values (the Longs to the left of the colons), for which
     * all the integers on the right can be added or multiplied to equal the calibration value,
     * keeping the integers in order.
     * @param calibrations An ArrayList of Long[], each consisting of a calibration value 
     * followed by the component values
     * @return calSum The sum of all the calibration values that can be exactly reached.
     */
    public static Long part1(ArrayList<Long[]> calibrations){
        Long calSum = Long.valueOf(0);
        for (int calIx=0; calIx<calibrations.size(); calIx++){
            if (calibrate(calibrations.get(calIx),Long.valueOf(0),1)){
                calSum += calibrations.get(calIx)[0];
            }
        }
        return calSum;
    }

    /**
     * A recursive helper function for part 1. 
     * Determines if the calibration value can be exactly reached by adding or multiplying each component value.
     * @param calibration A Long[] of variable length, with calibration[0] being the target calibration value.
     * @param currTotal A running total of the preceding operations on component values.
     * @param ix The index of the current component value, to be applied to the running total.
     * @return true if the calibration value is exactly reached after all component values are applied,
     * or false otherwise
     */
    static public boolean calibrate(Long[] calibration, Long currTotal, int ix){
        if(ix >= calibration.length){
            if(currTotal.equals(calibration[0])){
                System.out.println("Success with "+calibration[0]);
                return true;
            }
            return false;
        }
        Long plusTotal = currTotal + calibration[ix];
        Long multTotal = currTotal * calibration[ix];

        return calibrate(calibration, plusTotal, ix+1) || calibrate(calibration, multTotal, ix+1);
    }

    /**
     * Solution for part 2.
     * Finds the sum of all calibration values (the Longs to the left of the colons), for which
     * all the integers on the right can be added, multiplied or concatenated to equal the calibration value,
     * keeping the integers in order.
     * @param calibrations An ArrayList of Long[], each consisting of a calibration value 
     * followed by the component values
     * @return calSum The sum of all the calibration values that can be exactly reached.
     */
    static public Long part2(ArrayList<Long[]> calibrations){
        Long calSum= 0L;
        for(int i = 0; i < calibrations.size(); i++){
            if(calibratePart2(calibrations.get(i), 0L, 1)){
                calSum += calibrations.get(i)[0];
            }
        }
        return calSum;
    }

    /**
     * A recursive helper function for part 2. 
     * Determines if the calibration value can be exactly reached by adding, multiplying or concatenating each component value.
     * @param calibration A Long[] of variable length, with calibration[0] being the target calibration value.
     * @param currTotal A running total of the preceding operations on component values.
     * @param ix The index of the current component value, to be applied to the running total.
     * @return true if the calibration value is exactly reached after all component values are applied,
     * or false otherwise
     */
    static public boolean calibratePart2(Long[] calibration, Long currTotal, int ix){
        if(ix >= calibration.length){
            if(currTotal.equals(calibration[0])){
                System.out.println("Success with "+calibration[0]);
                return true;
            }
            return false;
        }
        Long plusTotal = currTotal + calibration[ix];
        Long multTotal = currTotal * calibration[ix];
        Long mergeTotal = Long.valueOf(currTotal.toString()+calibration[ix].toString());

        return calibratePart2(calibration, plusTotal, ix+1) || calibratePart2(calibration, multTotal, ix+1) || calibratePart2(calibration, mergeTotal, ix+1);

    }
}
