//Emily Higgs
public class UnionFind {

	private int numberOfSubsets;
	private int[] id;
	private int[] sz;


	public UnionFind(int vertices) {

		numberOfSubsets = vertices;
		id = new int[vertices];
		sz = new int[vertices];
		for (int i = 0; i < vertices; i++) { 
			id[i] = i; 
			sz[i] = 1; 
		}
	}

	public int getSubsetNumber() {
		return numberOfSubsets;
	}

	public void union(int p, int q) {
		int i = find(p);
		int j = find(q);

		if (i == j) 
			return;

		if (sz[i] < sz[j]) { 
			id[i] = j; 
			sz[j] += sz[i]; 
		}
		else { 
			id[j] = i; 
			sz[i] += sz[j]; 
		}

		numberOfSubsets--;
	}

	public int find(int p) {
		while (p != id[p]) 
			p = id[p];

		return p;
	}
}
