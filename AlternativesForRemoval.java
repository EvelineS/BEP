package main;

import java.util.Arrays;

public class AlternativesForRemoval {
	static Binet[] binets;
	
	public static void removeBinetsAlternatives(Binet[] binetArray, int alternativeNo) {
		binets = binetArray;
		
		if (alternativeNo == 1) {
			removeBinetsAlternativeOne();
		}
		else if (alternativeNo ==2 ) {
			removeBinetsAlternativeTwo();
		}
		else if (alternativeNo == 3) {
			removeBinetsAlternativeThree();
		}
		else {
			removeBinetsAlternativeFour();
		}
		Algorithm.resetValues(binets);
		Algorithm.runAlgorithm(binets);
	}
	
	public static void removeBinetsAlternativeOne() {
		//Remove a random Binet
		int binetNo = Algorithm.getRandomNumber(binets.length);
		
		//System.out.println(binets[binetNo].toString());
		binets = removeBinet(binets,binetNo);
	}
	
	public static void removeBinetsAlternativeTwo() {
		char[] nodeArray = new char[1];
		Binet[] binetArray = new Binet[1];
		//remove a random Binet which is contained in cykel
		if(Algorithm.errorNo == 2) {
			nodeArray = getNodesFromMatrix(BuildNW.getCycleComponents());
			Arc[] arcArray = BuildNW.getCycleArcs();
			binetArray = getBinetsFromCycleArcs(arcArray,binets);
		}
		//remove random binet
		else {
			nodeArray = Algorithm.nodes;
			binetArray = binets;
		}
		Binet[] removeableBinets = findBinetsFromNodes(nodeArray, binetArray);
		Binet removedBinet = removeableBinets[Algorithm.getRandomNumber(removeableBinets.length)];
		
		//System.out.println(removedBinet.toString());
		binets = removeBinet(binets,removedBinet);
	}
	
	public static void removeBinetsAlternativeThree() {
		Binet[] removeableBinets = new Binet[1];
		//remove a random Binet which is contained in the smallest cycle
		if(Algorithm.errorNo == 2) {
			removeableBinets = getBinetsFromSmallestCykels();
		}
		//remove random binet
		else {
			removeableBinets = findBinetsFromNodes(Algorithm.nodes, binets);
		}
		Binet removedBinet = removeableBinets[Algorithm.getRandomNumber(removeableBinets.length)];
		
		//System.out.println(removedBinet.toString());
		binets = removeBinet(binets,removedBinet);
	}
	
	public static void removeBinetsAlternativeFour() {
		Binet[] removeableBinets = null;
		//remove a random Binet which is contained in most cycles
		if(Algorithm.errorNo == 2) {
			removeableBinets = getArcContainedInMostCycles();
		}
		//remove random binet
		else {
			removeableBinets = findBinetsFromNodes(Algorithm.nodes, binets);
		}
		Binet removedBinet = removeableBinets[Algorithm.getRandomNumber(removeableBinets.length)];
		
		//System.out.println(removedBinet.toString());
		binets = removeBinet(binets,removedBinet);
	}	
	
	//Removing Binet from array
	public static Binet[] removeBinet(Binet[] binetArray, int binetNo) {
		if (binetNo<binetArray.length) {
			Binet[] returnArray = new Binet[binetArray.length-1];
			for (int number = 0; number<binetNo; number++) {
				returnArray[number]=binetArray[number];
			}
			for (int number=binetNo+1; number<binetArray.length; number++) {
				returnArray[number-1]=binetArray[number];
			}
			return returnArray;
		}
		return binetArray;
	}
	
	public static Binet[] removeBinet(Binet[] binetArray, Binet binet) {
		if(getBinetNo(binetArray,binet)!=-1) {
			binetArray = removeBinet(binetArray,getBinetNo(binetArray,binet));
		}
		return binetArray;
	}
	
