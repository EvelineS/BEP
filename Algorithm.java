package main;

import java.io.*;
import java.util.*;

public class Algorithm {
	public static Binet[] binets = new Binet[1];
	public static Binet[] networkBinets = new Binet[1];
	static char[] nodes;
	static Arc[] arcs;
	static char[][] UComponents;
	static SuperNetwork superNetwork;
	static String newick;
	static int errorNo = 0;
	static int[] totalError = new int[4];
	static double[] errorArray = new double[4];
	static int currentAlternativeNo = 0;
	static int changeForRemoval = 10;
	static int changeForAlteration = 10;
	static boolean goToNextIteration = false;
	
	public static void main(String[] args) throws FileNotFoundException{
		initialize();
		
		//Option 1: find network for binet set; lines 163 and 170 must be uncommented, lines 164 and 171 must be commented
//		currentAlternativeNo = 1;
//		runAlgorithm(binets);
//		System.out.println(newick);
		
		//Option2: compare the four alternatives for removal; lines 163 and 170 should be commented, lines 164 and 171 must be uncommented
		int numberOfIterations = 100;
		
		for (int iterationNo = 0; iterationNo<numberOfIterations; iterationNo++) {
			System.out.println(iterationNo);
			Binet[] binetArray = binets;
			Binet[] alteredBinets = alterBinets(binets);
			binets = alteredBinets;
			for (int alternativeNo = 0; alternativeNo<4; alternativeNo++) {
				currentAlternativeNo = alternativeNo+1;
				runAlgorithm(binets);
				totalError[alternativeNo] = totalError[alternativeNo] + (binetArray.length-binets.length);
				resetValues(alteredBinets);
				goToNextIteration = false;
			}
			resetValues(binetArray);
		}
		for (int alternativeNo = 0; alternativeNo<4; alternativeNo++) {
			errorArray[alternativeNo]=averagePerIteration(totalError[alternativeNo],numberOfIterations);
			System.out.print("Total amount of errors for alternative ");
			System.out.print(alternativeNo+1);
			System.out.println(" is: " + errorArray[alternativeNo]);
		}
	}
	
	public static void initialize() throws FileNotFoundException{
		Scanner scanner = new Scanner(new File("src/main/Binets.txt"));
		while(scanner.hasNextLine()) {
		    final String line = scanner.nextLine();
		    processLine(line);
		}
		scanner.close();
	}

	public static void processLine(String line) {
		StringTokenizer stringTokenizer = new StringTokenizer(line);
		if(stringTokenizer.countTokens()!=3) {
			System.out.println("The input is incorrect.");
			System.out.println("Each binet should be entered on a new line.");
			System.out.println("First the two nodes and then the direction should be entered, seperated with a space.");
			System.exit(0);
		}
		int direction = -1;
		char[][] nodes = new char[2][];
		int counter = 0;
		while (stringTokenizer.hasMoreTokens()) {
			if (stringTokenizer.countTokens()==1) {
				direction = Integer.parseInt(stringTokenizer.nextToken());
			}
			else {
				char[] node = new char[1]; 
				node[0] = stringTokenizer.nextToken().charAt(0);
				nodes[counter] = node;
				counter++;
			}
		}
		if (!(direction == 0 || direction == 1) ||nodes[0]==null || nodes[1]==null) {
			System.out.println("The binet is incorrect.");
			System.out.println("Each binet should be entered on a new line.");
			System.out.println("First the two nodes and then the direction should be entered, seperated with a space.");
			System.exit(0);
		}
		Binet binet = new Binet(nodes[0],nodes[1],direction);
		binets = addBinetToArray(binets,binet);
	}
	
	public static void runAlgorithm(Binet[] binetArray) {
		binets = binetArray;
		nodes = BuildNW.getNodes(binets);
		arcs = BuildNW.getArcs(binets);
		
		computeSuperNetwork(nodes, arcs);
		superNetwork = new SuperNetwork(networkBinets);
		if(containsAllBinets()) {
			getNewickString();
		}
		else {
			System.out.println("Doens't contain all binets");
			getNewickString();
		}
	}
	
