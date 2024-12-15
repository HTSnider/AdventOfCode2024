import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Program for Day 14 of Advent of Code.
 */
public class Day14 {

    /**
     * Main method which intakes and parses the input file, which consists of
     * N lines, each detailing a robot's initial position, and their constant
     * velocity vector.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        File inFile = new File("src\\input14.txt");
        Scanner in = new Scanner(inFile);
        ArrayList<ArrayList<Integer[]>> robots = new ArrayList<ArrayList<Integer[]>>();
        Pattern p = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");
        while(in.hasNextLine()){
            String temp = in.nextLine();
            Matcher matcher = p.matcher(temp);
            matcher.find();
            Integer[] pos = new Integer[] {Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))};
            Integer[] vel = new Integer[] {Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4))};
            ArrayList<Integer[]> curr = new ArrayList<Integer[]>();
            curr.add(pos);
            curr.add(vel);
            robots.add(curr);
        }
        in.close();
        
        System.out.println(part1(robots));
        /* ArrayList<Integer[]> finalPos = new ArrayList<Integer[]>();
        stepN(deepCopy(robots), 100).forEach((ArrayList<Integer[]> robot)-> finalPos.add(robot.get(0)));
        System.out.println(getSafety(finalPos)); */

        System.out.println(part2SafetyScore(robots));

    }

    /**
     * Solution to part 1.
     * Given that each robot moves 1 velocity vector every second, with
     * screen wraparound, and the screen is 101 by 103, find the positions
     * of the robots after 100 seconds. Then, find the number of robots
     * in each quadrant, and multiply them together to find the security
     * score.
     * @param robots An ArrayList of ArrayList of Integer[], where each
     * ArrayList contains [xPosition, yPosition], [xVelocity, yVelocity]
     * @return The safety score of the robots after 100 seconds.
     */
    public static Long part1(ArrayList<ArrayList<Integer[]>> robots){
        ArrayList<Integer[]> finalPositions = new ArrayList<Integer[]>();

        for (ArrayList<Integer[]> robot : robots){
            finalPositions.add(moveBot100(robot));
        }
        return getSafety(finalPositions);
    }

    /**
     * Helper function for part 1.
     * Finds the position of a robot after 100 seconds, using 
     * modular arithmetic.
     * @param robot An ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return [xPositionFinal, yPositionFinal]
     */
    public static Integer[] moveBot100(ArrayList<Integer[]> robot){
        Integer numMoves = 100;
        Integer width = 101;
        Integer height = 103;
        Integer x = robot.get(0)[0];
        Integer y = robot.get(0)[1];
        Integer xVel = robot.get(1)[0];
        Integer yVel = robot.get(1)[1];
        x += numMoves*xVel;
        y += numMoves*yVel;
        while(x < 0){
            x += width;
        }
        while (x >= width){
            x -= width;
        }
        while(y < 0){
            y += height;
        }
        while(y >= height){
            y -= height;
        }
        return new Integer[] {x, y};
    }

    /**
     * Helper function for both parts.
     * Finds the safety score given the postions of the robots.
     * @param robotPositions An ArrayList of Integer[], each containing
     * [xPosition, yPosition].
     * @return The safety score of the robots.
     */
    public static Long getSafety(ArrayList<Integer[]> robotPositions){
        Integer xMiddle = 50;
        Integer yMiddle = 51;
        Integer[] quadrants = {0,0,0,0};
        for (Integer[] robot : robotPositions){
            if( robot[0] < xMiddle && robot[1] < yMiddle){
                quadrants[0]++;
            }else if(robot[0] < xMiddle && robot[1] > yMiddle){
                quadrants[1]++;
            }else if(robot[0] > xMiddle && robot[1] < yMiddle){
                quadrants[2]++;
            }else if(robot[0] > xMiddle && robot[1] > yMiddle){
                quadrants[3]++;
            }
        }
        return Long.valueOf(quadrants[0]) * Long.valueOf(quadrants[1]) * Long.valueOf(quadrants[2]) * Long.valueOf(quadrants[3]);
    }

    /**
     * Failed attempt at part 2.
     * Tries to find the second that the robots form a christmas tree
     * by finding the maximum time it takes for robots to all return 
     * to their starting positions, then finding the minimum mean 
     * horizontal distance from the center.
     * @param robots An ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return The time at which the mean horizontal distance from the 
     * center is minimized.
     */
    public static Integer part2(ArrayList<ArrayList<Integer[]>> robots){
        // find cycle lengths
        Long cycleLength = getTrueCycle(robots);


        // simulate each movement
        Integer time = findLowestMeanXDiff(deepCopy(robots), cycleLength);

        // print out position graph at time of minimum average distance
        printRobots(robots, time);
        return time;
    }

    /**
     * Solution for part 2.
     * Finds the time at which the robots form a christmas tree by
     * finding the time at which the safety score is minimized.
     * @param robots An ArrayList of ArrayLists of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return The time at which the safety score of the robots is
     * minimized.
     */
    public static Integer part2SafetyScore(ArrayList<ArrayList<Integer[]>> robots){
        Long minSafety = Long.MAX_VALUE;
        Integer minSafetyTime = 0;
        ArrayList<ArrayList<Integer[]>> tempRobots = deepCopy(robots);
        for (int i = 0; i < 10000; i++){
            tempRobots = stepN(tempRobots,1);
            Long currSafety = getSafety(positionsWrapper(tempRobots));
            if (currSafety < minSafety){
                minSafety = currSafety;
                minSafetyTime = i+1;
            }
        }
        printRobots(robots, minSafetyTime);
        return minSafetyTime;
    }

    /**
     * Helper function for part 2.
     * A genericized version of moveBot100, for any integer N.
     * @param robot An ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @param N The time to move the robot.
     * @return [xPositionFinal, yPositionFinal]
     */
    public static Integer[] moveBotN(ArrayList<Integer[]> robot, Integer N){
        Long numMoves = Long.valueOf(N);
        Integer width = 101;
        Integer height = 103;
        Long x = Long.valueOf(robot.get(0)[0]);
        Long y = Long.valueOf(robot.get(0)[1]);
        Integer xVel = robot.get(1)[0];
        Integer yVel = robot.get(1)[1];
        x += numMoves*xVel;
        y += numMoves*yVel;
        while(x < 0){
            x += width;
        }
        while (x >= width){
            x -= width;
        }
        while(y < 0){
            y += height;
        }
        while(y >= height){
            y -= height;
        }
        return new Integer[] {Math.toIntExact(x), Math.toIntExact(y)};
    }

    /**
     * Defunct Helper function.
     * Finds the first time the robot returns to its starting position.
     * @param robot An ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return The time the robot reaches its starting position again.
     */
    public static int getCycle(ArrayList<Integer[]> robot){
        HashSet<String> positions = new HashSet<String>();
        Integer[] pos = robot.get(0);
        Integer[] vel = robot.get(1);
        ArrayList<Integer[]> rob = new ArrayList<Integer[]>();
        String hashString = String.valueOf(pos[0]) +":"+ String.valueOf(pos[1]);
        int i=0;
        while(!positions.contains(hashString)){
            positions.add(hashString);
            rob.clear();
            rob.add(pos);
            rob.add(vel);
            pos = moveBotN(rob, 1);
            hashString = String.valueOf(pos[0]) +":"+ String.valueOf(pos[1]);
            i++;
        }
        return i+1;
    }

    /**
     * Defunct helper function for part 2.
     * Finds the lowest common multiple of the lengths of each
     * cycle, at which point all the robots are returned to their
     * starting positions.
     * @param cycleLengths An ArrayList of Integers, each the time
     * it takes for a robot to return to its starting position
     * @return The lowest common multiple
     */
    public static Long LCM(ArrayList<Integer> cycleLengths){
        Long lcm = 1L;
        int divisor = 2;

        while(true){
            int counter = 0;
            boolean divisible = false;
            for(int robNum = 0; robNum < cycleLengths.size(); robNum++){
                
                if (cycleLengths.get(robNum) == 1){
                    counter++;
                }  
                if (cycleLengths.get(robNum) % divisor == 0){
                    cycleLengths.set(robNum, (Integer) cycleLengths.get(robNum)/divisor);
                    divisible = true;
                }

            }
            if (divisible){
                lcm *= divisor;
            }else{
                divisor++;
            }
            if (counter == cycleLengths.size()){
                return lcm;
            }
        }
    }

    /**
     * Defunct helper function.
     * Finds the time it takes for each robot to return to
     * its starting position, and their LCM.
     * @param robots An ArrayList of ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return The time it takes for all robots to return to their starting positions.
     */
    public static Long getTrueCycle(ArrayList<ArrayList<Integer[]>> robots){
        ArrayList<Integer> cycles = new ArrayList<Integer>();
        for (ArrayList<Integer[]> robot : robots){
            cycles.add(getCycle(robot));
        }
        Long trueCycleLength = LCM(cycles);
        return trueCycleLength;
    }

    /**
     * Helper function for part 2.
     * Advances all robots by N seconds.
     * @param robots An ArrayList of ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @param N The time to advance all robots by.
     * @return movedBots An ArrayList of ArrayList of Integer[], [xPositionFinal, yPositionFinal],
     * [xVelocity, yVelocity]
     */
    public static ArrayList<ArrayList<Integer[]>> stepN(ArrayList<ArrayList<Integer[]>> robots, Integer N){
        ArrayList<ArrayList<Integer[]>> movedBots = new ArrayList<ArrayList<Integer[]>>();
        for (ArrayList<Integer[]> robot : robots){
            ArrayList<Integer[]> curr = new ArrayList<Integer[]>();
            curr.add(moveBotN(robot, N));
            curr.add(robot.get(1));
            movedBots.add(curr);
        }
        
        return movedBots;
    }

    /**
     * Defunct helper function for part 2.
     * Finds the mean horizontal difference from the center of the robots.
     * @param robots An ArrayList of ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return mean The mean horizontal difference of the robots from the center
     */
    public static Double getMeanDiff(ArrayList<ArrayList<Integer[]>> robots){
        Double numRobots = Double.valueOf(robots.size());
        int xMiddle = 50;
        Double mean = 0.0;

        for (ArrayList<Integer[]> robot : robots){
            mean += Math.pow(Math.abs(xMiddle - robot.get(0)[0]),2);
        }
        
        mean /= numRobots;
        mean = Math.sqrt(mean);
        return mean;
    }

    /**
     * Defunct helper function for part 2.
     * Finds the minimum mean horizontal difference of the robots
     * over the length of the cycles.
     * @param robots
     * @param cycleLength
     * @return
     */
    public static Integer findLowestMeanXDiff(ArrayList<ArrayList<Integer[]>> robots, Long cycleLength){
        Double lowestMean = Double.MAX_VALUE;
        Integer lowestTime = Integer.MAX_VALUE;

        for ( Integer i = 0; i< cycleLength; i++){
            robots = stepN(robots,1);
            Double mean = getMeanDiff(robots);
            if (mean< lowestMean){
                lowestMean = mean;
                lowestTime = i;
            }
        }
        return lowestTime;
    }

    /**
     * Helper function for part 2.
     * Prints a grid of the locations of the robots after
     * a given number of seconds
     * @param robots An ArrayList of ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @param time The time at which to print the robots.
     */
    public static void printRobots(ArrayList<ArrayList<Integer[]>> robots, Integer time){
         robots = stepN(robots, time);
         Boolean[][] graph = new Boolean[103][101];
         for (int i = 0; i< graph.length; i++){
            for (int j = 0; j < graph[0].length; j++){
                graph[i][j] = false;
            }
         }
         for (ArrayList<Integer[]> robot : robots){
            graph[robot.get(0)[1]][robot.get(0)[0]] = true;
         }
         for(int i = 0; i < graph.length; i++){
            for (int j = 0; j < graph[0].length; j++){
                System.out.print(graph[i][j] ? "0" : ".");
            }
            System.out.print("\n");
         }
    }

    /**
     * Helper function for part 2.
     * Creates a deep copy of the robots ArrayList.
     * @param robots An ArrayList of ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return copy An ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     */
    public static ArrayList<ArrayList<Integer[]>> deepCopy(ArrayList<ArrayList<Integer[]>> robots){
        ArrayList<ArrayList<Integer[]>> copy = new ArrayList<ArrayList<Integer[]>>();
        for(int i = 0; i < robots.size(); i++){
            ArrayList<Integer[]> currRobot = new ArrayList<Integer[]>();
            currRobot.add(new Integer[] {robots.get(i).get(0)[0], robots.get(i).get(0)[1]} );
            currRobot.add(new Integer[] {robots.get(i).get(1)[0], robots.get(i).get(1)[1]} );
            copy.add(currRobot);
        }
        return copy;
    }

    /**
     * Helper function for part 2.
     * Retrieves the positions of the robots, for use with getSafety()
     * @param robots An ArrayList of ArrayList of Integer[], [xPosition, yPosition],
     * [xVelocity, yVelocity]
     * @return An ArrayList of Integer[], [xPosition, yPosition]
     */
    public static ArrayList<Integer[]> positionsWrapper(ArrayList<ArrayList<Integer[]>> robots){
        ArrayList<Integer[]> out = new ArrayList<Integer[]>();
        for (ArrayList<Integer[]> robot : robots){
            out.add(robot.get(0));
        }
        return out;
    }

}
