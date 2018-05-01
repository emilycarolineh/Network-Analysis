//Emily Higgs

import java.util.*;
import java.lang.Math;

public class ArticulationClass {

	private Graph graph;
	private int numberOfVertices;
	private int[] low;					//Array to store the lowest reachable vertex from index.
	private int[] num;					//Array to store the pre-order traversal number of each vertex.
	private int articulation;			//Number of artiulation points.
	private int visitedIndex;			//Counter for visiting vertices.
	private int source;
	
	public ArticulationClass(int number, Graph g, int s) {

		graph = g;
		numberOfVertices = number;
		low = new int[numberOfVertices];	
		num = new int[numberOfVertices];
		articulation = 0;
		visitedIndex = 1;
		source = s;

		//Initialize all num values to zero; no vertices have been visited yet. 
		for(int i = 0; i < numberOfVertices; i++) {
			num[i] = 0;
		}

	}

	public int findArticulation() {
		
		for(int i = 0; i < numberOfVertices; i++) {
			//If a vertex has yet to be visited, call recursive helper.
			if(num[i] == 0)
				recursiveHelper(i);
		}

		return articulation;

	}
	private void recursiveHelper(int vertexA) {

		num[vertexA] = visitedIndex;
		low[vertexA] = visitedIndex;
		visitedIndex++;

		int c = 0; 	//Number of children in the DFS tree traversal.

		Edge placeholder = graph.getFirstEdge(vertexA);

		//Consider all edges leading from this vertex.
		while(placeholder != null) {

			int vertexB = placeholder.getVertexB();

			//If this edge leads to a vertex that has yet to be seen:
			if(vertexB >= 0 && num[vertexB] == 0) {
				c++;

				//Recursively traverse the graph rooted at vertexB.
				recursiveHelper(vertexB);

				//For now, use one spanning tree edge to reach a lower 'low' value.
				low[vertexA] = Math.min(low[vertexA], low[vertexB]);

				//If vertexA roots the tree and it has more than one child, it's an articulation point
				if(vertexA == source && c > 1)
					articulation++;
				else if(vertexA != source && low[vertexB] >= num[vertexA])
					articulation++;
			}
			//If this edge leads to a vertex that has already been seen, this back edge might lower its 'low' value.
			else if(vertexB >= 0){
				low[vertexA] = Math.min(low[vertexA], low[vertexB]);
			}

			placeholder = placeholder.getNext();
		}
	}
}

