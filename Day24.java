import java.io.*;
import java.math.BigInteger;
import java.util.*;


public class Day24 {

    // A Hashmap to store each gate and its output value
    public static HashMap<String, Integer> gates = new HashMap<String, Integer>();
    // A HashMap to store what the values in the z gates should be. (ie X add Y)
    public static HashMap<String, Integer> gatesCorrect = new HashMap<String, Integer>();
    // A HashMap to store each operation that feeds into another gate.
    public static HashMap<String, String[]> operations = new HashMap<String, String[]>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * N lines of describing the innput gates and their values, then an 
     * empty line, then M lines of form gate1 OPERATION gate2 -> gate3.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input24.txt");
        Scanner in = new Scanner(inFile);
        while(in.hasNextLine()){
            String line = in.nextLine();
            if(line.equals( "")){
                break;
            }
            String[] temp = line.split(": ");
            gates.put(temp[0], Integer.parseInt(temp[1]));
            gatesCorrect.put(temp[0], Integer.parseInt(temp[1]));
        }
        while(in.hasNextLine()){
            String line = in.nextLine();
            String[] temp = line.split(" -> ");
            operations.put(temp[1], temp[0].split(" "));
        }

        in.close();
        BigInteger z = BigInteger.valueOf(part1());
        System.out.println(z);
        System.out.println(part2(z));
    }
    
    /**
     * Solution for part 1.
     * Given a list of source gates, and a listt of operations
     * that produce more gates, find the values of the gates
     * starting with z, and find the decimal value of their
     * resulting binary string.
     * @return
     */
    public static Long part1(){
        // evaluate the possible operations until all are complete
        evaluateOps();

        // find the value of the z gates as a single integer
        return getZs();
    }

    /**
     * Helper function for part 1.
     * Cycle through each operation, and evaluate each gate's value
     * based on its component gates, and its operation AND|OR|XOR.
     * Stores the results in a global HashMap.
     */
    public static void evaluateOps(){
        ArrayDeque<String> processQueue = new ArrayDeque<String>();
        processQueue.addAll(operations.keySet());
        while (!processQueue.isEmpty()){
            String key = processQueue.pollFirst();
            String[] operation = operations.get(key);
            if(gates.containsKey(operation[0]) && gates.containsKey(operation[2])){
                switch (operation[1]){
                    case "AND":
                        gates.put(key, gates.get(operation[0]) & gates.get(operation[2]));
                        break;
                    case "XOR":
                        gates.put(key, gates.get(operation[0]) ^ gates.get(operation[2]));
                        break;
                    case "OR":
                        gates.put(key, gates.get(operation[0]) | gates.get(operation[2]));
                }   
            }else{
                processQueue.addLast(key);
            }
        }
    }

    /**
     * Helper function for part 1.
     * Finds the values of the gates starting with z, in order,
     * and converts the resulting binary string to a decimal.
     * @return The decimal value of the z gates.
     */
    public static Long getZs(){
        ArrayList<String> zGates = new ArrayList<String>();
        gates.keySet().forEach((String s) ->{ if (s.startsWith("z")){zGates.add(s);}});
        Collections.sort(zGates);
        Collections.reverse(zGates);
        String zOut = "";
        for (String z : zGates){
            zOut += gates.get(z);
        }
        Long out = Long.valueOf(zOut,2);
        return out;
    }

    /**
     * Solution for part 2.
     * Given that the gates from the previous part are intended to
     * add x and y to produce z, find the gates whose outputs have 
     * been swapped, and return them in alphabetical order.
     * @param currZ The decimal value produced by the original circuit.
     * @return The gates that were swapped, in alphabetical order,
     * concatenated with ,
     */
    public static String part2(BigInteger currZ){
        // find the desired z values
        BigInteger z = addXY();
        
        // find the gates that are incorrect.
        ArrayList<String> swapGates = wrongGates();

        // sort the incorrect gates alphabetically
        HashSet<String> tempSet = new HashSet<String>(swapGates);
        swapGates.clear();
        swapGates.addAll(tempSet);
        Collections.sort(swapGates);
        return String.join(",", swapGates);
    }

    /**
     * Helper function for part 2.
     * Collects the values in the x and y input gates, and 
     * finds their sum.
     * @return The sum of x and y
     */
    public static BigInteger addXY(){
        ArrayList<String> xGates = new ArrayList<String>();
        ArrayList<String> yGates = new ArrayList<String>();
        for(String key : gatesCorrect.keySet()){
            if (key.startsWith("x")){
                xGates.add(key);
            }
            if (key.startsWith("y")){
                yGates.add(key);
            }
        }
        Collections.sort(xGates);
        Collections.sort(yGates);
        Collections.reverse(xGates);
        Collections.reverse(yGates);
        String xString = "";
        String yString = "";
        for(int i = 0; i < xGates.size(); i++){
            xString += gatesCorrect.get(xGates.get(i));
            yString += gatesCorrect.get(yGates.get(i));
        }
        BigInteger x = BigInteger.valueOf(Long.valueOf(xString, 2));

        BigInteger y = BigInteger.valueOf(Long.valueOf(yString, 2));
        BigInteger z = x.add(y);
        int numDigits = xGates.size();
        String zString = Integer.toBinaryString(z.intValue());
        zString = "0".repeat(numDigits - zString.length()) + zString;
        String[] zArr = zString.split("");
        for(int i = 0; i < zArr.length; i++){
            String key = String.format("z%02d", i);
            gatesCorrect.put(key, Integer.parseInt(zArr[i]));
        }
        return z;
    }

    /**
     * Helper function for part 2.
     * Finds gates that break rules for a cascade half adder circuit.
     * @return An ArrayList of gates that have their outputs swapped.
     */
    public static ArrayList<String> wrongGates(){
        ArrayList<String> incorrect = new ArrayList<String>();        
        
        for(String gate : operations.keySet()){
            String[] currOp = operations.get(gate);
            String left = currOp[0];
            String op = currOp[1];
            String right = currOp[2];
            // Case: a z gate that is not the highest gate has an operation other than XOR
            if( gate.startsWith("z") && !op.equals("XOR") && !gate.equals("z45")){
                incorrect.add(gate);
            }
            // Case: a XOR operation does not involve an input or output gate
            if( op.equals("XOR") && !isXYZ(gate) && !isXYZ(left) && !isXYZ(right)){
                incorrect.add(gate);
            }
            // Case: An AND operation that does not involve x00
            if (op.equals("AND") && !(left.equals("x00") || right.equals("x00"))){
                for (String gate2 : operations.keySet()){
                    // and the gate is not ORed with another gate
                    if((gate.equals(operations.get(gate2)[0]) || gate.equals(operations.get(gate2)[2]))&&!(operations.get(gate2)[1].equals("OR"))){
                        incorrect.add(gate);
                    }
                }
            }
            // Case: a XOR operation 
            if (op.equals("XOR")){
                for (String gate2 : operations.keySet()){
                    // and the gate is ORed with another gate.
                    if((gate.equals(operations.get(gate2)[0]) || gate.equals(operations.get(gate2)[2]))&&(operations.get(gate2)[1].equals("OR"))){
                        incorrect.add(gate);
                    }
                }
            }
        }
        return incorrect;
    }

    /**
     * Helper function for part 2.
     * Determines if a given gate is an input or output gate.
     * @param gateName
     * @return
     */
    public static boolean isXYZ(String gateName){
        if(gateName.startsWith("x") || gateName.startsWith("y") || 
            gateName.startsWith("z")){
            return true;
        }
        return false;
    }
    
}
