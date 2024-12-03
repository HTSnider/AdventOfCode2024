import java.io.*;
import java.util.*;
import java.util.regex.*;


/**
 * Program for day 3 of Advent of Code 2024.
 */
public class Day3 {

    /**
     * Main method which intakes the input file, 
     * which consists of corrupted instructions without spaces.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input03.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<String> inputLines = new ArrayList<String>();
        while(in.hasNextLine()){
            inputLines.add(in.nextLine());
        }
        in.close();
        String input = "";
        for(String str: inputLines){
            input+=str;
        }
        
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    /**
     * Solution for part 1.
     * Finds each properly formed multiplication operation
     *      mul(%d,%d)
     * and returns the sum of their products.
     * @param input A String of the malformed operations, with newlines removed.
     * @return productSum The sum of the product of each correctly formed multiplication operation.
     */
    public static int part1(String input){
        int productSum = 0;
        Pattern mulPattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
        Matcher matchOps = mulPattern.matcher(input);
        while(matchOps.find()){
            productSum += Integer.parseInt(matchOps.group(1)) * Integer.parseInt(matchOps.group(2));
        }

        return productSum;
    }

    /**
     * Solution for part 2.
     * Finds all properly formed multiplication operations, as well as do() and don't() operations.
     * When a multiplicatio operation is preceded by a do() operation, its product is added to productSum.
     * A multiplication operation preceded by a don't() operation is ignored.
     * Multiplication operations with no preceding do() or don't() operations are counted.
     * @param input A string of malformed operations, with newlines removed.
     * @return productSum The sum of the products of correctly formed and active multiplication operations.
     */
    public static int part2(String input){
        int productSum = 0;
        Boolean doFlag = true;

        Pattern mulDoPattern = Pattern.compile("(mul)\\((\\d+),(\\d+)\\)|(do)\\(\\)|(don\\'t)\\(\\)");
        Matcher matchOps = mulDoPattern.matcher(input);
        while(matchOps.find()){
            //System.out.println(matchOps.toString());
            String currOp = matchOps.group(1);
            if(currOp==null){
                currOp = matchOps.group(4);
                if (currOp==null){
                    currOp = matchOps.group(5);
                }
            }
            if (currOp.equals("do")){
                doFlag = true;
            }else if (currOp.equals("don't")){
                doFlag = false;
            }else if(doFlag){
                productSum += Integer.parseInt(matchOps.group(2)) * Integer.parseInt(matchOps.group(3));
            }
        }
        return productSum;
    }
}