	public static void computeSuperNetwork(char[] nodeArray, Arc[] arcArray) {
		if (!goToNextIteration) {
			if (nodeArray.length<=1) {
				return;
			}
			else if (nodeArray.length==2 && arcArray.length==1) {
				Binet networkBinet = new Binet(arcArray[0].getNodes()[0], arcArray[0].getNodes()[1], arcArray[0].getDirection());
				networkBinets = addBinetToArray(networkBinets, networkBinet);
			}
			else {
				BuildNW.computeRNetwork(nodeArray, arcArray);
				if (!BuildNW.satisfiesErrorConditionOne(BuildNW.edgeConnectedComponents, BuildNW.arcConnectedComponents)) {
					BuildNW.componentArcs = BuildNW.computeComponentArcs(BuildNW.edgeConnectedComponents, arcArray);
					if(BuildNW.componentArcs.length==0 && BuildNW.arcConnectedComponents.length==nodeArray.length) {
						Binet[] treeBinets = BuildNW.computeTreeBinets(nodeArray);
						for (int binetNo = 0; binetNo<treeBinets.length; binetNo++) {
							networkBinets = addBinetToArray(networkBinets,treeBinets[binetNo]);
						}
					}
					else if(!BuildNW.satifiesErrorConditionTwo(BuildNW.edgeConnectedComponents, BuildNW.componentArcs)) {
						UComponents = BuildNW.discoverU();
						if (UComponents.length>0 && BuildNW.edgeConnectedComponents.length>1) {
							char[] upNetworkNodes = UComponents[0];
							Arc[] upNetworkArcs = BuildNW.getSubsetArcs(upNetworkNodes, arcArray);
							
							char[] lowNetworkNodes = BuildNW.removeNodeArray(nodeArray, upNetworkNodes);
							Arc[] lowNetworkArcs = BuildNW.getSubsetArcs(lowNetworkNodes,arcArray);
							
							computeSuperNetwork(upNetworkNodes, upNetworkArcs);
							computeSuperNetwork(lowNetworkNodes, lowNetworkArcs);
							
							Binet binet = new Binet(upNetworkNodes,lowNetworkNodes,1);
							networkBinets = addBinetToArray(networkBinets,binet);
						}
						else if (UComponents.length==1) {
							char[] leftNetworkNodes = BuildNW.arcConnectedComponents[0];
							Arc[] leftNetworkArcs = BuildNW.getSubsetArcs(leftNetworkNodes, arcArray);
							
							char[] rightNetworkNodes = BuildNW.removeNodeArray(nodeArray, leftNetworkNodes);
							Arc[] rightNetworkArcs = BuildNW.getSubsetArcs(rightNetworkNodes, arcArray);
							
							computeSuperNetwork(leftNetworkNodes, leftNetworkArcs);
							computeSuperNetwork(rightNetworkNodes, rightNetworkArcs);
							
							Binet binet = new Binet(leftNetworkNodes,rightNetworkNodes,0);
							networkBinets = addBinetToArray(networkBinets, binet);
						}
					}
					else {
						errorNo = 2;
						AlternativesForRemoval.removeBinetsAlternatives(binets, currentAlternativeNo);
						//System.exit(0);
						goToNextIteration = true;
					}
				}
				else {
					errorNo = 1;
					AlternativesForRemoval.removeBinetsAlternatives(binets, currentAlternativeNo);
					//System.exit(0);
					goToNextIteration = true;
				}
			}
		}
	}

