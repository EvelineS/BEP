package main;

import java.util.Arrays;

public class BuildNW {
	static RNetwork rNetwork;
	static char[][] edgeConnectedComponents;
	static char[][] arcConnectedComponents;
	static Arc[] componentArcs;
	static char[][] cycleComponents;
	static Arc[] cycleArcs;
	
	//Specific methods
	public static char[] getNodes(Binet[] binets) {
		char[] nodes = new char[binets.length*2];
		for (int binetNo=0; binetNo<binets.length; binetNo++) {
			for (int node1=0; node1<binets[binetNo].getNodes().length; node1++) {
				nodes = addNodeArray(nodes, binets[binetNo].getNodes()[node1]);
			}
		}
		return trimNodeArray(nodes);
	}
	
	public static Arc[] getArcs(Binet[] binets) {
		Arc[] arcs =  new Arc[(2*binets.length*(2*binets.length-1)/2)+1];
		for (int arcNo=0; arcNo<binets.length; arcNo++) {
			arcs = addArc(arcs, binets[arcNo].getConnection());		
		}
		return trimArcArray(arcs);
	}
	
	public static char[][] getCycleComponents() {
		return cycleComponents;
	}
	
	public static Arc[] getCycleArcs() {
		return cycleArcs;
	}
	
	public static void computeRNetwork(char[] nodeArray, Arc[] arcArray) {
		rNetwork = new RNetwork(arcArray,nodeArray);
		edgeConnectedComponents = rNetwork.edgeConnectedComponents();
		arcConnectedComponents = rNetwork.arcConnectedComponents();
	}

