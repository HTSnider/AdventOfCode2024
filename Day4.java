import java.io.*;
import java.util.*;

/**
 * Program for Day 4 of Advent of Code 2024.
 */
public class Day4{

    /**
     * Main method which intakes input file,
     *  which consists of N lines of M characters,
     *  and converts it into a NxM ArrayList of Characters.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
        File inFile = new File("src\\input04.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<ArrayList<Character>> words = new ArrayList<ArrayList<Character>>();
        int i = 0;
        while (in.hasNextLine()){
            String temp = in.nextLine();
            words.add(new ArrayList<Character>());
            for(int j=0; j<temp.length(); j++){
                words.get(i).add(temp.charAt(j));
                //System.out.println(temp.charAt(j));
            }
            i++;
        }
        in.close();
        //System.out.println(words.get(0));
        System.out.println(part1(words));
        System.out.println(part2(words));

    }

    /**
     * Solution for part 1.
     * Finds the number of times the sequence "xmas" occurs in any direction.
     * Uses try-catch blocks for each direction to not have to work around edges.
     * @param words An ArrayList of ArrayLists of Characters.
     * @return xmasCount The number of times "xmas" appears.
     */
    static int part1(ArrayList<ArrayList<Character>> words){
        int xmasCount = 0;
        int yCoord = words.size();
        int xCoord = words.get(0).size();
        for (int j=0; j<yCoord; j++){
            for (int i=0; i<xCoord; i++){
                //forward
                try {
                    if(words.get(j).get(i)=='X' && words.get(j).get(i+1)=='M'
                        && words.get(j).get(i+2)=='A' && words.get(j).get(i+3)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }
                
                //reverse
                try {
                    if(words.get(j).get(i)=='X' && words.get(j).get(i-1)=='M'
                        && words.get(j).get(i-2)=='A' && words.get(j).get(i-3)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }

                // up
                try {
                    if(words.get(j).get(i)=='X' && words.get(j-1).get(i)=='M'
                        && words.get(j-2).get(i)=='A' && words.get(j-3).get(i)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }

                // down
                try {
                    if(words.get(j).get(i)=='X' && words.get(j+1).get(i)=='M'
                        && words.get(j+2).get(i)=='A' && words.get(j+3).get(i)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }

                // diagonal forward up
                try {
                    if(words.get(j).get(i)=='X' && words.get(j-1).get(i+1)=='M'
                        && words.get(j-2).get(i+2)=='A' && words.get(j-3).get(i+3)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }

                // diagonal forward down
                try {
                    if(words.get(j).get(i)=='X' && words.get(j+1).get(i+1)=='M'
                        && words.get(j+2).get(i+2)=='A' && words.get(j+3).get(i+3)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }

                // diagonal reverse down
                try {
                    if(words.get(j).get(i)=='X' && words.get(j+1).get(i-1)=='M'
                        && words.get(j+2).get(i-2)=='A' && words.get(j+3).get(i-3)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }

                // diagonal reverse up
                try {
                    if(words.get(j).get(i)=='X' && words.get(j-1).get(i-1)=='M'
                        && words.get(j-2).get(i-2)=='A' && words.get(j-3).get(i-3)=='S'){
                            xmasCount+=1;
                    }
                } catch (Exception e) {
                    
                }
            }
        }

        return xmasCount;
    }

    /**
     * Solution for part 2.
     * Finds the number of times the sequence M.S
     *                                        .A.
     *                                        M.S
     *  appears in any direction. To prevent duplicate counting,
     *  only the 4 possible orientations of the Ms are checked.
     *  Uses try-catch blocks to avoid working around edges.
     * @param words
     * @return
     */
    static int part2(ArrayList<ArrayList<Character>> words){
        int masCount = 0;
        int yCoord = words.size();
        int xCoord = words.get(0).size();
        for (int j=0; j<yCoord; j++){
            for (int i=0; i<xCoord; i++){
                // M left
                try {
                    if(words.get(j).get(i)=='M' && words.get(j+1).get(i+1)=='A' && words.get(j+2).get(i+2)=='S'
                    && words.get(j+2).get(i)=='M' && words.get(j).get(i+2)=='S'){
                        masCount+=1;
                    }
                    
                } catch (Exception e) {
                    
                }

                // M right
                try {
                    if(words.get(j).get(i)=='S' && words.get(j+1).get(i+1)=='A' && words.get(j+2).get(i+2)=='M'
                    && words.get(j+2).get(i)=='S' && words.get(j).get(i+2)=='M'){
                        masCount+=1;
                    }
                    
                } catch (Exception e) {
                    
                }

                // M up
                try {
                    if(words.get(j).get(i)=='M' && words.get(j+1).get(i+1)=='A' && words.get(j+2).get(i+2)=='S'
                    && words.get(j+2).get(i)=='S' && words.get(j).get(i+2)=='M'){
                        masCount+=1;
                    }
                    
                } catch (Exception e) {
                    
                }

                // M down
                try {
                    if(words.get(j).get(i)=='S' && words.get(j+1).get(i+1)=='A' && words.get(j+2).get(i+2)=='M'
                    && words.get(j+2).get(i)=='M' && words.get(j).get(i+2)=='S'){
                        masCount+=1;
                    }
                    
                } catch (Exception e) {
                    
                }
            }
        }
        return masCount;
    }
}