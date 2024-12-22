import java.io.*;
import java.util.*;

/**
 * Program for day 21 of Advent of Code 2024.
 */
public class Day21 {
    
    // List of the input codes.
    public static ArrayList<String> codes = new ArrayList<String>();
    // 2D ArrayList of buttons on the numeric keypad, with X representing an empty space.
    public static ArrayList<ArrayList<String>> numPad = new ArrayList<ArrayList<String>>();
    // 2D ArrayList of buttons on the directional keypad, with X representing an empty space.
    public static ArrayList<ArrayList<String>> dirPad = new ArrayList<ArrayList<String>>();
    // HashMap to link each button's string (key) to its position on the directional keypad.
    public static HashMap<String, Integer[]> dirPadButtons = new HashMap<String, Integer[]>();
    // HashMap to link each button's string (key) to its position on the numeric keypad.    
    public static HashMap<String, Integer[]> numPadButtons = new HashMap<String, Integer[]>();
    // HashMap of the ways to go from button 1 to button 2 on the first robot directional pad,
    // where the value is the least number of human inputs to do so.
    public static HashMap<String, Integer> bestDirMovesFirst = new HashMap<String, Integer>();
    // HashMap of the ways to go from button 1 to button 2 on the second robot directional pad,
    // where the value is the least number of human inputs to do so.
    public static HashMap<String, Integer> bestDirMovesSecond = new HashMap<String, Integer>();
    // HashMap of the ways to go from button 1 to button 2 on the robot numeric pad,
    // where the value is the least number of human inputs to do so.
    public static HashMap<String, Integer> bestNumMoves = new HashMap<String, Integer>();

    /**
     * Main method that intakes and parses the input file, which consists of
     * N lines of codes, each having M digits and ending with an A.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File inFile = new File("src\\input21.txt");
        Scanner in = new Scanner(inFile);
        while(in.hasNextLine()){
            codes.add(in.nextLine());
        }
        in.close();
        initPads();
        findCosts();

        System.out.println(part1());
        System.out.println(part2());
    }

    /**
     * Helper function:
     * Populates the 2d arrays and buttom mappings for the numeric and
     * directional keypads.
     */
    public static void initPads(){
        // Numeric keypad
        ArrayList<String> firstLine = new ArrayList<String>();
        firstLine.add("7");
        firstLine.add("8");
        firstLine.add("9");
        numPad.add(firstLine);
        ArrayList<String> secondLine = new ArrayList<String>();
        secondLine.add("4");
        secondLine.add("5");
        secondLine.add("6");
        numPad.add(secondLine);
        ArrayList<String> thirdLine = new ArrayList<String>();
        thirdLine.add("1");
        thirdLine.add("2");
        thirdLine.add("3");
        numPad.add(thirdLine);
        ArrayList<String> fourthLine = new ArrayList<String>();
        fourthLine.add("X");
        fourthLine.add("0");
        fourthLine.add("A");
        numPad.add(fourthLine);

        // Numeric Keypad button vectors
        numPadButtons.put("7", new Integer[] {0, 0});
        numPadButtons.put("8", new Integer[] {0, 1});
        numPadButtons.put("9", new Integer[] {0, 2});
        numPadButtons.put("4", new Integer[] {1, 0});
        numPadButtons.put("5", new Integer[] {1, 1});
        numPadButtons.put("6", new Integer[] {1, 2});
        numPadButtons.put("1", new Integer[] {2, 0});
        numPadButtons.put("2", new Integer[] {2, 1});
        numPadButtons.put("3", new Integer[] {2, 2});
        
        numPadButtons.put("0", new Integer[] {3, 1});
        numPadButtons.put("A", new Integer[] {3, 2});

        // Directional keypad
        ArrayList<String> topLine = new ArrayList<String>();
        topLine.add("X");
        topLine.add("^");
        topLine.add("A");
        dirPad.add(topLine);
        ArrayList<String> bottomLine = new ArrayList<String>();
        bottomLine.add("<");
        bottomLine.add("v");
        bottomLine.add(">");
        dirPad.add(bottomLine);

        // Directional keypad button vectors

        dirPadButtons.put("^", new Integer[] {0, 1});
        dirPadButtons.put("A", new Integer[] {0, 2});
        dirPadButtons.put("<", new Integer[] {1, 0});
        dirPadButtons.put("v", new Integer[] {1, 1});
        dirPadButtons.put(">", new Integer[] {1, 2});
    }

