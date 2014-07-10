import java.util.*;
import java.io.*;

public class Router {


	/** 
	* Main Method
	*/
	public static void main(String[] args) {

		// Step 1
		int n = getNumRouters();
		Router me = new Router(n);
		// Steps 2, 3
		me.askForNeighbors();
		me.displayNeighbors();
		// Step 4
		for (int i = 1; i < n; i++) {
			me.askD(0,i);
			me.askL(i);
		}
		me.displayD(0);
		me.displayL();
		// Step 5
		System.out.println("\nDistance Vectors:\n");
		
		for (int i = 1; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j)
					me.askD(i,j);
			}
		}
		for (int i = 0; i < n; i++) {
			me.displayD(i);
		}

		System.out.println();
		System.out.println("Event 1: a change in local link cost to a neighbor of router V0");
		System.out.println("Event 2: receiving a distance vector message from a neighbor of router V0");
		
		do {
			me.event();
			if (me.recompute()) {
				for (int i = 0; i < n; i++) {
					me.displayD(i);
				}
				System.out.println("\nList of neighbors to be notified:");
				me.displayNeighbors();
				System.out.println("\nThe list of the entries in the distance vector D0 to be sent to all the above neighbors:");
				me.displayD(0);
				System.out.println("\nThe list of the entries in the link vector L0:");
				me.displayL();
			}
		} while (prompt("New event? Y/N").toLowerCase().charAt(0) == 'y');
	}

//	public TreeMap<Integer,Integer> D, L, neighbors;
	public int[][] D;
	int[] L, C;
	public int index, total;
	public Router[] routers;

	public Router(int total) {
		//D = new TreeMap<Integer, Integer>();
		//L = new TreeMap<Integer, Integer>();
		//neighbors = new TreeMap<Integer, Integer>();
		D = new int[total][total];
		L = new int[total];
		C = new int[total];
		for (int i = 0; i < total; i++) {
			C[i] = Integer.MAX_VALUE;
		}
		this.total = total;
	}

	public void askForNeighbors() {
		do {
			try {
				int index = Integer.parseInt(prompt("The index of a neighboring router:"));
				if (index >= total) {
					System.out.println("Please enter an ZERO-INDEXED index for a router less than "+total);
					askForNeighbors();
					return;
				}
				int cost = Integer.parseInt(prompt("The cost over the link to this neighboring router"));
				C[index] = cost;
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
				continue;
			}
		} while (prompt("Add another neighbor? (Y/N):").toLowerCase().charAt(0) == 'y');

	}

	public void displayNeighbors() {
		System.out.println("Neighbor\tLink Cost");
		for (int i = 0; i < C.length; i++) {
			if (C[i] != Integer.MAX_VALUE)
				System.out.println(i + "\t\t" + C[i]);
		}
	}	

	public void askD(int from, int to) {
		try {
			int leastCost = Integer.parseInt(prompt("The least cost from router V"+from+" to router V"+to+", D"+from+"("+to+"):"));
			D[from][to] = leastCost;
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid number");
			askD(from,to);
		}
	}

	public void askL(int i) {
		try {
			int neighbor = Integer.parseInt(prompt("The neighboring node achieving such least cost from router V"+index+" to router V"+i+", L"+index+"("+i+"):"));
			if (C[neighbor] == Integer.MAX_VALUE) {
				System.out.println("Invalid router number");
				askL(i);
				return;
			}
			L[i] = neighbor;
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid number");
			askL(i);
		}
	}

	public void displayD(int index) {
		System.out.print("D"+index+" = [");
		for (int i = 0; i < D.length; i++) {
			System.out.print(" "+D[index][i]);
			if (i == D.length - 1) {
				System.out.print(",");
			}
			System.out.print(" ");
		}
		System.out.println("]");
	}

	public void displayL() {
		System.out.print("L"+index+" = [");
		for (int i = 0; i < L.length; i++) {
		
			System.out.print(" ("+index+","+L[i]+")");
			if (i == L.length - 1) {
				System.out.print(",");
			}
			System.out.print(" ");
		}
		System.out.println("]");
	}
	
	public void event() {
		switch (prompt("Which event? (1 or 2):").charAt(0)) {
			case '1': event1(); break;
			case '2': event2(); break;
			default: event(); 
		}
	}

	public void event1() {
		try {
			int i = Integer.parseInt(prompt("The index of this neighboring router:"));
			if (i >= this.total || i < 0 || C[i] == Integer.MAX_VALUE) {
				System.out.println("Invalid neighbor ID");
				event1();
				return;
			}
			int cost = Integer.parseInt(prompt("The new link cost to this neighboring router:"));
			C[i] = cost;
		} catch (NumberFormatException e) {
			System.out.println("Please enter a vaid number");
			event1();
		}
	}

	public void event2() {
		try {
			int neighbor = Integer.parseInt(prompt("The index of the neighbor from which the distance vector message is received:"));
			if (neighbor >= this.total || neighbor < 0 || C[neighbor] == Integer.MAX_VALUE) {
				System.out.println("Inalid router ID. Please select a valid neighbor.");
				event2();
				return;
			}
			for (int i = 0; i < this.total; i++) {
				if (i != neighbor) {
					askD(neighbor, i);
				}

			}
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid number");				
			event2();
		}
	}


	public boolean recompute() {
		boolean different = false;
		for (int i = 1; i < C.length; i++) { 
			int old = D[0][i];
			int min = Integer.MAX_VALUE;
			for (int j = 0; j < D.length; j++) {
				int c = C[j];
				int d = D[j][i];
				System.out.println("I"+i+"j"+j+"C:"+c+" D:"+d);
				if (c == Integer.MAX_VALUE || d == Integer.MAX_VALUE) continue;
				if (c + d < min) {
					System.out.println("Min:"+(c+d));
					min = c + d;
					L[i] = j;
				}
				//}
			}
			if (min == Integer.MAX_VALUE) {
				different = false;
			}
			D[0][i] = min;
			different = min != old;
		}
		return different;
	}



	/*** Static Methods **/


	public static int getNumRouters() {
		int numRouters = 0;

		while (numRouters == 0) {
			try {
				numRouters = Integer.parseInt(prompt("Please enter the total number of routers in the network:"));
				if (numRouters < 2) {
					numRouters = 0;
					System.out.println("Network must have at least two routers.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number");
			}
		}

		return numRouters;
	}
	public static String prompt(String msg) { 
		try {		
			System.out.print(msg+" ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String result = in.readLine();
			System.out.println();
			return result;
		} catch (IOException e) {
			System.out.println("ERROR: IO Exception");
		}
		return null;
	}



}