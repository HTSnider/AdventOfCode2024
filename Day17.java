import java.io.*;
import java.util.*;
import java.math.BigInteger;

/**
 * Program for day 17 of Advent of Code 2024
 */
public class Day17 {
    
    // Holds the values for the A, B, and C registers. 
    static ArrayList<BigInteger> registers = new ArrayList<BigInteger>();
    // Holds the instructions, each an octal digit
    static ArrayList<Integer> operations = new ArrayList<Integer>();
    // the index of the current instruction within operations
    static Integer instructionPtr = 0;
    // Holds the results of each out operation
    static ArrayList<Integer> output = new ArrayList<Integer>();
 
    /**
     * Main method that intakes and parses the input file, which consists of
     * 3 lines stating the initial values in the registers A, B and C 
     * respectively, followed by an empty line, followed by the comma-
     * separated program instructions as octal digits.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input17.txt");
        Scanner in = new Scanner(inFile);
        
        registers.add(BigInteger.valueOf(Integer.parseInt(in.nextLine().split(": ")[1])));
        registers.add(BigInteger.valueOf(Integer.parseInt(in.nextLine().split(": ")[1])));
        registers.add(BigInteger.valueOf(Integer.parseInt(in.nextLine().split(": ")[1])));
        in.nextLine();
        for (String s: in.nextLine().split(": ")[1].split(",")){
            operations.add(Integer.parseInt(s));
        }
        in.close();

        part1();
        printOutput();
        System.out.println(getTrueA());
    }

    /**
     * Solution for part 1.
     * Performs each operation program instructions.
     */
    public static void part1(){
        int numInstruction = operations.size();
        int opCode;
        int operand;
        while(instructionPtr<numInstruction-1){
            opCode = operations.get(instructionPtr);
            operand = operations.get(instructionPtr+1);
            callOperation(opCode, operand);
        }
    }

    /**
     * Helper function for part 1.
     * Outputs the results of the out operations to the console as
     * comma-separated octal digits.
     */
    public static void printOutput(){
        for (int i = 0; i < output.size(); i++){
            if(i!=0){
                System.out.print(",");
            }
            System.out.print(String.valueOf(output.get(i)));
        }
        System.out.println();
    }

    /**
     * Helper function for part 1.
     * Converts the opCode and operand instructions into an operation,
     * executes the specified operation, and updates the instruction 
     * pointer if the operation throws an exception.
     * @param opCode An int from 0 to 7, representing an operation.
     * @param operand An int from 0 to 7, representing the input value.
     * for the selected operation.
     * @return true if the operation succeeds, or false if not.
     */
    public static boolean callOperation(int opCode, int operand){
        try{
            switch(opCode){
                case 0: 
                    adv(operand);
                    break;
                case 1: 
                    bxl(operand);
                    break;
                case 2: 
                    bst(operand);
                    break;
                case 3: 
                    jnz(operand);
                    break;
                case 4: 
                    bxc(operand);
                    break;
                case 5: 
                    out(operand);
                    break;
                case 6: 
                    bdv(operand);
                    break;
                case 7: 
                    cdv(operand);
                    break;
            }
        }catch(Exception e){
            instructionPtr+=2;
            return false;
        }
        return true;
    }

    /**
     * Operation function.
     * Divides the value in register A by the operand, and writes the
     * floor of the result to register A.
     * @param comboOperand Values 0-3 represent literals, values 4-6 
     * represent the values in register A, B and C respectively.
     * @return true if the operation succeeds, and false otherwise.
     */
    public static boolean adv(int comboOperand){
        BigInteger A = registers.get(0);
        BigInteger operand = checkCombo(comboOperand) ? registers.get(comboOperand-4) : BigInteger.valueOf(comboOperand);
        BigInteger other = BigInteger.valueOf(2L).pow(operand.intValue()); 
        BigInteger res = A.divideAndRemainder(other)[0];
        registers.set(0, res);
        instructionPtr+=2;
        return true;
    }

    /**
     * Operation function.
     * Finds the value in register B XOR the input operand, 
     * and writes the result to register B.
     * @param literalOperand An integer from 0 to 7.
     * @return true if the operation succeeds, and false otherwise.
     */
    public static boolean bxl(int literalOperand){
        BigInteger result = registers.get(1).xor(BigInteger.valueOf(literalOperand));
        registers.set(1, result);
        instructionPtr+=2;
        return true;
    } 

    /**
     * Operation function.
     * Takes the input operand mod 8, and writes the result to register B.
     * @param comboOperand Values 0-3 represent literals, and values 4-6
     * represent the values in registers A, B and C respectively.
     * @return true if the operations succeeds, or false otherwise.
     */
    public static boolean bst(int comboOperand){
        BigInteger other = checkCombo(comboOperand) ? registers.get(comboOperand-4) : BigInteger.valueOf(comboOperand);
        other = other.mod(BigInteger.valueOf(8));
        registers.set(1, other);
        instructionPtr+=2;
        return true;
    }