	public static Binet[] addBinetToArray(Binet[] binetArray, Binet binet) {
		if (binetArray[binetArray.length-1]!=null) {
			Binet[] newArray = new Binet[binetArray.length+1];
			for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
				newArray[binetNo]=binetArray[binetNo];
			}
			
			newArray[binetArray.length]=binet;
			return newArray;
		}
		else {
			for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
				if (binetArray[binetNo]==null) {
					binetArray[binetNo]=binet;
					break;
				}
			}
			return binetArray;
		}
	}

	public static void getNewickString() {
		if(newick==null) {
			newick = superNetwork.getNewick();
		}
	}

	public static boolean containsAllBinets() {
		boolean[] containsBinet = new boolean[binets.length];
		for (int binetNo = 0; binetNo<binets.length; binetNo++) {
			containsBinet[binetNo] = containsBinet(binets[binetNo]);
		}
		boolean contains = true;
		for(boolean binetContained: containsBinet){
			  if(!binetContained) { 
				  contains = false;
			  }
		}
		return contains;
	}
	
	public static boolean containsBinet(Binet binet) {
		for (int nwBinetNo = 0; nwBinetNo<networkBinets.length; nwBinetNo++) {
			if(binet.getConnection().getDirection()==networkBinets[nwBinetNo].getConnection().getDirection()) {
				if (BuildNW.containsNode(networkBinets[nwBinetNo].getNodes()[0], binet.getNodes()[0][0]) && BuildNW.containsNode(networkBinets[nwBinetNo].getNodes()[1], binet.getNodes()[1][0])) {
					return true;
				}
				else if ((BuildNW.containsNode(networkBinets[nwBinetNo].getNodes()[1], binet.getNodes()[0][0]) && BuildNW.containsNode(networkBinets[nwBinetNo].getNodes()[0], binet.getNodes()[1][0])) && binet.getConnection().getDirection()==0) {
					return true;
				}
			}
		}
		return false;
	}

	public static int containingBinets() {
		int counter = 0;
		for (int binetNo=0; binetNo<binets.length; binetNo++) {
			if (containsBinet(binets[binetNo])) {
				counter++;
			}
		}
		return counter;
	}

	public static void resetValues(Binet[] binetArray) {
		binets = binetArray;
		networkBinets = new Binet[1];
	}

	public static int getRandomNumber(int length) {
		Random rand = new Random();
		return rand.nextInt(length);
	}
	
	public static Binet[] alterBinets(Binet[] binetArray) {
		int[] removedBinets = new int[binetArray.length];
		Arrays.fill(removedBinets,-1);
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			int number = getRandomNumber(100)+1;
			if (number<changeForRemoval) {
				removedBinets = addToIntArray(removedBinets,binetNo);
			}
			else {
				number = getRandomNumber(100)+1;
				if (number<changeForAlteration) {
					binetArray = AlternativesForRemoval.alterBinet(binetArray, binetArray[binetNo]);
				}
			}
		}
		removedBinets = trimIntArray(removedBinets);
		for (int binetNo = removedBinets.length-1; binetNo>=0; binetNo--) {
			binetArray = AlternativesForRemoval.removeBinet(binetArray, binetArray[removedBinets[binetNo]]);
		}
		return binetArray;
	}

	public static int[] addToIntArray(int[] valueArray, int value) {
		if (valueArray[valueArray.length-1]==-1) {
			for (int valueNo = 0; valueNo<valueArray.length; valueNo++) {
				if (valueArray[valueNo]==-1) {
					valueArray[valueNo]=value;
					return valueArray;
				}
			
			}
		}
		else {
			int[] returnArray = new int[valueArray.length+1];
			for (int valueNo = 0; valueNo<valueArray.length; valueNo++) {
				returnArray[valueNo] = valueArray[valueNo];
			}
			returnArray[returnArray.length-1] = value;
			return returnArray;
		}
		return valueArray;
	}

	public static int[] trimIntArray(int[] valueArray) {
		if(valueArray[valueArray.length-1]==-1) {
			int value = -1;
			for(int valueNo = 0; valueNo<valueArray.length; valueNo++) {
				if (valueArray[valueNo] == -1) {
					value = valueNo;
					break;
				}
			}
			int[] returnArray = new int[value];
			for (int valueNo = 0; valueNo<value; valueNo++) {
				returnArray[valueNo] = valueArray[valueNo];
			}
			return returnArray;
		}
		return valueArray;
	}
	
	public static double averagePerIteration(int totalValue, int numberOfIterations) {
		return totalValue/numberOfIterations;
	}
}