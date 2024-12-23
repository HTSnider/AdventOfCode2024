import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Program for day 22 of Advent of Code.
 */
public class Day22 {

    // List to hold input numbers, as BigIntegers to prevent overflow.
    public static ArrayList<BigInteger> secretNumbers = new ArrayList<BigInteger>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * N lines, each with one number.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input22.txt");
        Scanner in = new Scanner(inFile);
        while(in.hasNextLine()){
            secretNumbers.add(BigInteger.valueOf(Long.valueOf(in.nextLine())));
        }
        in.close();

        System.out.println(part1());
        System.out.println(part2());
    }
    
    /**
     * Helper function for both parts.
     * Finds the secret number xor the given other number.
     * @param secretNumber A BigInteger
     * @param other A second BigInteger
     * @return The BigInteger result of the secretNumber xor the other number.
     */
    public static BigInteger mix(BigInteger secretNumber, BigInteger other){
        return secretNumber.xor(other);
    }

    /**
     * Helper function for both parts.
     * Finds the modulo of the secret number mod a constant.
     * @param secretNumber A BigInteger
     * @return A BigInteger, the secret number mod 16777216
     */
    public static BigInteger prune(BigInteger secretNumber){
        return secretNumber.mod(BigInteger.valueOf(16777216L));
    }

    /**
     * Helper function for both parts.
     * The process to turn a secret number into the next secret number.
     * @param secretNumber A BigInteger
     * @return The next BigInteger secret number to be generated.
     */
    public static BigInteger evolve(BigInteger secretNumber){
        BigInteger curr = prune(mix(secretNumber.multiply(BigInteger.valueOf(64)), secretNumber));
        curr = prune(mix(curr, curr.divide(BigInteger.valueOf(32))));
        curr = prune(mix(curr, curr.multiply(BigInteger.valueOf(2048))));
        return curr;
    }

    /**
     * Solution for part 1.
     * Each monkey has a secret number, which is changed each step via a fixed 
     * process. Find the sum of the secret numbers after their 2000th change.
     * @return The sum of the 2000th secret numbers of the monkeys.
     */
    public static BigInteger part1(){
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger num : secretNumbers){
            BigInteger curr = num;
            for (int i = 0; i < 2000; i++){
                curr = evolve(curr);
            }
            sum = sum.add(curr);
        }

        return sum;
    }

    /**
     * Solution for part 2.
     * The price each monkey will pay for a secret is the last digit of its secret 
     * number. A monkey will only sell when they see a specific sequence of 4 price 
     * changes for the first time, or not at all. Find the one sequence that will give 
     * the most bananas from selling to all the monkeys.
     * @return
     */
    public static BigInteger part2(){
        ArrayDeque<BigInteger> window = new ArrayDeque<BigInteger>();
        HashMap<String, BigInteger> profits = new HashMap<String, BigInteger>();
        HashSet<String> seen = new HashSet<String>();
        for (BigInteger num : secretNumbers){
            window.clear();
            seen.clear();
            BigInteger curr = num;
            window.add(num);
            for (int i = 0; i < 2000; i++){
                curr = evolve(curr);
                window.addLast(curr);
                if(window.size()>5){
                    window.pollFirst();
                }
                if( window.size() == 5){ // when the sliding window has 5 secret numbers
                    BigInteger[] seq = getSequence(window); // get the price changes, and the final price
                    String hash = String.format("%d:%d:%d:%d", seq[0], seq[1], seq[2], seq[3]);
                    if(!seen.contains(hash)){
                        profits.put(hash, seq[4].add(profits.getOrDefault(hash, BigInteger.ZERO)));
                        seen.add(hash);
                    }
                }
            }
        }
        return Collections.max(profits.values());

    }

    /**
     * Helper function for part 2.
     * Given a series of five secret numbers, find the sequence of four
     * price changes, and the final sell price. The price is the last digit of a
     * secret number.
     * @param window An ArrayList of 5 secret numbers.
     * @return A BigInteger[] [firstChange, secondChange, thirdChange, fourthChange,
     * finalPrice]
     */
    public static BigInteger[] getSequence(ArrayDeque<BigInteger> window){
        BigInteger[] windowCopy =   window.toArray(new BigInteger[5]);
        BigInteger[] out = new BigInteger[5];
        for(int i = 0; i < 4; i++){
            out[i] = (windowCopy[i+1].mod(BigInteger.TEN).subtract(windowCopy[i].mod(BigInteger.TEN)));
        }
        out[4] = windowCopy[4].mod(BigInteger.TEN);
        return out;
    }

}
