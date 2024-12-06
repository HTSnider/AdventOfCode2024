import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Program for day 5 of Advent of Code 2024
 */
public class Day5 {
    /**
     * Main method which intakes and parses input file,
     *  which consists of N lines of pairs of '|' separated integers,
     *  followed by a blank line, then M lines of ',' separated integers.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input05.txt");
        Scanner in = new Scanner(inFile);
        Pattern rulePattern = Pattern.compile("(\\d+)\\|(\\d+)");
        ArrayList<ArrayList<String>> rules = new ArrayList<ArrayList<String>>();
        //HashMap<String, String> rules = new HashMap<String,String>();
        ArrayList<String> updates = new ArrayList<String>();
        while(in.hasNextLine()){
            String temp = in.nextLine();
            if(temp.equals("")){
                break;
            }

            Matcher matcher = rulePattern.matcher(temp);
            matcher.find();
            ArrayList<String> currRule = new ArrayList<String>();
            currRule.add(matcher.group(1));
            currRule.add(matcher.group(2));
            rules.add(currRule);
            //System.out.println(matcher.group(1)+"|"+matcher.group(2));
        }
        while(in.hasNextLine()){
            updates.add(in.nextLine());
        }
        in.close();

        //System.out.println(part1(rules, updates));
        System.out.println(part2(rules,updates));
    }

    /**
     * Solution to part 1.
     * Determines if an update (the comma separated integers from input), follows the rules,
     * where a rule says X|Y meaning that page X must precede page Y if both are in the update,
     * then finds the sum of the middle integers of the updates which follow the rules.
     * @param rules An ArrayList of ArrayLists of Strings, each sublist consisting of first the X, then the Y, of a rule.
     * @param updates An ArrayList of Strings, each being the comma separated integers of a single update.
     * @return correctUpdateMiddleSum An integer sum of the middle integer of correct updates.
     */
    static int part1(ArrayList<ArrayList<String>> rules, ArrayList<String> updates){
        int correctUpdateMiddleSum = 0;
        for(int i=0; i<updates.size(); i++){
            Boolean orderedFlag = true;
            for (int j=0; j<rules.size(); j++){
                //System.out.println(e);
                if(orderedFlag){
                    String k = rules.get(j).get(0);
                    String v = rules.get(j).get(1);
                    String rulePatt = "[,\\^]"+v+",(\\d+,)*"+k+",?";
                    if(Pattern.compile(rulePatt).matcher(updates.get(i)).find()){
                        orderedFlag = false;
                        System.out.println(updates.get(i)+" NO "+k+":"+v);
                    }
                }
            }
            if(orderedFlag){
                String[] correctUpdate = updates.get(i).split(",");
                int middleIx = (int) Math.ceil(correctUpdate.length/2);
                correctUpdateMiddleSum += Integer.parseInt(correctUpdate[middleIx]);
                //System.out.println(updates.get(i)+" YES "+middleIx+" "+correctUpdate[middleIx]);
            }
        }

        return correctUpdateMiddleSum;
    }

    /**
     * Solution for part 2.
     * Find the updates which are in an incorrect order, correct them, and find the sum of the middle digit of those updates
     * @param rules An ArrayList of ArrayLists of Strings, each sublist consisting of first the X, then the Y, of a rule.
     * @param updates An ArrayList of Strings, each being the comma separated integers of a single update.
     * @return fixedMiddleSum An integer sum of the middle digits of incorrectly ordered updates after they are put in order
     */
    static int part2(ArrayList<ArrayList<String>> rules, ArrayList<String> updates){
        int fixedMiddleSum = 0;
        // convert rules into sets of numbers that must be before a given number
        HashMap<String, ArrayList<String>> rulesMap = new HashMap<String, ArrayList<String>>();
        for(int i=0; i<rules.size(); i++){
            if(!rulesMap.containsKey(rules.get(i).get(1))){
                ArrayList<String> curr = new ArrayList<String>();
                curr.add(rules.get(i).get(0));
                rulesMap.put(rules.get(i).get(1), curr);
            }else{

                rulesMap.get(rules.get(i).get(1)).add(rules.get(i).get(0));
            }
        }

        // loop through updates, evaluating each of them
        for (int i=0; i<updates.size(); i++){

            if (! checkOrdered(rules, updates.get(i))){      
                ArrayList<Integer[]> updatePages = new ArrayList<Integer[]>();
                String[] currUpdate = updates.get(i).split(",");
                /// put update in order
                // for each page, check how many of the other pages must be before it to follow the rules
                for (int j=0; j<currUpdate.length; j++){
                    Integer preceding = 0;
                    for(int k=0; k<currUpdate.length;  k++){
                        if (rulesMap.get(currUpdate[j]).contains(currUpdate[k])){
                            preceding += 1;
                        }
                    }
                    updatePages.add(new Integer[]{Integer.parseInt(currUpdate[j]), preceding});
                }
                
                //  then, sort by that number
                updatePages.sort((o1, o2) -> o1[1].compareTo(o2[1]));
                // finally, add middle number to total
                fixedMiddleSum += getMiddleNum(updatePages);
            }
        }

        return fixedMiddleSum;
    }

    /**
     * Helper function to determine if an update is in its proper order.
     * @param rules An ArrayList of ArrayLists of Strings, each sublist consisting of first the X, then the Y, of a rule.
     * @param update A String of comma separated integers
     * @return true if the update follows proper ordering, or false if not
     */
    static boolean checkOrdered(ArrayList<ArrayList<String>> rules, String update){
        for (int j=0; j<rules.size(); j++){
            String k = rules.get(j).get(0);
            String v = rules.get(j).get(1);
            String rulePatt = "[,\\^]"+v+",(\\d+,)*"+k+",?";
            if(Pattern.compile(rulePatt).matcher(update).find()){
               return false;
            }
        }   
        return true;
    }

    /**
     * A helper function to return the middle integer of an update.
     * @param update An ArrayList of arrays of integers. Each array consists of the //
     *      [the update page number, and the number of pages in the update that must be before this page]
     * @return the middle integer of the update.
     */
    static int getMiddleNum(ArrayList<Integer[]> update){
        int middleIx = (int) Math.ceil(update.size()/2);
        return update.get(middleIx)[0];
    }   

}
