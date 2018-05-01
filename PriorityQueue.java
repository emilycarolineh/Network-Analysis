//Emily Higgs

public class PriorityQueue {

	private int numberOfVertices;
	private int nextIndex;
	private Edge[] heap;


	public PriorityQueue(int vertices) {
		nextIndex = 0;
		numberOfVertices = vertices;
		heap = new Edge[(numberOfVertices * (numberOfVertices-1)) * 2];

		
	}

	//Add an edge to the priority queue. 
	public void add(Edge newEdge) {

		//If there is not enough room in the heap, then resize it. 
		if(nextIndex == heap.length)
		{
			resize();
		}

		//Place the newest heapObj in the next available index initially. 
		heap[nextIndex] = newEdge;

		//Swim the car up to its proper place in the heap.
		swim();

		//Increase the next available index for any future adds. 
		nextIndex++;
		
		return;
	}

	//To maintain the heap property, swim an added element up to its proper index. 
	private void swim() {
			
			//If this is the first element being added, there is no parent. Just return.
			if(nextIndex == 0)
				return;

			//Get the index of the current node's parent.
			int parent = (int)Math.floor(nextIndex / 2.0);

			swimTime(parent, nextIndex);

			return;
	}

	//Recursive method that uses price to restore heap property. 
	private void swimTime(int parent, int insertionPoint) {
		
		//Base case: If the parent index has run off the front of the array, return. 
		if(parent < 0)
			return;

		//Grab both the current car and its parent's prices. 
		float parentTime = heap[parent].getTime();
		float insertionTime = heap[insertionPoint].getTime();


		if(parentTime > insertionTime) {
			//Swap the objects. 
			Edge tempEdge = heap[parent];
			heap[parent] = heap[insertionPoint];
			heap[insertionPoint] = tempEdge;

			//Focus on the new spot where the insertion lives.  
			insertionPoint = parent;

			//Calculate the new parent. 
			int newParent = (int)Math.floor(insertionPoint / 2.0);


			//Recurse on these new indices. 
			swimTime(newParent, insertionPoint);
		}
	}

	//Remove a particular edge from consideration.
	public void remove(int index) {

		//If there is only one edge in the PQ, just delete it and return. 
		if((nextIndex - 1) == index) {
			nextIndex--;
			return;
		}
			
		//Swap the edge to be removed with the one in the last occupied index.
		nextIndex--;

		Edge tempEdge = heap[index];
		heap[index] = heap[nextIndex];
		heap[nextIndex] = tempEdge;

		//(At this point, nextIndex is the edge to be excluded, so consider it an empty cell.)


		//Sink the other swapped edge down, to maintain heap property. 
		sinkTime(index);

		return;
	}

	//Sink a particular entry based on its price attribute. This maintains the heap property. 
	private void sinkTime(int index) {
		if((index * 2 + 1) >=  nextIndex)
			return;

		int tempIndex = index * 2 + 1;
		float tempTime = heap[tempIndex].getTime();
		

		//If a node has two children, store the lowest of their weights.  
		if((index * 2 + 2) < nextIndex) {
			if(tempTime > heap[index * 2 + 2].getTime()) {
				tempIndex++;
				tempTime = heap[tempIndex].getTime();
			}
		}

		//If a child node has a lower price, swap the current and child nodes. 
		if(tempTime < heap[index].getTime()) {
			Edge tempEdge = heap[tempIndex];
			heap[tempIndex] = heap[index];
			heap[index] = tempEdge;


			//Recurse on the node swapped into.  
			sinkTime(tempIndex);
		}
	}


	public Edge getMinimumEdge() {
	
		return heap[0];
	}

	public boolean isEmpty() {
		if(nextIndex == 0)
			return true;
		else 
			return false;
	}

	//Return an edge at a particular index.
	public Edge accessEdge(int index) {
		return heap[index];
	}

	//If the array backing the heap is full, resize it. 
	private void resize() {
		int newSize = heap.length * 2;
		Edge[] newHeap = new Edge[newSize];


		//For every car in the heap, copy it over to the expanded heap. 
		for(int i = 0; i < heap.length; i++) {
			newHeap[i] = heap[i];
		}

		heap = newHeap;

		return;
	}
}
