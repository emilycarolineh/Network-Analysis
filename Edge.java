public class Edge {

		int vertexA;
		int vertexB;
		int currentFlux;
		int bandwidth;
		int speed;
		int length;
		float time;
		int capacity;
		Edge next;

		public Edge(int A, int B, int flux, int b, int sp, int len, Edge nxt, float t) {
			vertexA = A;
			vertexB = B;
			currentFlux = flux;
			bandwidth = b;
			capacity = bandwidth;
			speed = sp;
			length = len;
			next = nxt;
			time = t + (float)length/(float)speed;
		}

		public int getVertexA() {
			return vertexA;
		}
		public void setVertexA(int A) {
			vertexA = A;
			return;
		}
		public int getVertexB() {
			return vertexB;
		}
		public void setVertexB(int B) {
			vertexB = B;
			return;
		}
		public int getFlux() {
			return currentFlux;
		}
		public void setFlux(int newFlux) {
			currentFlux = newFlux;
			return;
		}
		public int getBandwidth() {
			return bandwidth;
		}
		public void setBandwidth(int b) {
			bandwidth = b;
			return;
		}
		public int getSpeed() {
			return speed;
		}
		public int getLength() {
			return length;
		}
		public Edge getNext() {
			return next;
		}
		public void setTime(float t) {
			time = t;
			return;
		}
		public float getTime() {
			return time;
		}
		public int getCapacity() {
			return capacity;
		}
		public void setCapacity(int cap) {
			capacity = cap;
			return;
		}

		public void setNext(Edge newNext) {
			next = newNext;
			return;
		}

		public float compareTime(Edge compare) {
			return (this.time - compare.time);
		}
	}
