package graph.algorithm;

import graph.core.AbstractGraphAlgorithm;
import graph.core.Edge;
import graph.core.Graph;
import graph.core.GraphAlgorithm;
import graph.core.Vertex;
import graph.gui.GraphOverlay;
import graph.util.List;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class DepthFirstSearchAlgorithm<V,E> extends AbstractGraphAlgorithm<V, E> {
	private class BreadthFirstOverlay implements GraphOverlay {
		Map<Integer, Color> colorMap = new HashMap<Integer, Color>();
		
		{
			colorMap.put(GraphAlgorithm.UNEXPLORED, Color.BLACK);
			colorMap.put(GraphAlgorithm.DISCOVERY, Color.RED);
			colorMap.put(GraphAlgorithm.VISITED, Color.RED);
			colorMap.put(GraphAlgorithm.BACK, Color.GREEN);
		}
		
		@Override
		public Color edgeColor(Edge edge) {
			return colorMap.get(edgeLabels.get(edge));
		}

		@Override
		public Color vertexColor(Vertex vertex) {
			return colorMap.get(vertexLabels.get(vertex));
		}
	}
	
	private Graph<V,E> G;
	private Map<Vertex<V>, Integer> vertexLabels;
	private Map<Edge<E>, Integer> edgeLabels;
	
	public DepthFirstSearchAlgorithm() {
		super();
		vertexLabels = new HashMap<Vertex<V>, Integer>();
		edgeLabels = new HashMap<Edge<E>, Integer>();
	}
	
	public DepthFirstSearchAlgorithm(Graph<V,E> graph) {
		this();
		G = graph;
	}
	
	public void setGraph(Graph<V,E> graph) {
		G = graph;
	}
	
	public void search(Map<String, Vertex<V>> parameters) {
		for (Vertex<V> vertex: G.vertices()) {
			vertexLabels.put(vertex, UNEXPLORED);
		}
		for (Edge<E> edge: G.edges()) {
			edgeLabels.put(edge, UNEXPLORED);
		}
		for (Vertex<V> vertex: G.vertices()) {
			if (vertexLabels.get(vertex) == UNEXPLORED) {
				search(vertex);
			}
		}
	}
	
	public void search(Vertex<V> s) {
		vertexLabels.put(s, VISITED);
		
		for (Edge<E> e : G.incidentEdges(s)) {
			if (edgeLabels.get(e) == UNEXPLORED) {
				Vertex<V> w = G.opposite(s, e);
				if (vertexLabels.get(w) == UNEXPLORED) {
					edgeLabels.put(e, DISCOVERY);
					search(w);
				} else {
					edgeLabels.put(e, BACK);
				}
			}
		}
	}

	@Override
	public GraphOverlay getOverlay() {
		return new BreadthFirstOverlay();
	}
}
