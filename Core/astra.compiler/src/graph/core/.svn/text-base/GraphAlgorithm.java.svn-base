package graph.core;

import graph.gui.GraphOverlay;
import graph.util.List;

import java.util.Map;

public interface GraphAlgorithm<V, E> {
	public static final int UNEXPLORED										= 0;
	public static final int VISITED											= 1;
	public static final int DISCOVERY										= 2;
	public static final int CROSS											= 3;
	public static final int BACK											= 4;

	public void setGraph(Graph<V,E> graph);
	public List<Parameter> parameterList();
	public void search(Map<String, Vertex<V>> parameters);
	public GraphOverlay getOverlay();
}
