package main;

public class Binet {
	private char[][] nodes = new char[2][];
	private Arc connection;	
	
	public Binet(char[] O, char[] T, int C) {
		nodes[0]=O;
		nodes[1]=T;		
		connection = new Arc(nodes[0],nodes[1],C);
	}
	
	public char[][] getNodes() {
		return nodes;
	}
	
	public Arc getConnection() {
		return connection;
	}
	
	public char[] getNodeArray() {
		char[] nodeArray = new char[nodes[0].length + nodes[1].length];
		int counter = 0;
		for (int componentNo = 0; componentNo<nodes.length; componentNo++) {
			for (int nodeNo = 0; nodeNo<nodes[componentNo].length; nodeNo++) {
				nodeArray[counter] = nodes[componentNo][nodeNo];
				counter++;
			}
		}
		return nodeArray;
	}
	
	public boolean containsNode(char node){
		boolean contains = false;
		for (int componentNo = 0; componentNo<nodes.length; componentNo++) {
			for (int nodeNo = 0; nodeNo<this.getNodes()[componentNo].length; nodeNo++) {
				if (this.getNodes()[componentNo][nodeNo]==node||this.getNodes()[componentNo][nodeNo]==node){
					contains = true;
				}
			}
		}
		return contains;
	}
	
	public String toString() {
		String string = "The left nodes are ";
		for (int left = 0; left<nodes[0].length; left++){
			string+= nodes[0][left] + ", ";
		}
		string += "and the right nodes are "; 
		for (int right = 0; right<nodes[1].length; right++){
			string+= nodes[1][right] + ", ";
		}
		string += "the direction is: " + getConnection().getDirection(); 
		return string;
	}
	
	public boolean isEqual(Binet binet) {
		return (connection.isEqual(binet.getConnection()));
	}
}
