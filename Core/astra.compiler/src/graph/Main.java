package graph;

import graph.algorithm.BreadthFirstSearchAlgorithm;
import graph.algorithm.DepthFirstSearchAlgorithm;
import graph.algorithm.DijkstraShortestPathAlgorithm;
import graph.algorithm.DijkstraShortestPathTreeAlgorithm;
import graph.algorithm.KruskalMSTAlgorithm;
import graph.algorithm.PrimJarnikMSTAlgorithm;
import graph.gui.JGraphFrame;
import graph.impl.AdjacencyMatrixGraph;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JGraphFrame frame = new JGraphFrame(new AdjacencyMatrixGraph<String, String>(), "weights.gra");
		frame.registerGraphAlgorithm("Complete Breadth First Search Overlay", new BreadthFirstSearchAlgorithm<String, String>());
		frame.registerGraphAlgorithm("Complete Depth First Search Overlay", new DepthFirstSearchAlgorithm<String, String>());
		frame.registerGraphAlgorithm("Dijkstra Shortest Path Tree Overlay", new DijkstraShortestPathTreeAlgorithm<String, String>());
		frame.registerGraphAlgorithm("Dijkstra Shortest Path Overlay", new DijkstraShortestPathAlgorithm<String, String>());
		frame.registerGraphAlgorithm("Kruskal MST Overlay", new KruskalMSTAlgorithm<String, String>());
		frame.registerGraphAlgorithm("Prim Jarnik MST Overlay", new PrimJarnikMSTAlgorithm<String, String>());
		frame.setVisible(true);
	}


}
