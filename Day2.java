
import java.util.*;
import java.io.*;

/**
 * Program for day 2 of Advent of Code 2024.
 */
public class Day2 {
    
    /**
     * Main method which intakes and parses the input file,
     * which consists of an unknown number of lines of space-separated integers.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inputFile = new File("src\\input02.txt");
        Scanner in = new Scanner(inputFile);
        ArrayList<ArrayList<Integer>> reports = new ArrayList<ArrayList<Integer>>(); 
        String temp = "";
        while(in.hasNextLine()){
            temp = in.nextLine();
            ArrayList<Integer> currReport = new ArrayList<Integer>();
            for(String level: temp.split(" ")){
                currReport.add(Integer.parseInt(level));
            }
            reports.add(currReport);
        }
        in.close();

        System.out.println(part1(reports));
        System.out.println(part2(reports));
    }

    /**
     * Solution for part 1.
     * Finds if report series is strictly rising with a difference between 1 and 3 inclusive,
     *  or strictly falling with an absolute difference between 1 and 3 inclusive.
     * @param reports An ArrayList of reports, each report being an ArrayList of Integers.
     * @return safeCount The number of reports that satisfy the criteria.
     */
    static int part1(ArrayList<ArrayList<Integer>> reports){
        int safeCount = 0;
            for (ArrayList<Integer> report: reports){
                safeCount += isReportSafe(report);
            }
        return safeCount;
    }

    /** 
     * Helper function to check if a single report is safe, by the criteria of part 1.
     * @param report An ArrayList of Integers.
     * @return 1 if the report is safe, and 0 otherwise.
     */
    static int isReportSafe(ArrayList<Integer> report){
        Boolean risingFlag = report.get(0)<report.get(1);
        Boolean safeFlag = true;
        for(int i=0; i<report.size()-1; i++){
            int diff = 0;
            if (risingFlag){
                diff = report.get(i+1) - report.get(i);
            } else{
                diff = report.get(i) - report.get(i+1);
            }

            if (diff>3 || diff<1){
                safeFlag = false;
                break;
            }
        }
        return safeFlag? 1 : 0;
    }

    /**
     * Solution for part 2.
     * Finds number of reports that are strictly increasing or strictly decreasing, with absolute difference between 1 and 3 inclusive.
     * A report is still counted as safe if removing a single element makes it safe.
     * @param reports An ArrayList of reports, each report being an ArrayList of Integers.
     * @return safeCount, the number of reports that satisfy the criteria.
     */
    static int part2(ArrayList<ArrayList<Integer>> reports){
        int safeCount = 0;
        for (ArrayList<Integer> report: reports){
            if(isReportSafe(report)==1){
                safeCount += 1;
            } else{
                safeCount += isReportDampSafe(report);
            }
        }
        return safeCount;
    }

    /**
     * Helper for part 2.
     * Identifies the first point where a level change is unsafe, then \
     * evaluates whether removing either side of that change makes the report safe.
     * Also checks whether removing the first element results in a safe report,
     *  for the case when the first change direction is different from the other change directions.
     * @param report An ArrayList of Integers
     * @return 1 if the report is safe when 1 element is removed, or 0 otherwise.
     */
    static int isReportDampSafe(ArrayList<Integer> report){
        Boolean risingFlag = report.get(0)<report.get(1);
        int failPoint=-1;
        for(int i=0; i<report.size()-1; i++){
            int diff = 0;
            if (risingFlag){
                diff = report.get(i+1) - report.get(i);
            } else{
                diff = report.get(i) - report.get(i+1);
            }

            if (diff>3 || diff<1){
                failPoint = i;
                break;
            }
        }
        ArrayList<Integer> reportCopy1 = new ArrayList<Integer>();
        ArrayList<Integer> reportCopy2 = new ArrayList<Integer>();
        ArrayList<Integer> reportCopy3 = new ArrayList<Integer>();
        for (int j=0; j<report.size(); j++){
            if(j!= failPoint){
                reportCopy1.add(report.get(j));
            }
            if(j!= failPoint+1){
                reportCopy2.add(report.get(j));
            }
            if(j!=0){
                reportCopy3.add(report.get(j));
            }
        }
        int dampSafe = isReportSafe(reportCopy1) + isReportSafe(reportCopy2) + isReportSafe(reportCopy3);
        return dampSafe>0? 1:0;
    }
}