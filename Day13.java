import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Program for day 13 of Advent of Code.
 */
public class Day13 {
    // ArrayList of prizes, populated in main method.
    static ArrayList<ArrayList<Long[]>> prizes = new ArrayList<ArrayList<Long[]>>();

    /**
     * Main method which intakes and parses the input file, which consists of
     * N emptyline-separated prize listings, each having the change in X and Y
     * resulting from pushing button A, the change in X and Y resulting from
     * pushing button B, and the location of the prize in terms of X and Y.
     * Populates this information into the class variable prizes.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input13.txt");
        Scanner in = new Scanner(inFile);
        Pattern buttonVector = Pattern.compile("X\\+(\\d+), Y\\+(\\d+)$");
        Pattern targetVector = Pattern.compile("X=(\\d+), Y=(\\d+)$");
        
        while(in.hasNextLine()){
            ArrayList<Long[]> curr = new ArrayList<Long[]>();
            String AString = in.nextLine();
            String BString = in.nextLine();
            String prizeString = in.nextLine();
            try{
                in.nextLine(); // blank line
            }catch(Exception e){
            }
            Matcher matcher = buttonVector.matcher(AString);
            matcher.find();
            Long[] aVector = new Long[] {Long.valueOf(matcher.group(1)), Long.valueOf(matcher.group(2))};
            matcher = buttonVector.matcher(BString);
            matcher.find();
            Long[] bVector = new Long[] {Long.valueOf(matcher.group(1)), Long.valueOf(matcher.group(2))};
            matcher = targetVector.matcher(prizeString);
            matcher.find();
            Long[] prizeVector = new Long[] {Long.valueOf(matcher.group(1)), Long.valueOf(matcher.group(2))};
            curr.add(aVector);
            curr.add(bVector);
            curr.add(prizeVector);
            prizes.add(curr);
        }
        in.close();
        
        System.out.println(String.format("%.0f", part1()));
        System.out.println(String.format("%.0f", part2()));
    }

    /**
     * Solution for part 1.
     * Finds the number of pushes of each button it takes to exactly reach
     * each prize, when exactly reaching the prize is possible, then returns
     * the sum of the cost of pushing the buttons.
     * @return cost The cost to press enough buttons to reach all reachable
     * prizes.
     */
    public static Double part1(){
        Double cost = Double.valueOf(0L);
        for (ArrayList<Long[]> prize : prizes){
            cost += costButtons(findButtonPressesDet(prize));
        }
        return cost;
    }

    /**
     * First attempt at finding the number of button presses to reach the given 
     * prize. Uses a system of equations to solve for the numbers of presses.
     * This fails because of rounding errors.
     * @param prizeVals An ArrayList of Long[], the first being the change vector
     * from pushing button A, the second being the change vector from pushing
     * Button B, and the third being the position vector of the prize.
     * @return A Double[] [AButtonPresses, BButtonPresses], or [0,0] if exactly
     * reaching the prize is not possible.
     */
    public static Double[] findButtonPresses(ArrayList<Long[]> prizeVals){
        Long[] aVector = prizeVals.get(0);
        Long[] bVector = prizeVals.get(1);
        Long[] prizeVector = prizeVals.get(2);
        Double ax = Double.valueOf(aVector[0]);
        Double ay = Double.valueOf(aVector[1]);
        Double bx = Double.valueOf(bVector[0]);
        Double by = Double.valueOf(bVector[1]);
        Double px = Double.valueOf(prizeVector[0]);
        Double py = Double.valueOf(prizeVector[1]);

        // solving aVector*c +bVector*d = prizeVector gives constants.
        Double a = Double.valueOf(0L);
        Double b = Double.valueOf(0L);
        try{
            
            b = (py - ((ay*px)/ax))/
                (by - ((ay*bx)/ax));

            a = (px - (by*b))/(ax);
        }catch(Exception e){

        }
        if( a*ax + b*bx == px && a*ay + b*by == py ){
            return new Double[] {a, b};
        }
        else{
            return new Double[] {Double.valueOf(0L), Double.valueOf(0L)};
        }

    }

