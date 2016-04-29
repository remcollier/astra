package graph.algorithm;

import graph.core.AbstractGraphAlgorithm;
import graph.core.Edge;
import graph.core.Graph;
import graph.core.Vertex;
import graph.gui.GraphOverlay;
import graph.util.LinkedList;
import graph.util.Heap;
import graph.util.List;
import graph.util.PriorityQueue;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KruskalMSTAlgorithm<V,E> extends AbstractGraphAlgorithm<V, E> {
	private class KruskalMSTOverlay implements GraphOverlay {
		@Override
		public Color edgeColor(Edge edge) {
			for (Edge<E> e: T) {
				if (e.equals(edge)) return Color.RED;
			}
			return Color.BLACK;
		}

		@Override
		public Color vertexColor(Vertex vertex) {
			return Color.RED;
		}
	}
	
	private Graph<V,E> G;
	private List<Edge<E>> T;
	private Map<Vertex<V>, List<Vertex<V>>> cloudMap;
	private PriorityQueue<Integer, Edge<E>> Q;
	
	public KruskalMSTAlgorithm() {
		super();
	}
	
	public KruskalMSTAlgorithm(Graph<V,E> graph) {
		this();
		G = graph;
	}
	
	public void setGraph(Graph<V,E> graph) {
		G = graph;
	}
	
	public void search(Map<String, Vertex<V>> parameters) {
		T = new LinkedList<Edge<E>>();
		cloudMap = new HashMap<Vertex<V>, List<Vertex<V>>>();
		Q = new Heap<Integer, Edge<E>>();
		for (Vertex<V> vertex: G.vertices()) {
			List<Vertex<V>> list = new LinkedList<Vertex<V>>();
			list.insertLast(vertex);
			cloudMap.put(vertex, list);
		}
		
		for (Edge<E> edge : G.edges()) {
			Q.insert(Integer.parseInt(edge.toString()), edge);
		}
		
		int n = G.vertices().size();

		while (T.size() < n - 1) {
			Edge<E> e = Q.remove();
			Vertex<V>[] endpoints = G.endVertices(e);
			if (!cloudMap.get(endpoints[0]).equals(cloudMap.get(endpoints[1]))) {
				T.insertLast(e);
				Iterator<Vertex<V>> it = cloudMap.get(endpoints[1]).iterator();
				while (!it.hasNext()) {
					cloudMap.get(endpoints[0]).insertLast(it.next());
				}
				cloudMap.put(endpoints[1], cloudMap.get(endpoints[0]));
			}
		}
	}
	
	@Override
	public GraphOverlay getOverlay() {
		return new KruskalMSTOverlay();
	}
}
