package graph.impl;

import java.util.LinkedList;

import graph.core.DirectedGraph;
import graph.core.Edge;
import graph.core.Vertex;

public class DirectedAdjacencyListGraph<V,E> extends AdjacencyListGraph<V,E> implements DirectedGraph<V,E> {
	@Override
	public boolean isSource(Edge<E> edge, Vertex<V> vertex) {
		return ((AdjacencyListEdge) edge).start == vertex;
	}

	@Override
	public boolean isTarget(Edge<E> edge, Vertex<V> vertex) {
		return ((AdjacencyListEdge) edge).end == vertex;
	}

	@Override
	public Vertex<V> source(Edge<E> edge) {
		return ((AdjacencyListEdge) edge).start;
	}
	
	@Override
	public Vertex<V> target(Edge<E> edge) {
		return ((AdjacencyListEdge) edge).end;
	}

	@Override
	public LinkedList<Edge<E>> outEdges(Vertex<V> vertex) {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		for (Edge<E> edge : incidentEdges(vertex)) {
			if (isSource(edge, vertex)) {
				list.add(edge);
			}
		}
		return list;
	}

	@Override
	public LinkedList<Edge<E>> inEdges(Vertex<V> vertex) {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		for (Edge<E> edge : incidentEdges(vertex)) {
			if (isTarget(edge, vertex)) {
				list.add(edge);
			}
		}
		return list;
	}

}
