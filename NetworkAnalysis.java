//Emily Higgs

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.lang.Math; 

public class NetworkAnalysis {

	//Constants for the cable speeds, in m/s. 
	static final int copperSpeed = 230000000;
	static final int opticSpeed = 200000000;

	private static Graph networkGraph; 
	private static int[] via;
	private static int[] seen;
	private static Edge[] bestEdges;

	//The input scanner
	private static Scanner input = new Scanner(System.in);
	
	public static void main(String args[]) throws FileNotFoundException {
		//Read in provided file, passing along the string containing its name.
		readInFile(args[0]);


		int option = -1;

        while (option != 0) {
            System.out.println();
            System.out.println("Network Analysis Menu");
            System.out.println("1. Lowest latency path");
            System.out.println("2. Copper-only connectivity");
            System.out.println("3. Maximum data that can pass from one vertex to another");
            System.out.println("4. Lowest average latency spanning tree");
            System.out.println("5. Would the graph survive with two vertex failures?");
            System.out.println("0. Quit");
            System.out.print("Selection: ");

            try {
                option = input.nextInt();
            } catch (NoSuchElementException e) {
                option = -1;
            } catch (IllegalStateException e) {
                option = -1;
            }
            input.nextLine();

            switch (option) {
                case 1:
                    lowestPath();
                    break;
                case 2:
                    copperOnly();
                    break;
                case 3:
                    maxData();
                    break;
                case 4:
                    lowestAverageTree();
                    break;
                case 5:
                    twoFailures();
                    break;
                case 0:
                    return;
                default:
                    // Invalid, just ignore and let loop again
                    break;
            }
        }
	
	}
	protected static void lowestPath() {
		int numberOfVertices = networkGraph.getNumberOfVertices();
			
		//Create the priority queue.
		PriorityQueue pq = new PriorityQueue(numberOfVertices);

		//Create an array to track where the vertex that was used to access the others.
		int[] parent = new int[numberOfVertices];
		Edge[] bestEdge = new Edge[numberOfVertices];
		int[] seen_ = new int[numberOfVertices];

		for(int i = 0; i < numberOfVertices; i++) {
			seen_[i] = 0;
			parent[i] = -2;
		}

		System.out.println();

		//Prompt the user for the starting and ending vertices.
		System.out.println("Which vertex will be the source? ");
		int source = input.nextInt();
		System.out.println("Which vertex will be the destination? ");
		int end = input.nextInt();

		//Input check- make sure the user has entered valid vertices.
		if(source > (numberOfVertices - 1) || end > (numberOfVertices - 1) || source < 0 || end < 0) {
			System.out.println("One or both of the vertices entered are invalid!");
			return;
		}
		if(source == end) {
			System.out.println("You don't need an edge to go from a vertex to itself.");
			return;
		}

		//Add all of the source's edges to the PQ.
		Edge curr = networkGraph.getFirstEdge(source);
		Edge newEdge;


		if(curr == null) {
			System.out.println("The source is not connected to any other vertices. There is no lowest latency path.");
		}

		//Add all of the source's edges to the PQ.
		while(curr != null) {
			newEdge = new Edge(curr.getVertexA(), curr.getVertexB(), curr.getFlux(), curr.getBandwidth(), curr.getSpeed(), curr.getLength(), null, 0);
			pq.add(newEdge);


			curr = curr.getNext();
		}
		
		//Set the source's via to -1, a value that denotes it is the source.
		parent[source] = -1;
		seen_[source] = 1;
		bestEdge[source] = new Edge(source, source, 0, 0, 10, 0, null, 0.0f);

		//In the worst case, all edges need to be evaluated. 
		while(seen_[end] == 0) {

			//Dequeue the edge with the minimum latency. 
			newEdge = pq.getMinimumEdge();

			pq.remove(0);

			//If we have not seen this vertex before or if we found a better path to this vertex:
			if(seen_[newEdge.getVertexB()] == 0 || bestEdge[newEdge.getVertexB()].getTime() > newEdge.getTime() + bestEdge[newEdge.getVertexA()].getTime()) {

				seen_[newEdge.getVertexB()] = 1;

				float pathTime;

				if(bestEdge[newEdge.getVertexA()] != null)
					pathTime = bestEdge[newEdge.getVertexA()].getTime();
				else
					pathTime = 0.0f;

				newEdge.setTime(newEdge.getTime() + bestEdge[newEdge.getVertexA()].getTime());
				bestEdge[newEdge.getVertexB()] = new Edge(newEdge.getVertexA(), newEdge.getVertexB(), newEdge.getFlux(), 
				newEdge.getBandwidth(), newEdge.getSpeed(), newEdge.getLength(), null, pathTime);


				//Set the new vertex's via array entry. 
				parent[newEdge.getVertexB()] = newEdge.getVertexA();

				curr = networkGraph.getFirstEdge(newEdge.getVertexB());

				while(curr != null) { 
					pq.add(new Edge(curr.getVertexA(), curr.getVertexB(), curr.getFlux(), curr.getBandwidth(), curr.getSpeed(), curr.getLength(), null, 
					bestEdge[curr.getVertexA()].getTime()));

					curr = curr.getNext();
				}
			}
		}
		

		int placeInList = 0;
		int[] list = new int[numberOfVertices];
		int minimumBandwidth = 70000000;
		int currVertex = end;

		//Backtrack from end to source, noting the path and finding the minimum bandwidth.
		while(parent[currVertex] > -1) {
			if(bestEdge[currVertex].getBandwidth() < minimumBandwidth)
				minimumBandwidth = bestEdge[currVertex].getBandwidth();

			list[placeInList] = currVertex;
			placeInList++;
			currVertex = bestEdge[currVertex].getVertexA();

		}
		if(parent[currVertex] == -2) {
			System.out.print("The two vertices are not connected!");
			return;
		}
		list[placeInList] = currVertex;

		//Print out the lowest latency path.
		System.out.print("The edges that comprise the lowest latency path: ");
		for(int j = placeInList; j >= 0; j--) {
			System.out.print(list[j] + " ");
		}
		System.out.println();
		System.out.println("The bandwidth available along this path is " + minimumBandwidth + "megabits per second");

		return;
		
	}
	protected static void copperOnly() {
		int numberOfVertices = networkGraph.getNumberOfVertices();

		//Create the priority queue.
		PriorityQueue pq = new PriorityQueue(numberOfVertices);

		//Create an array to track storing the path.
		int[] seen_ = new int[numberOfVertices];

		for(int i = 0; i < numberOfVertices; i++)
			seen_[i] = 0;

		//Pick an arbitrary starting point.
		int source = 0;
		seen_[source] = 1;

		//Add all of the source's edges to the PQ.
		Edge curr = networkGraph.getFirstEdge(source);
		Edge newEdge;

		System.out.println();


		//If there are no edges out of this vertex, it is disconnected from all others.
		if(curr == null) {
			System.out.println("The graph is not connected.");
			return;
		}

		while(curr != null) {

			//If the edge in question is connected with copper (speed == copperSpeed), add the edge to the pq.
			if(curr.getSpeed() == copperSpeed) {
				newEdge = new Edge(curr.getVertexA(), curr.getVertexB(), curr.getFlux(), curr.getBandwidth(), curr.getSpeed(), curr.getLength(), null, 0.0f);
				pq.add(newEdge);

			}

			curr = curr.getNext();
		}

		//All vertices need to be evaluated. 
		for(int i = 0; i < numberOfVertices - 1; i++) {

			//Dequeue the edge with the minimum latency. 
			newEdge = pq.getMinimumEdge();

			if(newEdge == null)
				break;

			if(seen_[newEdge.getVertexB()] == 1 || newEdge.getSpeed() != copperSpeed) {
				pq.remove(0);
			}
			else {

				pq.remove(0);

				//Mark the edge as seen.
				seen_[newEdge.getVertexB()] = 1;

				curr = networkGraph.getFirstEdge(newEdge.getVertexB());

				//If there are no edges out of this vertex, it is disconnected from all others.
				if(curr == null) {
					System.out.println("The graph is not connected.");
					return;
				}

				while(curr != null) {

					//If the vertex has yet to be discovered and it is connected with copper, add the edge to the pq.
					if(seen_[curr.getVertexB()] == 0 && curr.getSpeed() == copperSpeed) {

						pq.add(new Edge(curr.getVertexA(), curr.getVertexB(), curr.getFlux(), curr.getBandwidth(), curr.getSpeed(), 
						curr.getLength(), null, 0));
					}

					curr = curr.getNext();
				}
			}
		}

		//If any vertex has gone unvisited, the graph is not connected.
		for(int i = 0; i < numberOfVertices; i++) {
			if(seen_[i] == 0) {
				System.out.println("The graph is not connected with only copper wires.");
				return;
			}
		}
		
		System.out.println("The graph is copper-only connected.");

		return;
	}
	protected static void maxData() {

		int numberOfVertices = networkGraph.getNumberOfVertices();


		//Prompt the user for the starting and ending vertices.
		System.out.println("Which vertex will be the source? ");
		int source = input.nextInt();
		System.out.println("Which vertex will be the destination? ");
		int end = input.nextInt();

		//Input check- make sure the user has entered valid vertices.
		if(source > (numberOfVertices - 1) || end > (numberOfVertices - 1) || source < 0 || end < 0 || source == end) {
			System.out.println("One or both of the vertices entered are invalid!");
			return;
		}

		
		//Grab a copy of the network graph.
		Graph residualGraph = copyNetworkGraph(numberOfVertices);

		int minPathFlow;
		int maxFlowInTotal = 0;
		int vertexB;
		int vertexA;
		int residual;
		int BCDifference;
		Edge tempEdge;
		Edge pairEdge;

		//While there is an augmenting path...
		while(BFSHelper(source, end, residualGraph)) {
			minPathFlow = -1;
			vertexB = end;
			
			//Find the smallest residual value along that path.
			while(via[vertexB] != -1) {

				tempEdge = bestEdges[vertexB];

				if(tempEdge == null) {
					return;
				}

				residual = tempEdge.getCapacity() - tempEdge.getFlux();
				
				if(minPathFlow == -1)
					minPathFlow = residual;
				else
					minPathFlow = Math.min(minPathFlow, residual);
				
				vertexB = via[vertexB];
			}

			vertexB = end;

			//Update flux and capacity for "forward" and "backward" edges.
			while(via[vertexB] != -1) {
				vertexA = via[vertexB];


				tempEdge = bestEdges[vertexB];


				//Find tempEdge's pair edge (A,B -> B,A)
				pairEdge = residualGraph.getFirstEdge(vertexB);

				while(pairEdge.getVertexB() != vertexA) {
					pairEdge = pairEdge.getNext();	
				}


				//This section of code applies the minimum residual to an edge, based on its capacity and bandwidth.
				if(tempEdge.getCapacity() > tempEdge.getBandwidth()) {
					BCDifference = tempEdge.getCapacity() - tempEdge.getBandwidth();

					if(minPathFlow > BCDifference) {
						tempEdge.setCapacity(tempEdge.getBandwidth());
						residual = minPathFlow - BCDifference;
						tempEdge.setFlux(residual);

						pairEdge.setFlux(pairEdge.getFlux() - BCDifference);
						pairEdge.setCapacity(pairEdge.getCapacity() + residual);
					}
					else{
						tempEdge.setCapacity(tempEdge.getCapacity() - minPathFlow);
						pairEdge.setFlux(pairEdge.getFlux() - minPathFlow);
					}
				}
				else {
					tempEdge.setFlux(tempEdge.getFlux() + minPathFlow);

					pairEdge.setCapacity(pairEdge.getCapacity() + tempEdge.getFlux());
				
				}
				
				//Move along to the next vertex on that path.
				vertexB = via[vertexB];
			}

			maxFlowInTotal += minPathFlow;

		}

		System.out.println("The maximum amount of data from vertex " + source + " to vertex " + end + " is " + maxFlowInTotal + "megabits per second");
		
		return;
	}
	//Kruskal's algorithm is used to find the MST with the lowest average latency per edge.
	protected static void lowestAverageTree() {
		int numberOfVertices = networkGraph.getNumberOfVertices();
		PriorityQueue pq = new PriorityQueue(numberOfVertices);


		int bestIndex = 0;
		Edge minEdge;
		float latency = 0.0f;

		for(int i = 0; i < numberOfVertices; i++) 
			bestEdges[i] = null;
			
		//Add all edges to a PQ.
		for(int i = 0; i < numberOfVertices; i++) {
			Edge temp = networkGraph.getFirstEdge(i);

			while(temp != null) {
				//Add temp to the pq.
				pq.add(new Edge(temp.getVertexA(), temp.getVertexB(), temp.getFlux(), temp.getBandwidth(), temp.getSpeed(), temp.getLength(), temp.getNext(), 
				0));
				temp = temp.getNext();
			}
		}


		int vertexA;
		int vertexB;

		//Initialize union find structure.
		UnionFind uf = new UnionFind(numberOfVertices);

		outerloop:
		while(!pq.isEmpty()) {
			//Pull the minimum latency edge from the PQ.
			minEdge = pq.getMinimumEdge();
			pq.remove(0);
		
			vertexA = minEdge.getVertexA();
			vertexB = minEdge.getVertexB();
			
			//While that edge creates a cycle, remove the next. 
			while(uf.find(vertexA) == uf.find(vertexB)) {
				minEdge = pq.getMinimumEdge();

				if(pq.isEmpty()) {
					break outerloop;
				}

				pq.remove(0);
		
				vertexA = minEdge.getVertexA();
				vertexB = minEdge.getVertexB();
			}
				

			//Add that edge to the bestEdge[] 
			bestEdges[bestIndex] = new Edge(minEdge.getVertexA(), minEdge.getVertexB(), minEdge.getFlux(), minEdge.getBandwidth(), 
			minEdge.getSpeed(), minEdge.getLength(), null, 0);

			bestIndex++;
			uf.union(vertexA, vertexB);
		}

		//If there is more than one subset, then there is not a single spanning tree.
		if(uf.getSubsetNumber() > 1) {
			System.out.println("There is no single spanning tree for this graph.");
			return;
		}

		System.out.println();
		System.out.println("The spanning tree with the lowest average latency per edge is comprised of the edges: ");
		
		//Print out the vertices in the MST and add up the latency sum.
		for(int i = 0; i < numberOfVertices; i++) {
			if(bestEdges[i] != null) {
				System.out.println(bestEdges[i].getVertexA() + " " + bestEdges[i].getVertexB());
				latency += bestEdges[i].getTime();
			}
		}

		//Calculate average latency.
		latency = latency/(float)(numberOfVertices-1);

		System.out.println("The average latency per edge is " + latency);
	}
	protected static void twoFailures() {
		System.out.println();
		int numberOfVertices = networkGraph.getNumberOfVertices();

		Graph copyGraph = copyNetworkGraph(numberOfVertices);

		//Find articulation points for the graph. 
		ArticulationClass ac = new ArticulationClass(numberOfVertices, copyGraph, 0);
		int articulationPoints = ac.findArticulation();

		//If the graph has any articulation points when no vertices are removed, it won't hold up when one arbitrary vertex is removed.
		if(articulationPoints > 0) {
			System.out.println("This graph could not survive one arbitrary vertex failure, much less two.");
		}
		else {
			//Remove vertices one by one, then check for articulation points.
			for(int i = 0; i < numberOfVertices; i++) {
			
				//Remove vertex i.
				copyGraph.removeVertex(i);

				int source;

				//If vertex 0 is being removed, it doesn't make sense to use it as the root.
				if(i == 0)
					source = 1;
				else 
					source = 0;

				//Search for articulation points.
				ac = new ArticulationClass(numberOfVertices, copyGraph, source);
				articulationPoints = ac.findArticulation();

				//If an articulation point is found, then this graph won't survive.
				if(articulationPoints > 0) {
					System.out.println("This graph could not survive two arbitrary vertex failures.");
					return;
				}

				//Restore the network graph for the next iteration.
				copyGraph = copyNetworkGraph(numberOfVertices);
			}

			System.out.println("This graph could survive two arbitrary vertex failures.");
		}
		return;
	}
	
