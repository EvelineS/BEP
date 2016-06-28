package main;

import java.util.Arrays;

public class RNetwork {
	Arc[] arcs;
	char[] nodes;
	char[][] edgeConnectedComponents;
	char[][] arcConnectedComponents;
	
	public RNetwork(Arc[] arcArray, char[] nodeArray) {
		arcs=arcArray;
		nodes=nodeArray;
		edgeConnectedComponents = new char[nodes.length][nodes.length];
		arcConnectedComponents =  new char[nodes.length][nodes.length];
	}
	
	public char[] getNodes() {
		return nodes;
	}
	
	public Arc[] getArcs() {
		return arcs;
	}
	
	public boolean isConnected(char n1, char n2, int direction) {
		char[] node1 = new char[1];
		node1[0] = n1;
		char[] node2 = new char[1];
		node2[0] = n2;
		Arc arc = new Arc(node1, node2, direction);
		for (int arcNo=0; arcNo<arcs.length; arcNo++) {
			if (arcs[arcNo].isEqual(arc))  {
				return true;
			}
		}
		return false;
	}
	
	public char[][] edgeConnectedComponents() {
		for (int node1=0; node1<nodes.length; node1++) {
			for (int node2=node1+1; node2<nodes.length; node2++) {
				if (isConnected(nodes[node1], nodes[node2], 0) || isConnected(nodes[node2], nodes[node1], 0)) {
					for (int componentNo=0; componentNo<edgeConnectedComponents.length; componentNo++) {
						if ((edgeConnectedComponents[componentNo][0]==0) || (containsNode(edgeConnectedComponents[componentNo],nodes[node1]) || containsNode(edgeConnectedComponents[componentNo],nodes[node2]))) {
							addNode(edgeConnectedComponents[componentNo],nodes[node1]);
							addNode(edgeConnectedComponents[componentNo],nodes[node2]);
							break;
						}
					}
				}
			}	
		}
		for (int node=0; node<nodes.length; node++) {
			if (!isEdgeConnected(edgeConnectedComponents,nodes[node])) {
				for (int componentNo=0; componentNo<edgeConnectedComponents.length; componentNo++) {
					if ((edgeConnectedComponents[componentNo][0]==0)) {
						addNode(edgeConnectedComponents[componentNo],nodes[node]);
						break;
					}
				}
			}
		}
		edgeConnectedComponents = trimNodeMatrix(edgeConnectedComponents);
		edgeConnectedComponents = checkForDoubleNodes(edgeConnectedComponents);
		return edgeConnectedComponents;
	}
	
	public boolean isEdgeConnected(char[][] components, char node) {
		for (int component=0; component<edgeConnectedComponents.length; component++) {
			if (containsNode(components[component], node)) {
				return true;
			}
		}
		return false;
	}
	
	public char[][] arcConnectedComponents() {
		for (int node1=0; node1<nodes.length; node1++) {
			for (int node2=0; node2<nodes.length; node2++) {
				if (node1!=node2 && (isConnected(nodes[node1], nodes[node2],1))) {
					for (int componentNo=0; componentNo<arcConnectedComponents.length; componentNo++) {
						if ((arcConnectedComponents[componentNo][0]==0) || (containsNode(arcConnectedComponents[componentNo],nodes[node1]) || containsNode(arcConnectedComponents[componentNo],nodes[node2]))) {
							addNode(arcConnectedComponents[componentNo],nodes[node1]);
							addNode(arcConnectedComponents[componentNo],nodes[node2]);
							break;
						}
					}
				}
			}	
		}
		for (int node=0; node<nodes.length; node++) {
			if (!isArcConnected(arcConnectedComponents,nodes[node])) {
				for (int componentNo=0; componentNo<arcConnectedComponents.length; componentNo++) {
					if ((arcConnectedComponents[componentNo][0]==0)) {
						addNode(arcConnectedComponents[componentNo],nodes[node]);
						break;
					}
				}
			}
		}
		arcConnectedComponents = trimNodeMatrix(arcConnectedComponents);
		arcConnectedComponents = checkForDoubleNodes(arcConnectedComponents);
		return arcConnectedComponents;
	}
	
