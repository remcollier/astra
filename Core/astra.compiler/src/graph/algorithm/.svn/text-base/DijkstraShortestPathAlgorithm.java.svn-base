package graph.algorithm;

import graph.core.AbstractGraphAlgorithm;
import graph.core.Edge;
import graph.core.Graph;
import graph.core.Parameter;
import graph.core.Vertex;
import graph.gui.GraphOverlay;
import graph.util.Heap;
import graph.util.LinkedList;
import graph.util.List;
import graph.util.Position;
import graph.util.PriorityQueue;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class DijkstraShortestPathAlgorithm<V,E> extends AbstractGraphAlgorithm<V, E> {
	private class DijkstraOverlay implements GraphOverlay {
		@Override
		public Color edgeColor(Edge edge) {
			for (Edge<E> e : spEdgeList) {
				if (edge.equals(e)) return Color.RED;
			}
			return Color.BLACK;
		}

		@Override
		public Color vertexColor(Vertex vertex) {
			for (Vertex<V> v : spVertexList) {
				if (vertex.equals(v)) return Color.RED;
			}
			return Color.BLACK;
		}
	}
	
	private List<Vertex<V>> spVertexList;
	private List<Edge<E>> spEdgeList;
	private Graph<V,E> G;
	private Map<Vertex<V>, Integer> distanceMap;
	private Map<Vertex<V>, Position<Vertex<V>>> positionMap;
	private Map<Vertex<V>, Edge<E>> parentMap;
	
	public DijkstraShortestPathAlgorithm() {
		super();
		distanceMap = new HashMap<Vertex<V>, Integer>();
		positionMap = new HashMap<Vertex<V>, Position<Vertex<V>>>();
		parentMap = new HashMap<Vertex<V>, Edge<E>>();
		addParameter(new Parameter("s","Give the start vertex for the algorithm"));
		addParameter(new Parameter("e","Give the end vertex for the algorithm"));
	}
	
	public DijkstraShortestPathAlgorithm(Graph<V,E> graph) {
		this();
		G = graph;
	}
	
	public void setGraph(Graph<V,E> graph) {
		G = graph;
	}
	
	public void search(Map<String, Vertex<V>> parameters) {
		Vertex<V> s = parameters.get("s");
		
		PriorityQueue<Integer, Vertex<V>> Q = new Heap<Integer, Vertex<V>>();

		spVertexList = new LinkedList<Vertex<V>>();
		spEdgeList = new LinkedList<Edge<E>>();
		
		distanceMap.clear();
		parentMap.clear();
		for (Vertex<V> vertex: G.vertices()) {
			int value = Integer.MAX_VALUE;
			if (vertex.equals(s)) value = 0;
			distanceMap.put(vertex, value);
			Position<Vertex<V>> p = Q.insert(value, vertex);
			positionMap.put(vertex, p);
		}
		
		while (!Q.isEmpty()) {
			Vertex<V> u = Q.remove();
			for (Edge<E> e : G.incidentEdges(u)) {
				Vertex<V> z = G.opposite(u, e);
				int r = distanceMap.get(u) + Integer.parseInt(e.element().toString());
				if (r < distanceMap.get(z)) {
					distanceMap.put(z, r);
					Q.replaceKey(positionMap.get(z), r);
					parentMap.put(z, e);
				}
			}
		}
		
		// Work out shortest path
		// NOTE: This is the only change to the STP algorithm
		Vertex<V> v = parameters.get("e");
		while (!v.equals(s)) {
			spVertexList.insertFirst(v);
			Edge<E> e = parentMap.get(v);
			spEdgeList.insertFirst(e);
			v = G.opposite(v, e);
		}
		spVertexList.insertFirst(v);
	}
	
	@Override
	public GraphOverlay getOverlay() {
		return new DijkstraOverlay();
	}
}