	protected static void readInFile(String fileName) throws FileNotFoundException {
		//Create Scanner to read in edges from text file.
        Scanner s = new Scanner(new File(fileName));

		//Read in the number of vertices and build the graph accordingly. 
		int vertices = s.nextInt();
		networkGraph = new Graph(vertices);


		via = new int[vertices];
		seen = new int[vertices];
		bestEdges = new Edge[vertices];

		//The fields for each edge:
		int vertexA;
		int vertexB;
		String type;
		int capacity;
		int speed;
		int length;

		//So long as there is a next line to the file:
		while(s.hasNextLine()) { 

			s.nextLine();

			if(s.hasNextLine()) {
				//Read in fields.
				vertexA = s.nextInt();
				vertexB = s.nextInt();
				type = s.next();
				capacity = s.nextInt();
				length = s.nextInt();


			
				if(type.equals("copper")) {
					speed = copperSpeed;
				}
				else
					speed = opticSpeed;

				//Add edge to the graph.
				networkGraph.addEdge(vertexA, vertexB, 0, capacity, speed, length, null, 0.0f);
			}	
		}

		//Now that the entire file has been read in, close the scanner. 
		s.close();

	}
	//Create a copy of the network graph.
	private static Graph copyNetworkGraph(int numberOfVertices) {
		Graph residualGraph = new Graph(numberOfVertices);
		Edge temp;

		//Create a residual graph. Don't mess up the existing one.
		for(int i = 0; i < numberOfVertices; i++) {
			temp = networkGraph.getFirstEdge(i);

			while(temp != null) {
				//Copy over each vertex.
				residualGraph.addEdge(i, temp.getVertexB(), 0, temp.getCapacity(), temp.getSpeed(), temp.getLength(), null, 0.0f);

				temp = temp.getNext();
			}
		}

		return residualGraph;
	}
	private static boolean BFSHelper (int source, int sink, Graph residualGraph ) {
		//Initialization.
		Edge curr = residualGraph.getFirstEdge(source);
		Queue<Edge> queueEdges = new LinkedList<>();
		Edge newEdge;

		int numberOfVertices = residualGraph.getNumberOfVertices();

		//Clear out via, bestEdges, and seen arrays, just in case.
		for(int i = 0; i < numberOfVertices; i++) {
			seen[i] = 0;
			via[i] = -2;
			bestEdges[i] = null;
		}

		if(curr == null) {
			return false;
		}

		//Add the source's edges. 
		while(curr != null) {
			
			//If there is residual capacity, add the edge to the queue.
			if(curr.getCapacity() - curr.getFlux() > 0) {
				queueEdges.add(curr);
			}

			curr = curr.getNext();
		}
		via[source] = -1;
		seen[source] = 1;

		//In the worst case, all edges need to be evaluated. 
		while(seen[sink] == 0 && queueEdges.peek() != null) {
		
			//Dequeue an edge.
			newEdge = queueEdges.remove();

			
			//Consider this edge. If you haven't seen vertexB and there's residual, update seen, via, and bestEdge arrays.
			if(seen[newEdge.getVertexB()] == 0 && newEdge.getCapacity() - newEdge.getFlux() > 0) {

				float pathTime;

				if(bestEdges[newEdge.getVertexA()] != null)
					pathTime = bestEdges[newEdge.getVertexA()].getTime();
				else
					pathTime = 0.0f;

				seen[newEdge.getVertexB()] = 1;
				via[newEdge.getVertexB()] = newEdge.getVertexA();
				bestEdges[newEdge.getVertexB()] = newEdge;

			}

			curr = residualGraph.getFirstEdge(newEdge.getVertexB());

			//Add all edges related to this vertex.
			while(curr != null) { 
				if(seen[curr.getVertexB()] == 0 && curr.getCapacity() - curr.getFlux() > 0)
					queueEdges.add(curr);
				curr = curr.getNext();
			}
			
		}

		//Has an augmenting path been found?
		if(seen[sink] == 1) {
			return true;
		}
		else 
			return false;
	}	
}
