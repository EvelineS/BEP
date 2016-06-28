package main;

import java.util.*;

public class SuperNetwork {
	Binet[] binets = new Binet[4];
	String newick;
	char[][] reticulations;
	
	public SuperNetwork(Binet[] binetArray) {
		binets = sortBinets(binetArray);
		reticulations = new char[binets.length][];
		getNetwork();
	}
	
	public void getNetwork() {
		String network = "(";
		for (int binetNo=0; binetNo<binets.length; binetNo++) {
			network += binetToNewick(binets[binetNo]) + ",";
		}
		newick = network.substring(0, network.length()-1) + ");";
	}
	
	public String binetToNewick(Binet binet) {
		if (binet!=null) {
			String network = "";
			removeBinet(binet);
			//Tree
			if (binet.getConnection().getDirection()==0) {
				//left node
				if (binet.getNodes()[0].length==1) {
					network += "(" + binet.getNodes()[0][0] + "),";
				}
				else {
					network += "(" + binetToNewick(getBinet(binet,"left")) + "),";
				}
				
				//right node
				if (binet.getNodes()[1].length==1) {
					network += "(" + binet.getNodes()[1][0] + ")";
				}
				else {
					network += "(" + binetToNewick(getBinet(binet,"right")) + ")";
				}
			}
			
			//Hybrid
			else {
				addReticulation(binet.getNodeArray());
						
				//left node
				if (binet.getNodes()[0].length==1) {
					network += "((" + binet.getNodes()[0][0] + "),P" + getReticulationNo(binet.getNodeArray()) + "#H" + getReticulationNo(binet.getNodeArray()) + "),";
				}
				else {
					network += "((" + binetToNewick(getBinet(binet,"left")) + "),P" + getReticulationNo(binet.getNodeArray()) + "#H" + getReticulationNo(binet.getNodeArray()) + "),";
				}
				
				//right node
				if (binet.getNodes()[1].length==1) {
					network += "(((" + binet.getNodes()[1][0] + "))P" + getReticulationNo(binet.getNodeArray()) + "#H" + getReticulationNo(binet.getNodeArray()) + ")";
				}
				else {
					network += "(((" + binetToNewick(getBinet(binet,"right")) + "))P" + getReticulationNo(binet.getNodeArray()) + "#H" + getReticulationNo(binet.getNodeArray()) + ")";
				}
			}
			return network;
		}
		return "";
	}
		
	public Binet getBinet(Binet b, String s) {
		if (b.getNodes()[0].length>0 && b.getNodes()[1].length>0) {
			for (int binet=0; binet<binets.length; binet++) {
				if (!b.isEqual(binets[binet]) && (((((s=="left" && binets[binet].containsNode(b.getNodes()[0][0])))) || (((s=="right" && binets[binet].containsNode(b.getNodes()[1][0]))))))) { 		
					return binets[binet];
				}
			}
		}
		return null;
	}
	
	public String getNewick() {
		return newick;
	}
	
	public void removeBinet(Binet binet) {
		for (int binetNo = 0; binetNo<binets.length; binetNo++) {
			if(binets[binetNo].isEqual(binet)) {
				Binet[] temp = new Binet[binets.length-1];
				for (int newBinetNo = 0; newBinetNo<binetNo; newBinetNo++) {
					
					temp[newBinetNo]=binets[newBinetNo];
				}
				for (int newBinetNo = binetNo; newBinetNo<binets.length-1; newBinetNo++) {
					temp[newBinetNo]=binets[newBinetNo+1];
				}
				binets=temp;
				break;
			}
		}
	}

	public Binet[] sortBinets(Binet[] binetArray) {
		Binet[] sortedBinets = new Binet[binetArray.length];
		int[] sizes = new int[binetArray.length];
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			sizes[binetNo] = binetArray[binetNo].getNodeArray().length;
		}
		Arrays.sort(sizes);
		int[] temp = new int[sizes.length];
		for (int element = 0; element<sizes.length; element++) {
			temp[element] = sizes[sizes.length-element-1];
		}
		sizes = temp;
		for (int binetNo = 0; binetNo<binetArray.length; binetNo++) {
			for (int element = 0; element<sizes.length; element++) {
				if ( binetArray[binetNo].getNodeArray().length==sizes[element]) {
					if (sortedBinets[element]==null) {
						sortedBinets[element] = binetArray[binetNo];
						break;
					}
				}
			}
		}
		return sortedBinets;
	}
	
	public void addReticulation(char[] nodes) {
		if (!containsReticulation(nodes)) {
			for (int reticulationNo = 0; reticulationNo < reticulations.length; reticulationNo++) {
				if (reticulations[reticulationNo]==null) {
					reticulations[reticulationNo]=nodes;
					return;
				}
			}
		}		
	}
	
	public boolean containsReticulation(char[] nodes) {
		for (int reticulationNo = 0; reticulationNo < reticulations.length; reticulationNo++) {
			if (reticulations[reticulationNo]==null) {
				return false;
			}
			if (equalsArray(reticulations[reticulationNo], nodes)) {
				return true;
			}
		}
		return false;
	}
	
	public int getReticulationNo(char[] nodes) {
		if (containsReticulation(nodes)) {
			for (int reticulationNo = 0; reticulationNo < reticulations.length; reticulationNo++) {
				if (equalsArray(reticulations[reticulationNo], nodes)) {
					return reticulationNo;
				}
			}
		}
		return -1;
	}
	
	public boolean equalsArray(char[] array, char[] nodes) {
		boolean[] nodeInArray = new boolean[nodes.length];
		for (int nodeNo = 0; nodeNo<nodes.length; nodeNo++) {
			nodeInArray[nodeNo] = containsNode(array, nodes[nodeNo]);
		}
		boolean[] arrayInNode = new boolean[array.length];
		for (int nodeNo = 0; nodeNo<array.length; nodeNo++) {
			arrayInNode[nodeNo] = containsNode(nodes, array[nodeNo]);
		}
		return allTrue(nodeInArray) && allTrue(arrayInNode);
	}
	
	public boolean containsNode(char[] array, char node) {
		for (int nodeNo = 0; nodeNo<array.length;  nodeNo++) {
			if (array[nodeNo] == node) {
				return true;
			}
		}
		return false;
	}
	
	public boolean allTrue(boolean[] values) {
		boolean answer = true;
		for (int valueNo = 0; valueNo<values.length; valueNo++) {
			if (values[valueNo]==false) {
				answer = false;
			}
		}
		return answer;
				
	}
}