    /**
     * Helper function for part 1.
     * Uses a solution derived from matrix multiplication to find the number of
     * presses on each button needed to exactly reach the prize.
     * @param prizeVals An ArrayList of Long[], the first being the change vector
     * from pushing button A, the second being the change vector from pushing
     * Button B, and the third being the position vector of the prize.
     * @return A Double[] [AButtonPresses, BButtonPresses], or [0,0] if exactly
     * reaching the prize is not possible.
     */
    public static Double[] findButtonPressesDet(ArrayList<Long[]> prizeVals){
        Long[] aVector = prizeVals.get(0);
        Long[] bVector = prizeVals.get(1);
        Long[] prizeVector = prizeVals.get(2);
        Double ax = Double.valueOf(aVector[0]);
        Double ay = Double.valueOf(aVector[1]);
        Double bx = Double.valueOf(bVector[0]);
        Double by = Double.valueOf(bVector[1]);
        Double px = Double.valueOf(prizeVector[0]);
        Double py = Double.valueOf(prizeVector[1]);
        
        Double a = ((by*px)/((ax*by)-(ay*bx)))- ((bx*py)/((ax*by)-(ay*bx)));
        Double b = ((ax*py)/((ax*by)-(ay*bx))) - ((ay*px)/((ax*by)-(ay*bx)));
        
        if(px==Math.rint(a)*ax+Math.rint(b)*bx && py==Math.rint(a)*ay+Math.rint(b)*by && a>=0 && b>=0){
            return new Double[] {a,b};
        }
        return new Double[] {Double.valueOf(0L), Double.valueOf(0L)};
        

    }

    /**
     * Solution to part 2.
     * Similar to part 1, except the prize is 10000000000000 away in both the
     * X and Y directions.
     * @return The cost for pushing buttons to exactly reach every reachable
     * prize.
     */
    public static Double part2(){
        Double cost = Double.valueOf(0L);
        for (ArrayList<Long[]> prize : prizes){
            cost += costButtons(findButtonPressesDet2(prize));
        }
        return cost;
    }

    /**
     * Helper function for part 2.
     * Almost identical to findButtonPressesDet except each position coordinate
     * is first increased by 10000000000000.
     * @param prizeVals An ArrayList of Long[], the first being the change vector
     * from pushing button A, the second being the change vector from pushing
     * Button B, and the third being the position vector of the prize.
     * @return A Double[] [AButtonPresses, BButtonPresses], or [0,0] if exactly
     * reaching the prize is not possible.
     */
    public static Double[] findButtonPressesDet2(ArrayList<Long[]> prizeVals){
        Double extraHeight = Double.valueOf(10000000000000L);
        Long[] aVector = prizeVals.get(0);
        Long[] bVector = prizeVals.get(1);
        Long[] prizeVector = prizeVals.get(2);
        Double ax = Double.valueOf(aVector[0]);
        Double ay = Double.valueOf(aVector[1]);
        Double bx = Double.valueOf(bVector[0]);
        Double by = Double.valueOf(bVector[1]);
        Double px = Double.valueOf(prizeVector[0])+extraHeight;
        Double py = Double.valueOf(prizeVector[1])+extraHeight;
        
        Double a = ((by*px)/((ax*by)-(ay*bx)))- ((bx*py)/((ax*by)-(ay*bx)));
        Double b = ((ax*py)/((ax*by)-(ay*bx))) - ((ay*px)/((ax*by)-(ay*bx)));
        
        if(px==Math.rint(a)*ax+Math.rint(b)*bx && py==Math.rint(a)*ay+Math.rint(b)*by && a>=0 && b>=0){
            return new Double[] {a,b};
        }
        return new Double[] {Double.valueOf(0L), Double.valueOf(0L)};
        

    }

    /**
     * Helper function for both parts.
     * Finds the cost of reaching a prize given the number of times each
     * button must be pressed. Filters out solutions with negative numbers
     * of button presses.
     * @param presses A Double[] [AButtonPresses, BButtonPresses]
     * @return The cost to press the buttons necessary to reach the prize.
     */
    public static Double costButtons(Double[] presses){
        if(presses[0] >= 0  && presses[1] >= 0){
            return presses[0]*3 + presses[1];
        }
        else{
            return Double.valueOf(0L);
        }
    }
}
