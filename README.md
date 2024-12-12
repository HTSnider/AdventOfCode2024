# AdventOfCode2024
My solutions for Advent of Code 2024 (https://adventofcode.com/), this year written in Java for practice.
Input files are not included per contest rules.

## Day 5: 
The first notable day. I completed the first part using regex to evaluate the following of the ordering rules, which turned out to be completely incompatible with part 2. 
My part 2 is follows the solution of another participant, where the rules of ordering are expressed as which pages must precede each page, and this is used to calculate the minimum index the page must go in the correct ordering, which is then used to sort the update.

## Day 6:
Another notable day. The solution to both parts was solved with brute force, with a clever trick in part 2 of hashing the position and direction of points the guard turns at, so a position could be checked faster than putting it in a set of visited coordinates.

## Day 7:
First day to use recursion this year. The maximum depth is quite low, so the only optimization was using || so only paths until the first correct path are evaluated. Another optimization could have been to cut off the recursion if the running total excceeds the target value, since all of the operations are monotomically increasing.

## Day 8:
This day was difficult, but only because I tricked myself into thinking I needed to reduce the distance vector between each pair of antenni by the greatest common denominator, to find antinodes in spaces that exactly intersect the line between the antenni. One little problem I just added a check for was the duplication of antenna coordinates, since it shouldn't be possible to happen, and yet it does.

## Day 9:
Another tricky day, manipulating large arrays of objects in loops. Switching from for to while loops made iterating through the files and blocks less complex, and a HashSet made determining which files I had already attempted to move much quicker.

## Day 10: 
A bit tricky to grasp, but not difficult to implement. Because part 2 was just part 1 without a check, I was able to optimize it a bit. A further optimization I could make is replacing the ArrayList I used to store steps of each path with a Queue, for faster list operations.

## Day 11:
I really struggled with this day, mostly part 2. My initial approach of keeping an ArrayDeque<Long> of the stones worked fine for 25 steps, but took up too much memory at 75 steps. Implementing the new process, keeping stones in a dictionary (implemented as a HashMap), with each stone markings being the key, and number of stones with that marking being the value, introduced logic errors, and I had to run someone else's solution in a separate project and manually compare the values in the dicts at each step to find and fix the problem.
