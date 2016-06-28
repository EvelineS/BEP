package main;

public class Arc {
	char[][] nodes = new char[2][];
	int direction;
	//direction={0,1}
	//0 is equal, 1 is first over second
	
	public Arc(char[] nodeSet1, char[] nodeSet2, int direct) {
		if (direct==0 || direct==1) {
			nodes[0] = nodeSet1;
			nodes[1] = nodeSet2;
			direction = direct;
		}
	}
	
	public char[][] getNodes() {
		return nodes;
	}
	
	public int getDirection() {
		return direction;
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
		string += "the direction is: " + direction; 
		return string;
	}
	
	public static boolean containsNode(char[] nodeArray, char node) {
		for (int nodeNo=0; nodeNo<nodeArray.length; nodeNo++) {
			if (nodeArray[nodeNo]==node) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equalsLeftSide(char[] nodes) {
		if(nodes.length==this.getNodes()[0].length) {
			for (int node = 0; node<nodes.length; node++) {
				if (!containsNode(this.getNodes()[0],nodes[node])) {
					return false; 
				}
			}
			for (int node = 0; node<this.getNodes()[0].length; node++) {
				if (!containsNode(nodes,this.getNodes()[0][node])) {
					return false; 
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean equalsRightSide(char[] nodes) {
		if(nodes.length==this.getNodes()[1].length) {
			for (int node = 0; node<nodes.length; node++) {
				if (!containsNode(this.getNodes()[1],nodes[node])) {
					return false; 
				}
			}
			for (int node = 0; node<this.getNodes()[1].length; node++) {
				if (!containsNode(nodes,this.getNodes()[1][node])) {
					return false; 
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean isEqual(Arc arc) {
		if (arc.getDirection()==this.getDirection()) {
			if (this.getDirection()==0) {
				return (this.equalsLeftSide(arc.getNodes()[0]) && this.equalsRightSide(arc.getNodes()[1])) || (this.equalsLeftSide(arc.getNodes()[1]) && this.equalsRightSide(arc.getNodes()[0]));
			}
			else {
				return this.equalsLeftSide(arc.getNodes()[0]) && this.equalsRightSide(arc.getNodes()[1]);
			}
		}
		return false;
	}
}
