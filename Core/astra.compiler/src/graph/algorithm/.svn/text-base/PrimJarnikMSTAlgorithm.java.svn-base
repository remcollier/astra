package graph.algorithm;

import graph.core.AbstractGraphAlgorithm;
import graph.core.Edge;
import graph.core.Graph;
import graph.core.Vertex;
import graph.gui.GraphOverlay;
import graph.util.LinkedList;
import graph.util.Heap;
import graph.util.List;
import graph.util.Position;
import graph.util.PriorityQueue;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrimJarnikMSTAlgorithm<V,E> extends AbstractGraphAlgorithm<V, E> {
	private class PrimJarnikMSTOverlay implements GraphOverlay {
		@Override
		public Color edgeColor(Edge edge) {
			for (Edge<E> e : parentMap.values()) {
				if (edge.equals(e)) return Color.RED;
			}
			return Color.BLACK;
		}

		@Override
		public Color vertexColor(Vertex vertex) {
			return Color.RED;
		}
	}
	
	private Graph<V,E> G;
	private PriorityQueue<Integer, Edge<E>> Q;
	private Map<Vertex<V>, Integer> distanceMap;
	private Map<Vertex<V>, Position<Vertex<V>>> positionMap;
	private Map<Vertex<V>, Edge<E>> parentMap;
	
	public PrimJarnikMSTAlgorithm() {
		super();
		distanceMap = new HashMap<Vertex<V>, Integer>();
		positionMap = new HashMap<Vertex<V>, Position<Vertex<V>>>();
		parentMap = new HashMap<Vertex<V>, Edge<E>>();
	}
	
	public PrimJarnikMSTAlgorithm(Graph<V,E> graph) {
		this();
		G = graph;
	}
	
	public void setGraph(Graph<V,E> graph) {
		G = graph;
	}
	
	public void search(Map<String, Vertex<V>> parameters) {
		Vertex<V> s = G.vertices().first().element();
		List<Vertex<V>> cloud = new LinkedList<Vertex<V>>();
		
		PriorityQueue<Integer, Vertex<V>> Q = new Heap<Integer, Vertex<V>>();
		
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
			cloud.insertLast(u);
			for (Edge<E> e : G.incidentEdges(u)) {
				Vertex<V> z = G.opposite(u, e);
				if (!inCloud(cloud, z)) {
					int r = Integer.parseInt(e.element().toString());
					if (r < distanceMap.get(z)) {
						distanceMap.put(z, r);
						parentMap.put(z, e);
						Q.replaceKey(positionMap.get(z), r);
					}
				}
			}
		}
	}
	
	private boolean inCloud(List<Vertex<V>> cloud, Vertex<V> z) {
		for (Vertex<V> vertex : cloud) {
			if (vertex.equals(z)) return true;
		}
		return false;
	}
	
	@Override
	public GraphOverlay getOverlay() {
		return new PrimJarnikMSTOverlay();
	}
}
