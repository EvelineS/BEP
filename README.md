# BEP

This is the code belonging to my Bachelor thesis as completion of my Bachelor Applied Mathematics at the TU Delft.

The binets should be the input in the file 'binets.txt'
Each binet should be represented with two nodes and a connection 0 or 1. 0 indicates a tree binet, 1 indicates a hybrid binet with the second node the result of the hybridization.
When the programme outputs a network, given the binet set, it is shown in a newick string. The programme 'Dendroscope' can be used to convert this string to a visual representation of the network.

There are three ways to use this programme:

1. Use the original algorithm to find a network given a set of binets; do not remove binets.

2. Use the algorithm to find a network given a set of binets.

  The alternative for removal of binets can be defined in variable 'currentAlternativeNo' in the file 'algorithm.java'. This number should be 1,2,3 or 4.
  The difference between the alternatives is in the removal if a cycle is found:
  
   Alternative one: Remove a random binet
  
   Alternative two: Remove a random binet from a cycle
  
   Alternative three: Remove a random binet from the shortest cycle
  
   Alternative four: Remove a random binet which is present in the most cycles
  
3. Use the algorithm to compare four alternatives for the removal of binets.
  
  The amount of times the programme iterates can be defined in variable 'numberOfIterations' in the file 'algorithm.java'. This number should be larger than 0.
