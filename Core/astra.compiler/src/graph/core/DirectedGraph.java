package graph.core;

import java.util.LinkedList;

import astra.dgraph.ASTRANode;

public interface DirectedGraph<V,E> extends Graph<V,E> {
	public boolean isSource(Edge<E> edge, Vertex<V> vertex);
	public boolean isTarget(Edge<E> edge, Vertex<V> vertex);
	public Vertex<V> source(Edge<E> edge);
	public Vertex<V> target(Edge<E> edge);
	public LinkedList<Edge<E>> outEdges(Vertex<V> vertex);
	public LinkedList<Edge<E>> inEdges(Vertex<V> vertex);
}