    /**
     * Operator function.
     * If the value in register A is not zero, sets the 
     * instruction pointer equal to the operand.
     * @param literalOperand An integer from 0 to 7.
     * @return true if the operation succeeds, or false otherwise.
     */
    public static boolean jnz(int literalOperand){
        BigInteger aVal = registers.get(0);
        if (!aVal.equals(BigInteger.ZERO)){
            instructionPtr = literalOperand;
            return true;
        }
        instructionPtr+=2;
        return false;
    }

    /**
     * Operation function.
     * Finds the result of the value in register B XOR with
     * the value in register C, and writes it to register B.
     * @param operand An integer from 0-7, not used.
     * @return true if the operation succeeds, or false otherwise.
     */
    public static boolean bxc(int operand){
        BigInteger bVal = registers.get(1);
        BigInteger cVal = registers.get(2);
        registers.set(1, bVal.xor(cVal));
        instructionPtr+=2;
        return true;
    }

    /**
     * Operation function.
     * Adds the operand to output.
     * @param comboOperand Values 0-3 represent literals, and values 4-6
     * represent the value in register A, B and C respectively.
     * @return
     */
    public static boolean out(int comboOperand){
        if(checkCombo(comboOperand)){
            BigInteger other = registers.get(comboOperand-4);
            output.add(other.mod(BigInteger.valueOf(8L)).intValue());
        } else{
            output.add(comboOperand);
        }
        instructionPtr+=2;
        return true;
    }

    /**
     * Operation function.
     * Divides the value in register A by the operand, and writes the
     * floor of the result to register B.
     * @param comboOperand Values 0-3 represent literals, values 4-6 
     * represent the values in register A, B and C respectively.
     * @return true if the operation succeeds, and false otherwise.
     */
    public static boolean bdv(int comboOperand){
        BigInteger A = registers.get(0);
        BigInteger operand = checkCombo(comboOperand) ? registers.get(comboOperand-4) : BigInteger.valueOf(comboOperand);
        BigInteger other = BigInteger.valueOf(2L).pow(operand.intValue());
        BigInteger res = A.divideAndRemainder(other)[0];
        registers.set(1, res);
        instructionPtr+=2;
        return true;
    }

    /**
     * Operation function.
     * Divides the value in register A by the operand, and writes the
     * floor of the result to register C.
     * @param comboOperand Values 0-3 represent literals, values 4-6 
     * represent the values in register A, B and C respectively.
     * @return true if the operation succeeds, and false otherwise.
     */
    public static boolean cdv(int comboOperand){
        BigInteger A = registers.get(0);
        BigInteger operand = checkCombo(comboOperand) ? registers.get(comboOperand-4) : BigInteger.valueOf(comboOperand);
        BigInteger other = BigInteger.valueOf(2).pow(operand.intValue());
        BigInteger res = A.divideAndRemainder(other)[0];
        registers.set(2, res);
        instructionPtr+=2;
        return true;
    }

    /**
     * Helper function for part 1.
     * Checks if the combination operand represents a literal
     * or a register value.
     * @param operand An integer from 0-7.
     * @return true if the operand is 4-7, or false otherwise. 
     */
    public static boolean checkCombo(int operand){
        if(4 <= operand && operand < 8){
            return true;
        }
        return false;
    }

    /**
     * Solution for part 2.
     * Starting from the last program instruction and A = 0,
     * finds the A values that cause the program to output its
     * instructions in order using breadth first search, and 
     * returns the smallest such A value.
     * @return A BigInteger, the smallest value of A that would
     * result in the program outputting its own instructions in
     * order.
     */
    public static BigInteger getTrueA(){
        ArrayDeque<BigInteger[]> processQueue = new ArrayDeque<BigInteger[]>();
        ArrayList<BigInteger> potentialAs = new ArrayList<BigInteger>();
        processQueue.add(new BigInteger[] {BigInteger.valueOf(operations.size()-1), BigInteger.ZERO});
        while (!processQueue.isEmpty()){
            BigInteger[] currTest = processQueue.pop();
            BigInteger i = currTest[0];
            BigInteger a = currTest[1];
            if (i.compareTo(BigInteger.ZERO)<0){
                continue;
            }
            for(int o = 0; o < 8; o++){
                reset();
                BigInteger test_a = a.shiftLeft(3).add(BigInteger.valueOf(o));
                registers.set(0, test_a);
                part1();
                if(!checkEquality()){
                    continue;
                }
                if (i.equals(BigInteger.ZERO)){
                    potentialAs.add(test_a);
                }
                processQueue.addLast(new BigInteger[] {i.subtract(BigInteger.ONE), test_a});
            }
        }
        Collections.sort(potentialAs);

        return potentialAs.get(0);
    }    

    /**
     * Helper function for part 2.
     * Checks if the output of the program is equal elementwise
     * to the ending elements of the program instructions.
     * @return true if the output is equal to the last n instructions,
     * where n is the size of the output, or false otherwise.
     */
    public static boolean checkEquality(){
        for (int i = 0; i < output.size(); i++){
            if(output.get(output.size()-1-i)!= operations.get(operations.size()-1-i)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Helper function for part 2.
     * Resets the values in registers B and C, as well as the 
     * instruction pointer.
     */
    public static void reset(){
        registers.set(1,BigInteger.ZERO);
        registers.set(2,BigInteger.ZERO);
        output.clear();
        instructionPtr = 0;
    }


}
