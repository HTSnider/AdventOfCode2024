import java.io.*;
import java.util.*;

/*
 * Program for day 9 of Advent of Code 2024
 */
public class Day9 {
    
    /**
     * Main method that intakes the input file,
     * which consists of a single long line of digits.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input09.txt");
        Scanner in = new Scanner(inFile);
        String input = in.nextLine();
        in.close();
        System.out.println(part1(input));
        System.out.println(part2(input));
    }

    /**
     * Solution for part 1.
     * Converts the input digit string into a series of file blocks, where
     * each digit indicates the size of a file in blocks, followed by a digit indicating the number of empty blocks,
     * alternating until all input digits are processed. Each block containing a file is labeled with a file ID,
     * starting at 0, and increasing by 1s. 
     * Blocks are then moved, from the rightmost block to the leftmost empty block.
     * Finally, checksums for each block are calculated by multiplying the index of the block and the file ID.
     * The sum of these checksums is the output.
     * @param input A string of digits, mapping the files on the disk.
     * @return checkSum The sum of the checksums of each block after file fragmentation.
     */
    public static Long part1(String input){
        ArrayList<Integer> blocks = new ArrayList<Integer>();
        int fileID = 0;
        Boolean fileFlag = true;
        for(String s : input.split("")){
            int multiple = Integer.parseInt(s);
            for (int i=0; i<multiple; i++){
                blocks.add( fileFlag ? fileID :-1);
            }
            fileID += fileFlag ? 1 : 0;
            fileFlag = !fileFlag;
        }
        blocks = condense(blocks);
        
        Long checkSum = getCheckSum(blocks);


        return checkSum;
    }

    /**
     * Helper function for part 1.
     * Moves file blocks from the right to the leftmost empty block.
     * @param blocks An ArrayList of Integers, each representing the fileID of a block file, or
     * -1 for an empty block.
     * @return blocks The input ArrayList, modified in place.
     */
    public static ArrayList<Integer> condense(ArrayList<Integer> blocks){
        for (int i=0; i<blocks.size(); i++){
            if (blocks.get(i)==-1){
                int last = blocks.remove(blocks.size()-1);
                while(last == -1){
                    last = blocks.remove(blocks.size()-1);
                }
                blocks.set(i, last);
            }
        }

        return blocks;
    }

    /**
     * Helper function for part 1.
     * Finds the sum of the checksums of the blocks, each calculated as
     * the block index * the fileID.
     * @param blocks An ArrayList of Integers, each representing the FileID of a fileblock,
     * or -1 for an empty block.
     * @return checkSum The sum of the checksum values of each file block.
     */
    public static Long getCheckSum(ArrayList<Integer> blocks){
        Long checkSum = 0L;
        for(int ix = 0; ix < blocks.size(); ix++){
            checkSum += Integer.toUnsignedLong(ix*blocks.get(ix));
        }

        return checkSum;

    }

    /**
     * Solution to part 2.
     * In this part, each file can only be moved as a whole. Each file may only attempt
     * to move once, and files are moved in reverse order of fileID. A file is moved
     * to the leftmost space that can fit the entire file, if one exists.
     * @param input A string of digits, mapping the files on the disk.
     * @return checkSum, the sum of the checkSums of the file blocks after moving them.
     */
    public static Long part2(String input){
        ArrayList<int[]> blocks = parsePart2(input);
        blocks = condensePart2(blocks);
        return getCheckSumPart2(blocks);

    }

    /**
     * Helper function for part 2.
     * Converts the input digit string into an ArrayList of int[], each
     * having three values [The number of spaces to the left of the file,
     * the fileID, and the size of the file in blocks].
     * @param input A string of digits, mapping the files on the disk.
     * @return blocks An ArrayList of int[], [spacesToLeft, fileID, fileSize]
     */
    public static ArrayList<int[]> parsePart2(String input){
        int fileID = 0;
        ArrayList<int[]> blocks = new ArrayList<int[]>();
        // left is free space to the left, middle is fileID, right is fileSize
        String[] diskMap = input.split("");
        blocks.add(new int[] {0, fileID, Integer.valueOf(diskMap[0])});
        fileID++;
        for (int i = 1; i<diskMap.length; i+=2){
            int[] curr = new int[3];
            if(i+1==diskMap.length){ // case ending with space
                curr[0] = Integer.valueOf(diskMap[i]);
                curr[1] = 0;
                curr[2] = 0;
            }else{
                curr[0] = Integer.valueOf(diskMap[i]);
                curr[1] = fileID;
                curr[2] = Integer.valueOf(diskMap[i+1]);
                fileID++;
            }
            blocks.add(curr);
        }
        return blocks;
    }

    /**
     * A helper function for part 2.
     * Attempts to move each file in order of reverse fileID, to the leftmost 
     * free space big enough to contain it.
     * @param blocks An ArrayList of int[], [spacesToLeft, fileID, fileSize]
     * @return blocks The input file, modified in place.
     */
    public static ArrayList<int[]> condensePart2(ArrayList<int[]> blocks){
        HashSet<Integer> triedToMove = new HashSet<Integer>();
        int rightFile = blocks.size()-1;
        while(rightFile>0){
            // check if file has been moved before, and skip if so
            if( triedToMove.contains(blocks.get(rightFile)[1])){
                rightFile--;
                continue;
            }
            triedToMove.add(blocks.get(rightFile)[1]);
            // check from left to right if theres enough free space to move the file
            // and if so, add it in that spot, subtract the amount of space left, 
            // and add the new space to the file to the right of old position, if one exists
            int leftFile=1;
            while (leftFile<=rightFile){
                if(blocks.get(leftFile)[0] >= blocks.get(rightFile)[2]){
                    // case: file has enough space to left, and no preceding file has enough space
                    if(leftFile==rightFile){
                        blocks.get(rightFile)[0] = 0;
                        if(rightFile!=blocks.size()-1){
                            blocks.get(rightFile+1)[0] += blocks.get(rightFile)[0] + blocks.get(rightFile)[2];
                        }
                    }else{ // case: a file to the left has enough free space
                        if(rightFile<blocks.size()-1){
                            blocks.get(rightFile+1)[0]+= blocks.get(rightFile)[0] + blocks.get(rightFile)[2];
                        }
                        blocks.get(leftFile)[0] -= blocks.get(rightFile)[2];
                        blocks.get(rightFile)[0] = 0;
                        blocks.add(leftFile, blocks.remove(rightFile));
                    }
                    break;
                }
                leftFile++;
            }
        }
        return blocks;
    }

    /**
     * Helper function for part 2.
     * Finds the sum of the checksums of each file block, using the format 
     * for part 2.
     * @param blocks An ArrayList of int[], [spacesToLeft, fileID, fileSize]
     * @return checkSum, the sum of the checksums of all of the file blocks.
     */
    public static Long getCheckSumPart2(ArrayList<int[]> blocks){
        int currIndex = 0;
        Long checkSum = 0L;
        for(int[] file : blocks){
            currIndex += file[0];
            for (int j = currIndex; j<currIndex+file[2]; j++){
                checkSum += Long.valueOf(file[1]*j);
            }
            currIndex += file[2]; 
        }
        return checkSum;
    }
}
