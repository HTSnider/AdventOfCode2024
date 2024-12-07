# AdventOfCode2024
My solutions for Advent of Code 2024 (https://adventofcode.com/), this year written in Java for practice.
Input files are not included per contest rules.

# Day 5: 
The first notable day. I completed the first part using regex to evaluate the following of the ordering rules, which turned out to be completely incompatible with part 2. 
My part 2 is follows the solution of another participant, where the rules of ordering are expressed as which pages must precede each page, and this is used to calculate the minimum index the page must go in the correct ordering, which is then used to sort the update.

# Day 6:
Another notable day. The solution to both parts was solved with brute force, with a clever trick in part 2 of hashing the position and direction of points the guard turns at, so a position could be checked faster than putting it in a set of visited coordinates.

# Day 7:
First day to use recursion this year. The maximum depth is quite low, so the only optimization was using || so only paths until the first correct path are evaluated. Another optimization could have been to cut off the recursion if the running total excceeds the target value, since all of the operations are monotomically increasing.
