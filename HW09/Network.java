/**

Zach Souser
Homework 9
CS 3700
Spring 2014

*/

import java.lang.*;
import java.util.*;
import java.io.*;

public class Network {


	public int numNodes;
	public double[][] C;
	public double[] D;
	public ArrayList<Integer> N;
	public int[] P;
	public ArrayList<Integer[]> Y;
	public Network(int numNodes, double[][] matrix) {
		this.numNodes = numNodes;
		this.C = matrix;
	}

	
	public static void main(String[] args) {
		int n = 0; 
		do {
			try {
				n = Integer.parseInt(prompt("Please enter the number of routers:"));
			} catch (NumberFormatException e) {
				System.out.println("Invalid number provided");
			}
		} while(n < 2);
		BufferedReader file = null;
		double[][] matrix = null;
		do {
			try {
				file = new BufferedReader(
					new InputStreamReader(
						new FileInputStream(
							new File(
								prompt("Please enter a file path for the routing info:")
							)
						)
					)
				);
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			}

			if (file != null) {
				try {
					matrix = initMatrix(n,file);
				} catch (IOException e) {
					System.out.println("ERROR: IO Exception");
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Mismatched Router Count");
					System.exit(1);
				}
			}
			
		} while (matrix == null);
		
		Network net = new Network(n,matrix);
		net.init(0);
		net.loop();
		net.table(0);
	}	

	public static double[][] initMatrix(int n, BufferedReader file) throws IOException {
		double[][] C = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) C[i][j] = Double.POSITIVE_INFINITY;
			}
		}
		int i = 0;
		String line = null;
		while ((line = file.readLine()) != null) {
			
			String[] data = line.split("\t");
			int node1, node2;
			double cost;
			try {
	            node1 = Integer.parseInt(data[0]) - 1;
	            if (node1 > n) {
	                System.out.println("Invalid first node entry in row "+i+". Please use the node's integer index.");
	            	return null;
	            }
	        } catch (NumberFormatException e) {
	        	System.out.println("Invalid first node entry in row "+i+". Please use the node's integer index.");
	            return null;
	        }
	        try {
	            node2 = Integer.parseInt(data[1]) - 1;
	            if (node2 > n) {
	                System.out.println("Invalid second node entry in row "+i+". Please use the node's integer index.");
	                return null;
	            }
	                
	        } catch (NumberFormatException e) {
	        	System.out.println("Invalid first node entry in row "+i+".  Please use the node's integer index.");
	            return null;
	        }
            cost = (double)Double.parseDouble(data[2]);
            try {
            	if (cost <= 0){
                	System.out.println("Invalid cost entry in row "+i+". Please try a number.");
            		return null;
            	}    
	        } catch (NumberFormatException e) {
	        	System.out.println("Invalid first node entry in row "+i+". Please try a number.");
	            return null;
	        }
            C[node1][node2] = cost;
            C[node2][node1] = cost;
		}
		for (int k = 0; k < n; k++) {
			for (int j = 0; j < n; j++) {
				System.out.print(C[k][j] == Double.POSITIVE_INFINITY ? "***\t" : C[k][j] + "\t");
			}
			System.out.println();
		}
		return C;
	}


	public void init(int start) {
		N = new ArrayList<Integer>(); // N'
		N.add((Integer)start);
		Y = new ArrayList<Integer[]>();
		D = new double[numNodes];
		P = new int[numNodes];
		for (int i = 0; i < numNodes; i++) {
			if (C[start][i] < Double.POSITIVE_INFINITY) {
				D[i] = C[start][i];
				P[i] = start;
			} else {
				D[i] = Double.POSITIVE_INFINITY;
			}
		}
		display();
	}

	public void loop() {
		while (N.size() != numNodes) {
			int k = -1;
			for (int i = 0; i < numNodes; i++) {
				if (!N.contains(i)) {
						//System.out.println("--"+D[i]+"-"+D[k]);
					if (k == -1 || D[i] < D[k]) {
						k = i;
					} //else System.out.println("AHHH");
				} //else System.out.println("HAHA");
			}
			N.add(k);
			Y.add(new Integer[] {P[k],k});
			for (int i = 0; i < numNodes; i++) {
				if (C[i][k] < Double.POSITIVE_INFINITY && !N.contains(i)) {
					if (k != i && D[k] + C[k][i] < D[i]) {
						D[i] = D[k] + C[k][i];
						P[i] = k;
					}
				}
			}
			display();
		}
	}

	public void display() {
		System.out.print("N':\t");
		for (int i = 0; i < N.size(); i++) {
			System.out.print(N.get(i)+ "\t");
		}
		System.out.println();
		System.out.print("Y:\t \t");
		for (int i = 0; i < Y.size(); i++) {
			System.out.print("("+((Y.get(i))[0])+","+((Y.get(i))[1])+")\t");
		}
		System.out.println();
		System.out.print("D:\t \t");
		for (int i = 1; i < numNodes; i++) {
			System.out.print(D[i]+"\t");
		}
		System.out.println();
		System.out.print("P:\t \t");
		for (int i = 1; i < numNodes; i++) {
			System.out.print(P[i]+"\t");
		}
		System.out.println();
		System.out.println();
	}

	public void table(int start) {
		for (int i = 0; i < N.size(); i++) {
			if (i != start) {
				int j = i;
				while (P[j] != start) {
					j = P[j];
				}
				System.out.println(i + " | (" + start + ", " + j + ")");
			}
		}
	}

	public static String prompt(String msg) { 
		try {		
			System.out.print(msg);
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