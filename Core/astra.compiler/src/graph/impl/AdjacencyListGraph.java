package graph.impl;

import graph.core.Edge;
import graph.core.Graph;
import graph.core.InvalidVertexException;
import graph.core.Vertex;
import graph.util.LinkedList;
import graph.util.List;
import graph.util.Position;

import java.util.Iterator;



public class AdjacencyListGraph<V,E> implements Graph<V,E> {
	protected class AdjacencyListVertex implements Vertex<V> {
		Position<AdjacencyListVertex> position;
		V element;
		List<Edge<E>> incidentEdges;
		
		public AdjacencyListVertex(V element) {
			this.element = element;
			incidentEdges = new LinkedList<Edge<E>>();
		}
		
		@Override
		public V element() {
			return element;
		}
		
		public String toString() {
			return element.toString();
		}
	}
	
	protected class AdjacencyListEdge implements Edge<E> {
		Position<AdjacencyListEdge> position;
		E element;
		AdjacencyListVertex start, end;
		public Position<Edge<E>> startPosition, endPosition;
		
		public AdjacencyListEdge(AdjacencyListVertex start, AdjacencyListVertex end, E element) {
			this.start = start;
			this.end = end;
			this.element = element;
		}
		
		@Override
		public E element() {
			return element;
		}
		
		public String toString() {
			return element.toString();
		}
	}
	
	private List<AdjacencyListVertex> vertices;
	private List<AdjacencyListEdge> edges;
	
	public AdjacencyListGraph() {
		vertices = new LinkedList<AdjacencyListVertex>();
		edges = new LinkedList<AdjacencyListEdge>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Vertex<V>[] endVertices(Edge<E> e) {
		AdjacencyListEdge edge = (AdjacencyListEdge) e;
		Vertex<V>[] endpoints = (Vertex<V>[]) new Vertex[2];
		endpoints[0] = edge.start;
		endpoints[1] = edge.end;
		return endpoints;
	}

	@Override
	public Vertex<V> opposite(Vertex<V> v, Edge<E> e) {
		Vertex<V>[] endpoints = endVertices(e);
		if (endpoints[0].equals(v)) {
			return endpoints[1];
		} else if (endpoints[1].equals(v)) {
			return endpoints[0];
		}
		throw new InvalidVertexException();
	}

	@Override
	public boolean areAdjacent(Vertex<V> v, Vertex<V> w) {
		for (AdjacencyListEdge edge: edges) {
			if ((edge.start.equals(v)) && (edge.end.equals(w))) return true;
			if ((edge.end.equals(v)) && (edge.start.equals(w))) return true;
		}
		return false;
	}

	@Override
	public V replace(Vertex<V> v, V x) {
		AdjacencyListVertex vertex = (AdjacencyListVertex) v;
		V temp = vertex.element;
		vertex.element = x;
		return temp;
	}

	@Override
	public E replace(Edge<E> e, E x) {
		AdjacencyListEdge edge = (AdjacencyListEdge) e;
		E temp = edge.element;
		edge.element = x;
		return temp;
	}

	@Override
	public Vertex<V> insertVertex(V v) {
		AdjacencyListVertex vertex = new AdjacencyListVertex(v);
		Position<AdjacencyListVertex> position = vertices.insertLast(vertex);
		vertex.position = position;
		return vertex;
	}

	@Override
	public Edge<E> insertEdge(Vertex<V> v, Vertex<V> w, E o) {
		AdjacencyListEdge edge = new AdjacencyListEdge((AdjacencyListVertex) v, (AdjacencyListVertex) w, o);
		Position<AdjacencyListEdge> position = edges.insertLast(edge);
		edge.position = position;
		position.element().startPosition = ((AdjacencyListVertex) v).incidentEdges.insertLast(position.element());
		position.element().endPosition = ((AdjacencyListVertex) w).incidentEdges.insertLast(position.element());
		return edge;
	}

	@Override
	public V removeVertex(Vertex<V> v) {
		AdjacencyListVertex vertex = (AdjacencyListVertex) v;
		if (vertex.position == null) return null;
		
		Iterator<Edge<E>> it = incidentEdges(v).iterator();
		while (it.hasNext()) it.remove();
		
		vertices.remove(vertex.position);
		vertex.position = null;
		return vertex.element;
	}

	@Override
	public E removeEdge(Edge<E> e) {
		AdjacencyListEdge edge = (AdjacencyListEdge) e;
		edge.start.incidentEdges.remove(edge.startPosition);
		edge.end.incidentEdges.remove(edge.endPosition);
		edges.remove(edge.position);
		return edge.element;
	}

	@Override
	public List<Edge<E>> incidentEdges(Vertex<V> v) {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		
		for (Edge<E> edge : ((AdjacencyListVertex) v).incidentEdges) {
			list.insertLast(edge);
		}
		
		return list;
	}

	@Override
	public List<Vertex<V>> vertices() {
		LinkedList<Vertex<V>> list = new LinkedList<Vertex<V>>();
		for (AdjacencyListVertex vertex : vertices) {
			list.insertLast(vertex);
		}
		return list;
	}

	@Override
	public List<Edge<E>> edges() {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		for (AdjacencyListEdge edge : edges) {
			list.insertLast(edge);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		String out = "";
		for (AdjacencyListVertex vertex : vertices) {
			out += vertex.toString() + " [";
			boolean first = true;
			for (Edge edge : incidentEdges(vertex)) {
				if (first) first=false; else out += ", ";
				AdjacencyListEdge e = (AdjacencyListEdge) edge;
				if (e.start == vertex) {
					out += " -> " + e.end.element();
				} else {
					out += " <- " + e.start.element();
				}
			}
			out +=" ]\n";
		}
		return out;
	}
}