    /**
     * Helper function for part 1.
     * Finds the best way to move the first robot from/to each button,
     * then uses that to find the best way to move the second robot to
     * each button, and then uses that to find the best way to move the
     * third robot to each button. Each is stored in a global HashMap variable.
     */
    public static void findCosts(){
        // find the number of inputs to move from each button to each other button
        // first directional pad
        for (String startButton : dirPadButtons.keySet()){
            for (String endButton : dirPadButtons.keySet()){
                bestDirMovesFirst.put(startButton +":"+ endButton, moveDir(dirPadButtons.get(startButton), dirPadButtons.get(endButton), false).size());
            }
        }
        dijkstraDirPad();
        dijkstraNumPad();
    }

    /**
     * Helper function for part 1.
     * Finds the best way to move the second robot arm to different buttons,
     * in least human inputs for each button-to-button pair.
     */
    public static void dijkstraDirPad(){
        for (String startButton : dirPadButtons.keySet()){
            for (String endButton : dirPadButtons.keySet()){
                int numMoves = 0;
                ArrayList<String> moves = moveDir(dirPadButtons.get(startButton), dirPadButtons.get(endButton), false);
                for (int i = 0; i < moves.size(); i++ ){
                    if (i==0){
                        numMoves += bestDirMovesFirst.get("A:"+moves.get(i));
                    }
                    else{
                        numMoves += bestDirMovesFirst.get(moves.get(i-1) +":"+ moves.get(i));
                    }
                }
                bestDirMovesSecond.put(startButton +":"+ endButton, numMoves);
            }
        }
    }

    /**
     * Helper function for part 2.
     * Finds the best way to move the next robot's arm to different buttons on its
     * directional pad, based on the current robot's best movements.
     * @param prevMap The HashMap of button-button pairs to least human inputs.
     * @return outMap A new HashMap of button-button pairs to least human inputs,
     * one level deeper.
     */
    public static HashMap<String, Long> dijkstraDirPadRepeat(HashMap<String,Long> prevMap){
        HashMap<String, Long> newMap = new HashMap<>();
        for (String startButton : dirPadButtons.keySet()){
            for (String endButton : dirPadButtons.keySet()){
                Long numMoves = 0L;
                ArrayList<String> moves = moveDir(dirPadButtons.get(startButton), dirPadButtons.get(endButton), false);
                for (int i = 0; i < moves.size(); i++ ){
                    if (i==0){
                        numMoves += prevMap.get("A:"+moves.get(i));
                    }
                    else{
                        numMoves += prevMap.get(moves.get(i-1) +":"+ moves.get(i));
                    }
                }
                newMap.put(startButton +":"+ endButton, numMoves);
            }
        }
        return newMap;
    }

    /**
     * Helper function for part 1.
     * Finds the best way to move the third robot between buttons on the numerical keypad,
     * in terms of least human inputs. Stores this in a global HashMap.
     */
    public static void dijkstraNumPad(){
        for (String startButton : numPadButtons.keySet()){
            for (String endButton : numPadButtons.keySet()){
                int numMoves = 0;
                ArrayList<String> moves = moveDir(numPadButtons.get(startButton), numPadButtons.get(endButton), true);
                for (int i = 0; i < moves.size(); i++){
                    if (i == 0){
                        numMoves += bestDirMovesSecond.get("A:"+moves.get(i));
                    }
                    else{
                        numMoves += bestDirMovesSecond.get(moves.get(i-1) +":"+ moves.get(i));
                    }
                }
                bestNumMoves.put(startButton +":"+ endButton, numMoves);
            }
        }
    }

    /**
     * Helper function for part 2.
     * Finds the best way to move the final robot between buttons on the numerical
     * keypad, in terms of least human inputs, as function of the best ways to move
     * the previous robot between buttons on its directional pad.
     * @param oldMap A HashMap of button-button pairs to least human inputs to move
     * between directional pad buttons.
     * @return newMap A HashMap of button-button pairs to least human inputs to move
     * between numerical pad buttons.
     */
    public static HashMap<String, Long> dijkstraNumPadPart2(HashMap<String, Long> oldMap){
        HashMap<String, Long> newMap = new HashMap<String, Long>();
        for (String startButton : numPadButtons.keySet()){
            for (String endButton : numPadButtons.keySet()){
                Long numMoves = 0L;
                ArrayList<String> moves = moveDir(numPadButtons.get(startButton), numPadButtons.get(endButton), true);
                for (int i = 0; i < moves.size(); i++){
                    if (i == 0){
                        numMoves += oldMap.get("A:"+moves.get(i));
                    }
                    else{
                        numMoves += oldMap.get(moves.get(i-1) +":"+ moves.get(i));
                    }
                }
                newMap.put(startButton +":"+ endButton, numMoves);
            }
        }
        return newMap;
    }