	public boolean isArcConnected(char[][] components, char node) {
		for (int component=0; component<arcConnectedComponents.length; component++) {
			if (containsNode(components[component], node)) {
				return true;
			}
		}
		return false;
	}
	
	public char[][] checkForDoubleNodes(char[][] nodeArray) {
		int[] removeArrayNo = new int[nodeArray.length-1];
		Arrays.fill(removeArrayNo, -1);
		int counter = 0;
		for (int component1 = 0; component1<nodeArray.length; component1++) {
			for (int component2 = component1+1; component2<nodeArray.length; component2++) {
				for (int node = 0; node<nodeArray[component1].length; node++) {
					if(containsNode(nodeArray[component2], nodeArray[component1][node])) {
						if(!arrayContainsElement(removeArrayNo, component2)) {						
							nodeArray[component1] = mergeNodeArrays(nodeArray[component1], nodeArray[component2]);
							removeArrayNo[counter] = component2;
							counter++;
						}
					}
				}
			}
		}
		removeArrayNo = trimIntArray(removeArrayNo);
		Arrays.sort(removeArrayNo);
		for (int arrayNo = removeArrayNo.length-1; arrayNo>=0; arrayNo--) {
			nodeArray = removeArrayFromMatrix(nodeArray, removeArrayNo[arrayNo]);
		}		
		return trimNodeMatrix(nodeArray);
	}
	
	public boolean containsNode(char[] nodeArray, char node) {
		for (int nodeNo=0; nodeNo<nodeArray.length; nodeNo++) {
			if (nodeArray[nodeNo]==node) {
				return true;
			}
		}
		return false;
	}
	
	public void addNode(char[] nodeArray, char node) {
		if(!containsNode(nodeArray,node)) {
			for (int i=0; i<nodeArray.length; i++) {
				if (nodeArray[i]==0) {
					nodeArray[i]=node;
					return;
				}
			}
		}
	}
	
	public char[] mergeNodeArrays(char[] nodeArray1, char[] nodeArray2) {
		char[] returnArray = new char[nodeArray1.length+nodeArray2.length];
		for (int node1 = 0; node1<nodeArray1.length; node1++) {
			addNode(returnArray, nodeArray1[node1]);
		}
		for (int node2 = 0; node2<nodeArray2.length; node2++) {
			addNode(returnArray, nodeArray2[node2]);
		}
		return trimNodeArray(returnArray);
	}
	
	public static char[][] removeArrayFromMatrix(char[][] matrix, int arrayNo) {
		char[][] returnMatrix = new char[matrix.length-1][];
		for (int array = 0; array<arrayNo; array++) {
			returnMatrix[array] = matrix[array];
		}
		for (int array = arrayNo+1; array<matrix.length; array++) {
			returnMatrix[array-1] = matrix[array];
		}
		return returnMatrix;
	}
	
	public boolean arrayContainsElement(int[] array, int element) {
		for (int elementNo = 0; elementNo<array.length; elementNo++) {
			if(array[elementNo]==element) {
				return true;
			}
		}
		return false;
	}
	
	public int[] trimIntArray(int[] intArray) {
		for (int element = 0; element<intArray.length; element++) {
			if (intArray[element]==-1) {
				int[] temp = new int[element];
				for (int elementNo=0; elementNo<element; elementNo++) {
					temp[elementNo]=intArray[elementNo];
				}
				return temp;
			}
		}
		return intArray;
	}
	
	public char[] trimNodeArray(char[] nodeArray) {
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
	
	public char[][] trimNodeMatrix(char[][] nodeMatrix) {
		for (int node1=0; node1<nodeMatrix.length; node1++) {
			nodeMatrix[node1]=trimNodeArray(nodeMatrix[node1]);
			if(nodeMatrix[node1].length==0) {
				char[][] temp = new char[node1][];
				for (int node2 = 0; node2<node1; node2++) {
					temp[node2]=nodeMatrix[node2];
				}
				nodeMatrix = temp;
				break;
			}
		}
		return nodeMatrix;
	}
}