	public static int getBinetNo(Binet[] binetArray, Binet binet) {
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			if (binetArray[binetNo].isEqual(binet)) {
				return binetNo;
			}
		}
		return -1;
	}
	
	public static Binet[] alterBinet(Binet[] binetArray, Binet binet) {
		int binetNo = getBinetNo(binetArray, binet);
		int number = Algorithm.getRandomNumber(100)+1;
		if (binet.getConnection().getDirection()==0) {
			if (number<=50) {
				binetArray[binetNo] = redirectBinet(binet,1);
			}
			else {
				binetArray[binetNo] = redirectBinet(binet,-1);
			}
		}
		else {
			if (number<=50) {
				binetArray[binetNo] = redirectBinet(binet,-1);
			}
			else {
				binetArray[binetNo] = redirectBinet(binet,0);
			}
		}
		return binetArray;
	}
	
	//Auxilary methods
	public static Binet redirectBinet(Binet binet, int direction) {
		if (direction == -1) {
			return new Binet(binet.getNodes()[1], binet.getNodes()[0], 1);
		}
		return new Binet(binet.getNodes()[0], binet.getNodes()[1], direction);
	}
	
 	public static int[] getNumberOfInArcs(char[] nodeArray, Arc[] arcArray) {
		int[] returnArray = new int[nodeArray.length];
		char[] temp = new char[1];
		for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
			temp[0] = nodeArray[nodeNo];
			returnArray[nodeNo] =  BuildNW.numberOfIncomingArcs(temp, arcArray);
		}
		return returnArray;
	}
	
	public static int[] getNumberOfOutArcs(char[] nodeArray, Arc[] arcArray) {
		int[] returnArray = new int[nodeArray.length];
		char[] temp = new char[1];
		for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
			temp[0] = nodeArray[nodeNo];
			returnArray[nodeNo] =  BuildNW.numberOfOutgoingArcs(temp, arcArray);
		}
		return returnArray;
	}
	
	public static int[] getNumberOfArcs(char[] nodeArray, Arc[] arcArray) {
		int[] numberOfInArcs = getNumberOfInArcs(nodeArray, arcArray);
		int[] numberOfOutArcs = getNumberOfOutArcs(nodeArray, arcArray);
		
		int[] numberOfArcs = new int[nodeArray.length];
		for(int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
			numberOfArcs[nodeNo] = numberOfInArcs[nodeNo] + numberOfOutArcs[nodeNo];
		}
		return numberOfArcs;
	}
	
	public static int getMaxValue(int[] valueArray) {
		int returnValue = Integer.MIN_VALUE;
		for (int element = 0; element<valueArray.length; element++) {
			if (valueArray[element] > returnValue) {
				returnValue = valueArray[element];
			}
		}
		return returnValue;
	}
	
	public static int getMinValue(int[] valueArray) {
		int returnValue = Integer.MAX_VALUE;
		for (int element = 0; element<valueArray.length; element++) {
			if (valueArray[element] < returnValue) {
				returnValue = valueArray[element];
			}
		}
		return returnValue;
	}
	
	public static char[] getNodesWithMaxValue(char[] nodeArray, int[] valueArray, int maxValue) {
		char[] returnArray = new char[nodeArray.length];
		for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
			if (valueArray[nodeNo]==maxValue) {
				BuildNW.addNode(returnArray, nodeArray[nodeNo]);
			}
		}
		return BuildNW.trimNodeArray(returnArray);
	}
	
	public static Arc[] getArcsWithMaxValue(Arc[] arcArray, int[] valueArray, int maxValue) {
		Arc[] returnArray = new Arc[arcArray.length];
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			if(valueArray[arcNo] == maxValue) {
				BuildNW.addArc(returnArray, arcArray[arcNo]);
			}
		}
		return BuildNW.trimArcArray(returnArray);
	}
	
	public static char[][] getCyclesWithShortestLength(char[][] cycleMatrix, int[] lengthArray, int shortestLength) {
		char[][] returnMatrix = new char[cycleMatrix.length][];
		for (int cycleNo = 0; cycleNo<cycleMatrix.length; cycleNo++) {
			if(lengthArray[cycleNo]==shortestLength) {
				BuildNW.addNodeArrayToMatrix(returnMatrix, cycleMatrix[cycleNo]);
			}
		}
		return BuildNW.trimComponentMatrix(returnMatrix);
	}
	
	public static Binet[] findBinetsFromNodes(char[] nodeArray, Binet[] binetArray) {
		Binet[] returnArray = new Binet[binetArray.length];
		if (getNodesFromBinets(binetArray).length == nodeArray.length) {
			returnArray = binetArray;
		}
		else {
			Arc[] arcArray = getArcsFromBinets(binetArray);
			for (int node1No = 0; node1No<nodeArray.length; node1No++) {
				for (int node2No = 0; node2No<nodeArray.length; node2No++) { 
					if (node1No != node2No) {
						char[] leftNode = new char[1];
						char[] rightNode = new char[1];
						leftNode[0] = nodeArray[node1No];
						rightNode[0] = nodeArray[node2No];
						if(BuildNW.containsArc(arcArray, new Arc(leftNode, rightNode, 1))) {
							Binet binet = new Binet(leftNode, rightNode, 1);
							returnArray = Algorithm.addBinetToArray(returnArray, binet);
						}
					}	
				}
			}
		}
		return BuildNW.trimBinetArray(returnArray);
	}
	
	public static char[] getNodesFromBinets(Binet[] binetArray) {
		char[] nodeArray = new char[binetArray.length*2];
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			BuildNW.addNodeArray(nodeArray,	binetArray[binetNo].getNodeArray());
		}
		return BuildNW.trimNodeArray(nodeArray);
	}
	
	public static Arc[] getArcsFromBinets(Binet[] binetArray) {
		Arc[] arcArray = new Arc[binetArray.length];
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			arcArray[binetNo] = binetArray[binetNo].getConnection();
		}
		return arcArray;
	}
	
	public static Binet[] getBinetsFromArcs(Arc[] arcArray) {
		Binet[] binetArray = new Binet[arcArray.length];
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			binetArray[arcNo] = new Binet(arcArray[arcNo].getNodes()[0], arcArray[arcNo].getNodes()[1], arcArray[arcNo].getDirection());
		}
		return BuildNW.trimBinetArray(binetArray);
	}
	
	public static char[][] getNodesFromCycles(char[] nodeArray, Arc[] arcArray) {
		int[] numberOfInArcs = getNumberOfInArcs(nodeArray, arcArray);
		int[] numberOfOutArcs = getNumberOfOutArcs(nodeArray, arcArray);
		int[] numberOfTimesPassed = new int[nodeArray.length];
		boolean hadAllCycles = false;

		int maxValue = getMaxValue(numberOfOutArcs);
		char[] maxValueNodes = getNodesWithMaxValue(nodeArray, numberOfOutArcs, maxValue);
		int nodeNo = 0;
		char[] maxValueNode = new char[1];
		maxValueNode[0] = maxValueNodes[nodeNo];
		char[][] cycles = new char[1][];
		while(!hadAllCycles) {
			char[][] cycleNodes = getCyclesFromNode(arcArray, maxValueNode);
			maxValueNode[0] = maxValueNodes[nodeNo];
			for (int cycleNo = 0; cycleNo<cycleNodes.length; cycleNo++) {
				cycles = BuildNW.addNodeArrayToMatrix(cycles,cycleNodes[cycleNo]);
			}

			numberOfTimesPassed = getNumberOfTimesPassedNodes(cycles, nodeArray);
			if (maxValueNode[0] == maxValueNodes[maxValueNodes.length-1] && allNodesPassedLargerThanNumberOfInArcs(numberOfTimesPassed,numberOfInArcs)) {
				hadAllCycles = true;
			}
			else {
				int number = getNodeNoFromArray(nodeArray, maxValueNode[0]);
				if (numberOfTimesPassed[number]>=numberOfOutArcs[number]) {
					nodeNo++;
					maxValueNode[0] = maxValueNodes[nodeNo];
				}
			}
		}
		return removeDoubleCycles(cycles);
	}
	
	public static Binet[] getBinetsFromSmallestCykels() {
		char[][] componentMatrix = new char[Algorithm.nodes.length][];
		for (int componentNo = 0; componentNo<BuildNW.getCycleComponents().length; componentNo++) {
			componentMatrix = BuildNW.addNodeArrayToMatrix(componentMatrix,BuildNW.getCycleComponents()[componentNo]);
		}
		componentMatrix = BuildNW.trimComponentMatrix(componentMatrix);
		Arc[] componentArcs = BuildNW.getCycleArcs();
		Binet[] componentBinets = getBinetsFromArcs(componentArcs);
		
		//convert components to nodes
		String[][] conversionMatrix = replaceComponentsWithNodes(componentMatrix);
		char[][] cycles = getNodesFromCycles(applyConversionMatrix(componentMatrix, conversionMatrix), applyConversionMatrixToArcs(componentArcs, conversionMatrix));
		
		int[] cycleLength = new int[cycles.length];
		
		int shortestCycleLength = getMinValue(cycleLength);
		char[][] shortestCycles = getCyclesWithShortestLength(cycles, cycleLength, shortestCycleLength);
		
		int cycleNo = Algorithm.getRandomNumber(shortestCycles.length);
		
		char[][] cycleNodes = getComponentsFromNodes(conversionMatrix, cycles[cycleNo]);
		
		Binet[] cycleBinets = getBinetsFromNodes(componentBinets, cycleNodes);
				
		return getDirectedBinetsFrom(cycleBinets);
	}
	
	public static Binet[] getArcContainedInMostCycles() {
		char[][] componentMatrix = new char[Algorithm.nodes.length][];
		for (int componentNo = 0; componentNo<BuildNW.getCycleComponents().length; componentNo++) {
			componentMatrix = BuildNW.addNodeArrayToMatrix(componentMatrix,BuildNW.getCycleComponents()[componentNo]);
		}
		componentMatrix = BuildNW.trimComponentMatrix(componentMatrix);
		Arc[] componentArcs = BuildNW.getCycleArcs();
		
		//convert components to nodes
		String[][] conversionMatrix = replaceComponentsWithNodes(componentMatrix);
		char[] nodeArray = applyConversionMatrix(componentMatrix, conversionMatrix);
		Arc[] arcArray = applyConversionMatrixToArcs(componentArcs, conversionMatrix);
		char[][] cycles = getNodesFromCycles(nodeArray, arcArray);
		int[] numberOfTimesArcInCycles = getNumberOfTimesPassedArcs(cycles, arcArray);
		int maxValue = getMaxValue(numberOfTimesArcInCycles);
		Arc[] maxValueArcs = getArcsWithMaxValue(arcArray, numberOfTimesArcInCycles, maxValue);

		Binet[] binetArray = getBinetsFromArcs(maxValueArcs);
		binetArray = reapplyConversionMatrixToBinets(conversionMatrix, binetArray);
		binetArray = getDirectedBinetsFrom(binetArray);
		
		return binetArray;
	}
	
	
	public static char[][] removeDoubleCycles(char[][] cycles) {
		for (int cycleNo = 0; cycleNo<cycles.length; cycleNo++) { 
			for (int cycle2No = cycleNo+1; cycle2No<cycles.length; cycle2No++) {
				if(sameCycle(cycles[cycleNo], cycles[cycle2No])) {
					cycles = removeCycle(cycles, cycle2No);
					cycles = removeDoubleCycles(cycles);
				}
			}
		}
		return cycles;
	}
	
	
	public static boolean sameCycle(char[] cycle1, char[] cycle2) {
		return (containedInOtherCycle(cycle1, cycle2) && containedInOtherCycle(cycle2,cycle1));
	}
	
	
	public static boolean containedInOtherCycle(char[] cycle1, char[] cycle2) {
		for (int element = 0; element<cycle1.length; element++) {
			if (!BuildNW.containsNode(cycle2, cycle1[element])) {
				return false;
			}
		}
		return true;
	}
	
	
	public static char[][] removeCycle(char[][] cycles, int number) {
		if (number<cycles.length) {
			char[][] returnArray = new char[cycles.length-1][];
			for (int  cycleNo= 0; cycleNo<number; cycleNo++) {
				returnArray[cycleNo] = cycles[cycleNo];
			}
			for (int cycleNo = number+1; cycleNo<cycles.length; cycleNo++) {
				returnArray[cycleNo-1] = cycles[cycleNo];
			}
			return returnArray;
		}
		return cycles;
	}
	
	
	public static char[] getNodesFromMatrix(char[][] componentMatrix) {
		char[] nodeArray = new char[Algorithm.nodes.length];
		for (int componentNo = 0; componentNo<componentMatrix.length; componentNo++) {
			nodeArray = BuildNW.addNodeArray(nodeArray, componentMatrix[componentNo]);
		}
		return BuildNW.trimNodeArray(nodeArray);	
	}
	
	
	public static char[][] getCyclesFromNode(Arc[] arcArray, char[] node) {
		char[] currentNode = node;
		char[][] cycleArray = new char[1][];
		cycleArray[0]=node;
		boolean allCyclesComplete = false;
		while(!allCyclesComplete) {
			int length = cycleArray.length;
			for (int cycleNo = 0; cycleNo<length; cycleNo++) {
				currentNode[0] = cycleArray[cycleNo][cycleArray[cycleNo].length-1];
				cycleArray = addNextNodeToCycle(cycleArray, arcArray, currentNode, cycleNo);
			}
			boolean[] cycleComplete = new boolean[cycleArray.length];
			Arrays.fill(cycleComplete,false);
			for (int cycleNo = 0; cycleNo<cycleArray.length; cycleNo++) {
				currentNode[0] = cycleArray[cycleNo][cycleArray[cycleNo].length-1];
				Arc[] outgoingArcs = BuildNW.getOutgoingArcs(currentNode, arcArray);
				if(getNextNodesInCycle(outgoingArcs, currentNode)[0]==cycleArray[0][0]) {
					cycleComplete[cycleNo] = true;
				}
			}
			allCyclesComplete = BuildNW.checkBooleanArray(cycleComplete);
		}
		return cycleArray;
	}
	
	
	public static char[][] addNextNodeToCycle(char[][] cycleArray, Arc[] arcArray, char[] node, int currentCycleNo) {
		Arc[] outgoingArcs = BuildNW.getOutgoingArcs(node, arcArray);
		char[] nextNodes = getNextNodesInCycle(outgoingArcs,node);
		if (nextNodes.length==1 && BuildNW.containsNode(cycleArray[currentCycleNo], nextNodes[0])){
			cycleArray[currentCycleNo] = BuildNW.addNode(cycleArray[currentCycleNo],nextNodes[0]);
		}
		else {
			for (int outgoing = 0; outgoing<nextNodes.length-1; outgoing++) {
				cycleArray = BuildNW.addNodeArrayToMatrix(cycleArray, cycleArray[currentCycleNo]);
			}
			for (int cycleNo = 0; cycleNo<nextNodes.length; cycleNo++) {
				cycleArray[cycleNo+currentCycleNo] = BuildNW.addNode(cycleArray[cycleNo+currentCycleNo], nextNodes[cycleNo]); 
			}
		}
		return cycleArray;
	}
	
	
	public static char[] getNextNodesInCycle(Arc[] arcArray, char[] node) {
		char[] returnArray = new char[arcArray.length];
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			returnArray[arcNo] = arcArray[arcNo].getNodes()[1][0];
		}
		return returnArray;
	}
	
	
	public static int[] getNumberOfTimesPassedNodes(char[][] cycleMatrix, char[] nodeArray) {
		int[] returnArray = new int[nodeArray.length];
		for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
			returnArray[nodeNo] = getNumberOfTimesPassedNode(cycleMatrix,nodeArray[nodeNo]);
		}
		return returnArray;
	}
	
	
	public static int getNumberOfTimesPassedNode(char[][] cycleMatrix, char node) {
		int counter = 0;
		for (int cycleNo = 0; cycleNo<cycleMatrix.length; cycleNo++) {
			for (int nodeNo = 0; nodeNo<cycleMatrix[cycleNo].length; nodeNo++) {
				if (cycleMatrix[cycleNo][nodeNo]==node) {
					counter++;
				}
			}
		}
		return counter;
	}

	
	public static int[] getNumberOfTimesPassedArcs(char[][] cycleMatrix, Arc[] arcArray) {
		int[] returnArray = new int[arcArray.length];
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			returnArray[arcNo] = getNumberOfTimesPassedArc(cycleMatrix,arcArray[arcNo]);
		}
		return returnArray;
	}
	
	
	public static int getNumberOfTimesPassedArc(char[][] cycleMatrix, Arc arc) {
		int counter = 0;
		for (int cycleNo = 0; cycleNo<cycleMatrix.length; cycleNo++) {
			if(arcInCycle(cycleMatrix[cycleNo],arc)) {
				counter++;
			}
		}
		return counter;
	}
	
	
	public static boolean arcInCycle(char[] cycle, Arc arc) {
		for (int element = 0; element<cycle.length; element++) {
			
			if((element!=cycle.length-1 && (cycle[element]==arc.getNodes()[0][0] && cycle[element+1]==arc.getNodes()[1][0])) || (element==cycle.length-1 && (cycle[element]==arc.getNodes()[0][0] && cycle[0]==arc.getNodes()[1][0]))) {
				return true;
			}
		}
		return false;
	}
	
	
	public static boolean allNodesPassedLargerThanNumberOfInArcs(int[] numberOfTimesPassed, int[] numberOfInArcs) {
		boolean[] nodePassedEnoughTimes = new boolean[numberOfTimesPassed.length];
		Arrays.fill(nodePassedEnoughTimes, false);
		for (int nodeNo = 0; nodeNo<numberOfTimesPassed.length; nodeNo++) {
			if (numberOfTimesPassed[nodeNo]>=numberOfInArcs[nodeNo]) {
				nodePassedEnoughTimes[nodeNo] = true;
			}
		}
		return BuildNW.checkBooleanArray(nodePassedEnoughTimes);
	}

	
	public static String[][] replaceComponentsWithNodes(char[][] componentMatrix) {
		String[][] conversionMatrix = new String[componentMatrix.length][2];
		for (int componentNo = 0; componentNo<componentMatrix.length; componentNo++) {
			conversionMatrix[componentNo][0] = mergeCharToString(componentMatrix[componentNo]);
			conversionMatrix[componentNo][1] = getConversionValue(componentNo);
		}
		return conversionMatrix;
	}
	
	
	public static char[] applyConversionMatrix(char[][] componentMatrix, String[][] conversionMatrix) {
		char[] returnArray = new char[conversionMatrix.length];
		for (int nodeNo = 0; nodeNo<conversionMatrix.length; nodeNo++) {
			returnArray[nodeNo] = conversionMatrix[nodeNo][1].charAt(0);
		}
		return returnArray;
	}
	
	public static Binet[] applyConversionMatrixToBinets(String[][] conversionMatrix, Binet[] binetArray) {
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			char[] leftNode = binetArray[binetNo].getNodes()[0];
			char[] rightNode = binetArray[binetNo].getNodes()[1];
			leftNode = getComponentFromNode(conversionMatrix,leftNode[0]);
			rightNode = getComponentFromNode(conversionMatrix,rightNode[0]);
			binetArray[binetNo] = new Binet(leftNode, rightNode, binetArray[binetNo].getConnection().getDirection());
		}
		return binetArray;
	}
	
	public static Binet[] reapplyConversionMatrixToBinets(String[][] conversionMatrix, Binet[] binetArray) {
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			char[] leftNode = binetArray[binetNo].getNodes()[0];
			char[] rightNode = binetArray[binetNo].getNodes()[1];
			leftNode = getComponentFromNode(conversionMatrix,leftNode[0]);
			rightNode = getComponentFromNode(conversionMatrix,rightNode[0]);
			binetArray[binetNo] = new Binet(leftNode, rightNode, binetArray[binetNo].getConnection().getDirection());
		}
		return BuildNW.trimBinetArray(binetArray);
	}
	public static Arc[] applyConversionMatrixToArcs(Arc[] arcArray, String[][] conversionMatrix) {
		Arc[] returnArray = new Arc[arcArray.length];
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			char[] leftNode = getConversionElement(conversionMatrix, arcArray[arcNo].getNodes()[0]);
			char[] rightNode = getConversionElement(conversionMatrix, arcArray[arcNo].getNodes()[1]);
			Arc arc = new Arc(leftNode, rightNode, arcArray[arcNo].getDirection());
			returnArray[arcNo] = arc;
		}
		return returnArray;
	}
	
	public static String mergeCharToString(char[] array) {
		String returnString = "";
		for (int element = 0; element<array.length; element++) {
			returnString += array[element];
		}
		return returnString;
	}
	
	public static char[] extractCharFromString(String component) {
		char[] returnArray = new char[component.length()];
		for (int element = 0; element<component.length(); element++) {
			returnArray[element] = component.charAt(element);;
		}
		return returnArray;
	}
	
	public static String getConversionValue(int number) {
		int charValue = number+65;
		return "" + (char) charValue;		
	}

	public static char[] getConversionElement(String[][] conversionMatrix, char[] component) {
		for (int nodeNo = 0; nodeNo<conversionMatrix.length; nodeNo++) {
			if (extractCharFromString(conversionMatrix[nodeNo][0])[0] == component[0]) {
				char[] returnArray = new char[1];
				returnArray[0] = conversionMatrix[nodeNo][1].charAt(0);
				return returnArray;
			}
		}
		return null;
	}

	
	public static char[][] getComponentsFromNodes(String[][] conversionMatrix, char[] nodeArray) {
		char[][] returnArray = new char[nodeArray.length][];
		for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
			returnArray[nodeNo] = getComponentFromNode(conversionMatrix,nodeArray[nodeNo]);
		}
		return returnArray;
	}
	
	
	public static char[] getComponentFromNode(String[][] conversionMatrix, char node) {
		for (int nodeNo = 0; nodeNo<conversionMatrix.length; nodeNo++) {
			if(conversionMatrix[nodeNo][1].charAt(0)==node) {
				return extractCharFromString(conversionMatrix[nodeNo][0]);
			}
		}
		return null;
	}
	
	public static char[] getNodeFromComponent(String[][] conversionMatrix, char[] component) {
		for (int componentNo = 0; componentNo<conversionMatrix.length; componentNo++) {
			if(extractCharFromString(conversionMatrix[componentNo][0])==component) {
				return extractCharFromString(conversionMatrix[componentNo][1]);
			}
		}
		return null;
	}
	
	public static Binet[] getBinetsFromCycleArcs(Arc[] arcArray, Binet[] binetArray) {
		Binet[] returnArray = new Binet[binetArray.length];
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			char[] leftNodes = arcArray[arcNo].getNodes()[0];
			char[] rightNodes = arcArray[arcNo].getNodes()[1];
			
			for (int leftNodeNo = 0; leftNodeNo<leftNodes.length; leftNodeNo++) {			
				for (int rightNodeNo = 0; rightNodeNo<rightNodes.length; rightNodeNo++) {
					char[] leftNode = new char[1];
					leftNode[0] = leftNodes[leftNodeNo];
					char[] rightNode = new char[1];
					rightNode[0] = rightNodes[rightNodeNo];
					Binet binet = new Binet(leftNode, rightNode, arcArray[arcNo].getDirection());
					if(containsBinet(binetArray, binet)) {
						returnArray = addBinetToArray(returnArray,binet);
					}
				}
			}
		}	
		return BuildNW.trimBinetArray(returnArray);
	}
	
	public static boolean containsBinet(Binet[] binetArray, Binet binet) {
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			if (binetArray[binetNo]!=null && binetArray[binetNo].isEqual(binet)) {
				return true;
			}
		}
		return false;
	}
	
	public static Binet[] addBinetToArray(Binet[] binetArray, Binet binet) {
		if(!containsBinet(binetArray,binet)) {
			for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
				if (binetArray[binetNo]==null) {
					binetArray[binetNo]=binet;
					return binetArray;
				}
			}
		}
		return binetArray;
	}
	
	public static int getNodeNoFromArray(char[] nodeArray, char node) {
		if (BuildNW.containsNode(nodeArray, node)) {
			for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
				if(nodeArray[nodeNo]==node) {
					return nodeNo;
				}
			}
		}
		return -1;
	}
	
	public static Binet[] getDirectedBinetsFrom(Binet[] binetArray) {
		Binet[] returnArray = new Binet[binets.length];
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			for (int leftNodeNo = 0; leftNodeNo<binetArray[binetNo].getNodes()[0].length; leftNodeNo++) {
				for (int rightNodeNo = 0; rightNodeNo<binetArray[binetNo].getNodes()[1].length; rightNodeNo++) {
					char[] left = new char[1];
					left[0] = binetArray[binetNo].getNodes()[0][leftNodeNo];
					char[] right = new char[1];
					right[0] = binetArray[binetNo].getNodes()[1][rightNodeNo];
					Binet temp = new Binet(left, right, 1);
					if (containsBinet(binets, temp)) {
						returnArray = addBinetToArray(returnArray, temp);
					}
				}
			}
		}
		return BuildNW.trimBinetArray(returnArray);
	}
	
	public static Binet[] getBinetsFromNodes(Binet[] binetArray, char[][] nodeMatrix) {
		Binet[] returnArray = new Binet[binetArray.length];
		for(int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			if(BuildNW.containsComponent(nodeMatrix,binetArray[binetNo].getNodes()[0]) && BuildNW.containsComponent(nodeMatrix,binetArray[binetNo].getNodes()[1])) {
				returnArray = addBinetToArray(returnArray, binetArray[binetNo]);
			}
		}
		return BuildNW.trimBinetArray(returnArray);
	}
}