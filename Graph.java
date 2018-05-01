//Emily Higgs

public class Graph {

	int numberOfVertices;
	Edge[] adjacencyList;

	public Graph(int number) {
		numberOfVertices = number;

		adjacencyList = new Edge[numberOfVertices];

		for(int i = 0; i < numberOfVertices; i++)
			adjacencyList[i] = null;
	}

	public void addEdge(int A, int B, int flux, int cap, int sp, int len, Edge nxt, float t) {
		
		Edge currentEdge = adjacencyList[A];

		//Find the first/forward edge.
			if(currentEdge == null) {
				adjacencyList[A] = new Edge(A, B, flux, cap, sp, len, nxt, t);
			}
			//Search for the edge in question. 
			else {
				while(currentEdge.getNext() != null) {

					//If this edge has already been added to the list, don't add it again. 
					if(currentEdge.getVertexB() == B)
						return;
					//"Edge" case when creating residual graph. (Haha..)
					else if(currentEdge.getNext() != null && (currentEdge.getNext()).getVertexB() == B)
						return;
					else
						currentEdge = currentEdge.getNext();
				}

				//Add the new edge to the end of the list. 
				currentEdge.setNext(new Edge(A, B, flux, cap, sp, len, null, t));
			}

		
		currentEdge = adjacencyList[B];

		//Find the second/backward edge.
			if(currentEdge == null) {
				adjacencyList[B] = new Edge(B, A, flux, cap, sp, len, null, t);
			}
			//Search for the edge in question. 
			else {
				while(currentEdge.getNext() != null) {
					//If this edge has already been added to the list, don't add it again. 
					if(currentEdge.getVertexB() == B)
						return;
					else
						currentEdge = currentEdge.getNext();
				}

				//Add the new edge to the end of the list. 
				currentEdge.setNext(new Edge(B, A, flux, cap, sp, len, null, t));
			}

		return;
	}
	
	public void removeVertex(int vertexA) {
		
		Edge temp = adjacencyList[vertexA];

		if(temp == null) 
			return;

		while(temp != null) {
			int vertexB = temp.getVertexB();

			//Find and delete the "back edge".
			Edge back = adjacencyList[vertexB];

			//If the back edge is the first one in its list, delete it.
			if(back != null && back.getVertexB() == vertexA)
				adjacencyList[vertexB] = back.getNext();
			//Else, continue searching for the back edge.
			else {
				while(back != null && back.getNext() != null) {
					//If you find the back edge, delete it.
					if(back.getNext().getVertexB() == vertexA) {
						back.setNext((back.getNext()).getNext());
						break;
					}
					back = back.getNext();
				}
			}

			temp = temp.getNext();
			adjacencyList[vertexA] = temp;
		}

		//Create a placeholder edge. The vertexB value of -1 denotes that this vertex no longer exists in the graph.
		adjacencyList[vertexA] = new Edge(vertexA, -1, 0, 0, 0, 0, null, 0.0f);

	}
	public int getNumberOfVertices() {
		return numberOfVertices;
	}
	public Edge getFirstEdge(int vertex) {
		return adjacencyList[vertex];
	}

	public void printContents() {
		for(int i = 0; i < numberOfVertices; i++) {
			Edge placeholder = adjacencyList[i];

			while(placeholder != null) {
				System.out.println(placeholder.getVertexA() + " " + placeholder.getVertexB());

				placeholder = placeholder.getNext();
			}
		}
	}

}