	public static Arc[] computeComponentArcs(char[][] edgeComponents, Arc[] arcArray) {
		Arc[] componentArcArray = new Arc[arcArray.length];
		char[] firstNode = null;
		char[] secondNode = null;
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			if (arcArray[arcNo].getDirection()==1) {
				for (int node1No = 0; node1No<arcArray[arcNo].getNodes().length; node1No++) {	
					for (int node2No = 0; node2No<arcArray[arcNo].getNodes()[node1No].length; node2No++) {
						for (int componentNo = 0; componentNo<edgeComponents.length; componentNo++) {
							if (containsNode(edgeComponents[componentNo], arcArray[arcNo].getNodes()[node1No][node2No])) {
								if (arcArray[arcNo].equalsRightSide(arcArray[arcNo].getNodes()[node1No])) {
									secondNode = edgeComponents[componentNo];
								}
								else {
									firstNode = edgeComponents[componentNo];
								}
							}
						}
					}
				}
				if (firstNode != secondNode) {
					Arc arc = new Arc(firstNode,secondNode,1);
					componentArcArray = addArc(componentArcArray,arc);
				}
			}
		}
		return trimArcArray(componentArcArray);
	}
	
	public static boolean satisfiesErrorConditionOne(char[][] edgeComponents, char[][] arcComponents) {
		return edgeComponents.length==1 && arcComponents.length==1;
	}
	
	public static boolean satifiesErrorConditionTwo(char[][] edgeComponents, Arc[] componentArcArray) {
		char[][] components = edgeComponents;
		Arc[] arcArray = componentArcArray;
		while (components.length>1) {
			char[][] subsetCycleComponents = getSubsetCycleComponents(components,arcArray);
			if(componentsAreEqual(components,subsetCycleComponents)) {
				cycleComponents = components;
				cycleArcs = getSubsetCycleArcs(components,arcArray);
				return true;
			}
			components = subsetCycleComponents;
			arcArray = getSubsetCycleArcs(components, arcArray);
		}
		return false;
	}
	
	public static char[][] discoverU() {
		char[][] componentsNoIncomingArcs = new char[edgeConnectedComponents.length][];
		int counter = 0;
		for (int componentNo = 0; componentNo<edgeConnectedComponents.length; componentNo++) {
			if (numberOfIncomingArcs(edgeConnectedComponents[componentNo], componentArcs) == 0) {
				componentsNoIncomingArcs[counter] = edgeConnectedComponents[componentNo];
				counter++;
			}
		}
		return trimComponentMatrix(componentsNoIncomingArcs);
	}

	public static char[][] getUComplement() {
		char[][] componentsNoIncomingArcs = new char[edgeConnectedComponents.length][];
		int counter = 0;
		for (int componentNo = 0; componentNo<edgeConnectedComponents.length; componentNo++) {
			if (numberOfIncomingArcs(edgeConnectedComponents[componentNo], componentArcs) != 0) {
				componentsNoIncomingArcs[counter] = edgeConnectedComponents[componentNo];
				counter++;
			}
		}
		return trimComponentMatrix(componentsNoIncomingArcs);
	}
	
	//Auxilary methods
	public static char[] addNode(char[] nodeArray, char node) {
		if(!containsNode(nodeArray,node)) {
			for (int nodeNo=0; nodeNo<nodeArray.length; nodeNo++) {
				if (nodeArray[nodeNo]==0) {
					nodeArray[nodeNo]=node;
					return nodeArray;
				}
			}
			char[] temp = new char[nodeArray.length+1];
			for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
				temp[nodeNo] = nodeArray[nodeNo];
			}
			temp[nodeArray.length] = node;
			return temp;
		}
		return nodeArray;
	}
	
	public static char[] addNodeArray(char[] nodeArray, char[] array) { 
		for (int node = 0; node<array.length; node++) {
			nodeArray = addNode(nodeArray, array[node]);
		}
		return nodeArray;
	}
	
	public static char[][] addNodeArrayToMatrix(char[][] nodeMatrix, char[] nodeArray) {
		if(nodeMatrix[nodeMatrix.length-1]==null) {
			for (int arrayNo = 0; arrayNo<nodeMatrix.length; arrayNo++) {
				if (nodeMatrix[arrayNo]==null) {
					nodeMatrix[arrayNo]=nodeArray;
					return nodeMatrix;
				}
			}
		}
		else {
			char[][] returnArray = new char[nodeMatrix.length+1][];
			for (int arrayNo = 0; arrayNo<nodeMatrix.length; arrayNo++) {
				returnArray[arrayNo] = nodeMatrix[arrayNo];
			}
			returnArray[nodeMatrix.length] = nodeArray;
			return returnArray;
		}
		return nodeMatrix;
	}
	
	public static boolean containsNode(char[] nodeArray, char node) {
		for (int nodeNo=0; nodeNo<nodeArray.length; nodeNo++) {
			if (nodeArray[nodeNo]==node) {
				return true;
			}
		}
		return false;
	}

	public static char[] removeNode(char[] nodeArray, char node) {
		if(containsNode(nodeArray, node)) {
			for (int nodeNo = 0; nodeNo<nodeArray.length; nodeNo++) {
				if(nodeArray[nodeNo]==node) {
					char[] temp = new char[nodeArray.length-1];
					for (int newNodeNo = 0; newNodeNo<nodeNo; newNodeNo++) {
						temp[newNodeNo] = nodeArray[newNodeNo];
					}
					for (int newNodeNo = nodeNo; newNodeNo<nodeArray.length-1; newNodeNo++) {
						temp[newNodeNo] = nodeArray[newNodeNo+1];
					}
					nodeArray = temp;
					break;
				}
			}
		}
		return nodeArray;
	}
	
	public static char[] removeNodeArray(char[] firstArray, char[] secondArray) {
		for(int nodeNo = 0; nodeNo<secondArray.length; nodeNo++) {
			firstArray = removeNode(firstArray, secondArray[nodeNo]);
		}
		return firstArray;
	}
	
	public static char[] trimNodeArray(char[] nodeArray) {
		if (nodeArray == null) {
			return new char[0];
		}
		for (int node1=0; node1<nodeArray.length; node1++) {
			if (nodeArray[node1]==0) {		
				char[] temp = new char[node1];
				for (int node2=0; node2<node1; node2++) {
					temp[node2]=nodeArray[node2];
				}
				return temp;
			}
		}
		return nodeArray;
	}
	
	public static char[][] trimComponentMatrix(char[][] componentMatrix) {
		for (int node1=0; node1<componentMatrix.length; node1++) {
			componentMatrix[node1]=trimNodeArray(componentMatrix[node1]);
			if(componentMatrix[node1].length==0) {
				char[][] temp = new char[node1][];
				for (int node2 = 0; node2<node1; node2++) {
					temp[node2]=componentMatrix[node2];
				}
				componentMatrix = temp;
				break;
			}
		}
		return componentMatrix;
	}
	
	public static boolean containsComponent(char[][] components, char[] component) {
		for (int componentNo = 0; componentNo<components.length; componentNo++) {
			boolean[] contains = new boolean[component.length];
			Arrays.fill(contains, false);
			for (int nodeNo = 0; nodeNo<component.length; nodeNo++) {
				if(containsNode(components[componentNo], component[nodeNo])) {
					contains[nodeNo] = true;
				}
			}
			if(checkBooleanArray(contains)) {
				return true;
			}
		}
		return false;
	}
		
	public static boolean componentsAreEqual(char[][] componentSet1, char[][] componentSet2) {
		for (int componentNo = 0; componentNo<componentSet2.length; componentNo++) {
			if (!containsComponent(componentSet1,componentSet2[componentNo])) {
				return false;
			}
		}
		for (int componentNo = 0; componentNo<componentSet1.length; componentNo++) {
			if (!containsComponent(componentSet2,componentSet1[componentNo])) {
				return false;
			}
		}
		return true;
	}
	
	public static Arc[] addArc(Arc[] arcArray, Arc arc) {
		if (!containsArc(arcArray,arc)) {
			boolean added = false;
			for (int arcNo=0; arcNo<arcArray.length; arcNo++) {
				if (arcArray[arcNo] == null) {
					added = true;
					arcArray[arcNo] = arc;
					break;
				}
			}
			if (!added) {
				Arc[] arcArray2 = new Arc[arcArray.length+1];
				for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
					arcArray2[arcNo]=arcArray[arcNo];
				}
				arcArray2[arcArray.length]=arc;
				arcArray = arcArray2;
			}
		}
		return arcArray;
	}
	
	public static boolean containsArc(Arc[] arcArray, Arc arc) {
		for (int arcNo=0; arcNo<arcArray.length; arcNo++) {
			if(arcArray[arcNo]!=null) {	
				if (arcArray[arcNo].isEqual(arc))  {
					return true;
				}
			}
			else {
				break;
			}
		}
		return false;
	}
		
	public static Arc[] trimArcArray(Arc[] arcArray) {
		for (int arcNo=0; arcNo<arcArray.length; arcNo++) {
			if (arcArray[arcNo]==null) {
				Arc[] returnArray = new Arc[arcNo];
				for (int arc=0; arc<arcNo; arc++) {
					returnArray[arc]=arcArray[arc];
				}
				return returnArray;
			}
		}
		return arcArray;
	}

	public static Arc[] mergeArcs(Arc[] firstArc, Arc[] secondArc) {
		for (int arcNo = 0; arcNo<secondArc.length; arcNo++) {
			firstArc = addArc(firstArc, secondArc[arcNo]);
		}
		return firstArc;
	}
	
	public static Binet[] trimBinetArray(Binet[] binetArray) {
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			if (binetArray[binetNo]==null) {
				Binet[] returnArray = new Binet[binetNo];
				for (int binet = 0; binet<binetNo; binet++) {
					returnArray[binet] = binetArray[binet];
				}
				return returnArray;
			}
		}
		return binetArray;
	}
	
	public static Binet[] computeTreeBinets(char[] nodeArray) {
		Binet[] temp = new Binet[nodeArray.length-1];
		int counter = 0;
		while (nodeArray.length > 1) {
			char[] left = new char[1];
			left[0] = nodeArray[0];
			char[] right = new char[nodeArray.length-1];
			for (int nodeNo = 1; nodeNo<nodeArray.length; nodeNo++) {
				right[nodeNo-1] = nodeArray[nodeNo];
			}
			Binet binet = new Binet(left,right,0);
			temp[counter] = binet;
			counter++;
			nodeArray = removeNode(nodeArray, nodeArray[0]);
		}
		return temp;
	}
	
 	public static int numberOfIncomingArcs(char[] component, Arc[] arcArray) {
		int counter = 0;
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			if (arcArray[arcNo].equalsRightSide(component)) {
				counter++;
			}
		}
		return counter;
	}
	
	public static int numberOfOutgoingArcs(char[] component, Arc[] arcArray) {
		int counter = 0;
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			if (arcArray[arcNo].equalsLeftSide(component)) {
				counter++;
			}
		}
		return counter;
	}

	public static Arc[] getIncomingArcs(char[] component, Arc[] arcArray) {
		Arc[] incoming = new Arc[numberOfIncomingArcs(component, arcArray)];
		int counter = 0;
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			if (arcArray[arcNo].equalsRightSide(component)) {
				incoming[counter]=arcArray[arcNo];
				counter++;
			}
		}
		return trimArcArray(incoming);
	}
		
	public static Arc[] getOutgoingArcs(char[] component, Arc[] arcArray) {
		Arc[] outgoing = new Arc[numberOfOutgoingArcs(component, arcArray)];
		int counter = 0;
		for (int arcNo = 0; arcNo<arcArray.length; arcNo++) {
			if (arcArray[arcNo].equalsLeftSide(component)) {
				outgoing[counter]=arcArray[arcNo];
				counter++;
			}
		}
		
		return trimArcArray(outgoing);
	}
	
	public static char[][] getSubsetCycleComponents(char[][] components, Arc[] arcArray) {
		char[][] subsetCycleComponents = new char[components.length][];
		int counter = 0;
		for (int componentNo = 0; componentNo<components.length; componentNo++) {
			if (numberOfIncomingArcs(components[componentNo], arcArray)>0 && numberOfOutgoingArcs(components[componentNo], arcArray)>0) {
				subsetCycleComponents[counter] = components[componentNo];
				counter++;
			}
		}
		return trimComponentMatrix(subsetCycleComponents);
	}
	
	public static Arc[] getSubsetCycleArcs(char[][] components, Arc[] arcArray) {
		Arc[] incomingArcs = new Arc[arcArray.length];
		Arc[] outgoingArcs = new Arc[arcArray.length];
		for (int componentNo = 0; componentNo<components.length; componentNo++) {
			Arc[] incoming = getIncomingArcs(components[componentNo], arcArray);
			incomingArcs = mergeArcs(incomingArcs, incoming);
			Arc[] outgoing = getOutgoingArcs(components[componentNo], arcArray);
			outgoingArcs = mergeArcs(outgoingArcs, outgoing);
		}
		incomingArcs = trimArcArray(incomingArcs);
		outgoingArcs = trimArcArray(outgoingArcs);
		Arc[] subsetArcs = new Arc[arcArray.length];
		int counter = 0;		
		for (int arcNo = 0; arcNo<incomingArcs.length; arcNo++) {
			if (containsArc(outgoingArcs,incomingArcs[arcNo])) {
				subsetArcs[counter]=incomingArcs[arcNo];
				counter++;
			}
		}
		return trimArcArray(subsetArcs);
	}
	
	public static Arc[] getSubsetArcs(char[] components, Arc[] arcArray) {
		Arc[] subsetArcs = new Arc[arcArray.length];
		for (int node1 = 0; node1<components.length; node1++) {
			for (int node2 = 0; node2<components.length; node2++) {
				if (node1!=node2) {
					char[] first = new char[1];
					first[0] = components[node1];
					char[] second = new char[1];
					second[0] = components[node2];
					Arc arc0 = new Arc(first, second, 0);
					Arc arc1 = new Arc(first, second, 1);
					if (containsArc(arcArray, arc0)) {
						subsetArcs = addArc(subsetArcs, arc0);
					}
					if (containsArc(arcArray, arc1)) {
						subsetArcs = addArc(subsetArcs, arc1);
					}
				}
			}
		}
		return trimArcArray(subsetArcs);
	}

	public static boolean checkBooleanArray(boolean[] array) {
		boolean value = true;
		for(boolean arrayElement: array){
			  if(!arrayElement) { 
				  value = false;
			  }
		}
		return value;
	}
}