    /**
     * Helper function for both parts.
     * Finds the directional pad inputs required to move from the starting button
     * to the ending button
     * @param start An Integer[] [startingRow, startingColumn]
     * @param end An Integer[] [endingRow, endingColumn]
     * @param isNumPad A boolean indicating if the target pad is numerical (true) or
     * directional (false).
     * @return An arrayList of button presses on a directional pad to make the 
     * required movement.
     */
    public static ArrayList<String> moveDir(Integer[] start, Integer[] end, boolean isNumPad){
        ArrayList<String> outInputs = new ArrayList<String>();
        Integer[] difference = new Integer[] {end[0] - start[0], end[1] - start[1]};
        if(difference[1] < 0){
            try {
                outInputs = moveDirHorizontalFirst(start, end, isNumPad);
            } catch (Exception e) {
                try {
                    outInputs = moveDirVerticalFirst(start, end, isNumPad);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }else{
            try {
                outInputs = moveDirVerticalFirst(start, end, isNumPad);
            } catch (Exception e) {
                // TODO: handle exception
                try {
                    outInputs = moveDirHorizontalFirst(start, end, isNumPad);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        
        outInputs.add("A");
        return outInputs;
    }

    /**
     * Helper function for both parts.
     * Directional pad inputs to move from start to end, when it is most efficient to
     * move horizontally before vertically.
     * @param start An Integer[] [startingRow, startingColumn]
     * @param end An Integer[] [endingRow, endingColumn]
     * @param isNumPad True if the target pad is numeric, and false if it is directional.
     * @return A list of directional inputs.
     * @throws Exception
     */
    public static ArrayList<String> moveDirHorizontalFirst(Integer[] start, Integer[] end, boolean isNumPad) throws Exception{
        ArrayList<ArrayList<String>> currPad = isNumPad ? numPad : dirPad;
        ArrayList<String> outInputs = new ArrayList<String>();
        Integer[] curr = new Integer[] {start[0], start[1]};
        Integer[] difference = new Integer[] {end[0] - start[0], end[1] - start[1]};
        while((curr[1] != end[1])){
            int colDif = difference[1] < 0 ? -1 : 1;
            curr[1] += colDif;
            difference[1] -= colDif;
            outInputs.add(colDif < 0 ? "<" : ">");
        }
        if(currPad.get(curr[0]).get(curr[1]).equals("X")){
            throw new Exception();
        }
        while((curr[0] != end[0])){
            int rowDif = difference[0] < 0 ? -1 : 1;
            curr[0] += rowDif;
            difference[0] -= rowDif;
            outInputs.add(rowDif < 0 ? "^" : "v");
        }
        return outInputs;
    }

    /**
     * Helper function for both parts.
     * Directional pad inputs to move from start to end, when it is most efficient to
     * move vertically before horizontally.
     * @param start An Integer[] [startingRow, startingColumn]
     * @param end An Integer[] [endingRow, endingColumn]
     * @param isNumPad True if the target pad is numeric, and false if it is directional.
     * @return A list of directional inputs.
     * @throws Exception
     */
    public static ArrayList<String> moveDirVerticalFirst(Integer[] start, Integer[] end, boolean isNumPad) throws Exception{
        ArrayList<ArrayList<String>> currPad = isNumPad ? numPad : dirPad;
        ArrayList<String> outInputs = new ArrayList<String>();
        Integer[] curr = new Integer[] {start[0], start[1]};
        Integer[] difference = new Integer[] {end[0] - start[0], end[1] - start[1]};
        while((curr[0] != end[0])){
            int rowDif = difference[0] < 0 ? -1 : 1;
            curr[0] += rowDif;
            difference[0] -= rowDif;
            outInputs.add(rowDif < 0 ? "^" : "v");
            if(currPad.get(curr[0]).get(curr[1]).equals("X")){
                throw new Exception();
            }

        }
        
        while((curr[1] != end[1])){
            int colDif = difference[1] < 0 ? -1 : 1;
            curr[1] += colDif;
            difference[1] -= colDif;
            outInputs.add(colDif < 0 ? "<" : ">");
        }
        return outInputs;
    }

    /**
     * Helper function for part 1.
     * Finds the number of human inputs to input the given code into the numerical pad.
     * @param codes A string of digits, terminated with an A.
     * @return The number of human inputs to enter the code.
     */
    public static int enterNumCode(String codes){
        int numMoves = 0;
        String[] numbers = codes.split("");
        for (int i = 0; i < numbers.length; i++){
            if(i == 0){
                numMoves += bestNumMoves.get("A:"+numbers[i]);
            }
            else{
                numMoves += bestNumMoves.get(numbers[i-1] +":"+ numbers[i]);
            }
        }
        return numMoves;
    } 

    /**
     * Helper function for both parts.
     * Finds the integer part of the input code.
     * @param code A string of digits, terminated by an A.
     * @return The digits of the code, as an integer.
     */
    public static int getNumPart(String code){
        return Integer.parseInt(code.substring(0, code.length()-1 ));
    }

    /**
     * Solution for part 1.
     * You must enter a series of codes into a keypad, but can only control a robot
     * in front of a directional pad, which controls another robot in front of another
     * directional pad, which controls another robot in front of the numeric pad.
     * For each code, find the product of the integer portion of the code and the shortest
     * number of human inputs that cause the final robot to enter the code, then find
     * the sum of these products.
     * @return
     */
    public static int part1(){
        int complexitySum = 0;
        ArrayList<Integer> numMovesPerCode = new ArrayList<Integer>();
        for (String code : codes){
            numMovesPerCode.add(enterNumCode(code));
        }

        for(int i = 0; i < codes.size(); i++){
            int complexity = getNumPart(codes.get(i)) * numMovesPerCode.get(i);
            System.out.println(codes.get(i) +": "+ numMovesPerCode.get(i) +":"+ complexity);
            complexitySum += complexity;
        } 
        return complexitySum;
    }

    /**
     * Helper function for part 2.
     * Finds the number of directional inputs to enter the given code in the 
     * numeric pad, using the given map of movement costs.
     * @param map
     * @param codes
     * @return
     */
    public static Long enterNumCodePart2(HashMap<String, Long> map, String codes){
        Long numMoves = 0L;
        String[] numbers = codes.split("");
        for (int i = 0; i < numbers.length; i++){
            if(i == 0){
                numMoves += map.get("A:"+numbers[i]);
            }
            else{
                numMoves += map.get(numbers[i-1] +":"+ numbers[i]);
            }
        }
        return numMoves;
    } 

    /**
     * Solution for part 2.
     * Given the same codes in part 1, calculate the same complexity values, 
     * except there is now a chain of 25 robots to the numeric pad instead 
     * of 3.
     * @return The sum of the products of each code's integer value multiplied
     * by the least number of human inputs required to enter the code. 
     */
    public static Long part2(){
        Long complexitySum = 0L;
        ArrayList<Long> numMovesPerCode = new ArrayList<Long>();

        // find the number of moves to go from each num button to each other num button
        HashMap<String, Long> movementMap = findCostsPart2();

        // find the number of moves for each code sequence
        for(String code: codes){
            numMovesPerCode.add(enterNumCodePart2(movementMap, code));
        }


        for(int i = 0; i < codes.size(); i++){
            Long complexity = getNumPart(codes.get(i)) * numMovesPerCode.get(i);
            System.out.println(codes.get(i) +": "+ numMovesPerCode.get(i) +":"+ complexity);
            complexitySum += complexity;
        } 
        return complexitySum;
    }

    /**
     * Helper function for part 2.
     * Finds the best way to move between each button on the numeric pad,
     * in terms of least human inputs, along a chain of 25 robots.
     * @return
     */
    public static HashMap<String, Long> findCostsPart2(){
        HashMap<String, Long> outMap = new HashMap<String, Long>();
        for (Map.Entry<String, Integer> entry : bestDirMovesFirst.entrySet()){
            outMap.put(entry.getKey(), Long.valueOf(entry.getValue()));
        }
        for(int i = 0; i < 24; i++){
            outMap = dijkstraDirPadRepeat(outMap);
        }
        outMap = dijkstraNumPadPart2(outMap);
        return outMap;
    }    


}
