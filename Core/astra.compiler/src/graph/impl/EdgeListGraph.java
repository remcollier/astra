package graph.impl;

import graph.core.Edge;
import graph.core.Graph;
import graph.core.InvalidVertexException;
import graph.core.Vertex;
import graph.util.LinkedList;
import graph.util.List;
import graph.util.Position;

import java.util.Iterator;



public class EdgeListGraph<V,E> implements Graph<V,E> {
	private class EdgeListVertex implements Vertex<V> {
		Position<EdgeListVertex> position;
		V element;
		
		public EdgeListVertex(V element) {
			this.element = element;
		}
		
		@Override
		public V element() {
			return element;
		}
		
		public String toString() {
			return element.toString();
		}
	}
	
	private class EdgeListEdge implements Edge<E> {
		Position<EdgeListEdge> position;
		E element;
		EdgeListVertex start, end;
		
		public EdgeListEdge(EdgeListVertex start, EdgeListVertex end, E element) {
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
	
	private List<EdgeListVertex> vertices;
	private List<EdgeListEdge> edges;
	
	public EdgeListGraph() {
		vertices = new LinkedList<EdgeListVertex>();
		edges = new LinkedList<EdgeListEdge>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Vertex<V>[] endVertices(Edge<E> e) {
		EdgeListEdge edge = (EdgeListEdge) e;
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
		for (EdgeListEdge edge: edges) {
			if ((edge.start.equals(v)) && (edge.end.equals(w))) return true;
			if ((edge.end.equals(v)) && (edge.start.equals(w))) return true;
		}
		return false;
	}

	@Override
	public V replace(Vertex<V> v, V x) {
		EdgeListVertex vertex = (EdgeListVertex) v;
		V temp = vertex.element;
		vertex.element = x;
		return temp;
	}

	@Override
	public E replace(Edge<E> e, E x) {
		EdgeListEdge edge = (EdgeListEdge) e;
		E temp = edge.element;
		edge.element = x;
		return temp;
	}

	@Override
	public Vertex<V> insertVertex(V v) {
		EdgeListVertex vertex = new EdgeListVertex(v);
		Position<EdgeListVertex> position = vertices.insertLast(vertex);
		vertex.position = position;
		return vertex;
	}

	@Override
	public Edge<E> insertEdge(Vertex<V> v, Vertex<V> w, E o) {
		EdgeListEdge edge = new EdgeListEdge((EdgeListVertex) v, (EdgeListVertex) w, o);
		Position<EdgeListEdge> position = edges.insertLast(edge);
		edge.position = position;
		return edge;
	}

	@Override
	public V removeVertex(Vertex<V> v) {
		Iterator<Edge<E>> it = incidentEdges(v).iterator();
		while (it.hasNext()) it.remove();
		
		EdgeListVertex vertex = (EdgeListVertex) v;
		vertices.remove(vertex.position);
		return vertex.element;
	}

	@Override
	public E removeEdge(Edge<E> e) {
		EdgeListEdge edge = (EdgeListEdge) e;
		edges.remove(edge.position);
		return edge.element;
	}

	@Override
	public List<Edge<E>> incidentEdges(Vertex<V> v) {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		
		for (EdgeListEdge edge : edges) {
			if (edge.start.equals(v)) list.insertLast(edge);
			if (edge.end.equals(v)) list.insertLast(edge);
		}
		
		return list;
	}

	@Override
	public List<Vertex<V>> vertices() {
		LinkedList<Vertex<V>> list = new LinkedList<Vertex<V>>();
		for (EdgeListVertex vertex : vertices) {
			list.insertLast(vertex);
		}
		return list;
	}

	@Override
	public List<Edge<E>> edges() {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		for (EdgeListEdge edge : edges) {
			list.insertLast(edge);
		}
		return list;
	}

}
