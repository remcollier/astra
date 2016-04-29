package graph.impl;

import graph.core.Edge;
import graph.core.Graph;
import graph.core.InvalidVertexException;
import graph.core.Vertex;
import graph.util.LinkedList;
import graph.util.List;
import graph.util.Position;

import java.util.Iterator;



public class AdjacencyMatrixGraph<V,E> implements Graph<V,E> {
	private class AdjacencyMatrixVertex implements Vertex<V> {
		Position<AdjacencyMatrixVertex> position;
		V element;
		int index;
		
		public AdjacencyMatrixVertex(V element, int index) {
			this.element = element;
			this.index = index;
		}
		
		@Override
		public V element() {
			return element;
		}
		
		public String toString() {
			return "{" + element.toString() + ", " + index + "}";
		}
	}
	
	private class AdjacencyMatrixEdge implements Edge<E> {
		Position<AdjacencyMatrixEdge> position;
		E element;
		AdjacencyMatrixVertex start, end;
		
		public AdjacencyMatrixEdge(AdjacencyMatrixVertex start, AdjacencyMatrixVertex end, E element) {
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

	private Edge[][] matrix = new Edge[10][10];
	
	private List<AdjacencyMatrixVertex> vertices;
	private List<AdjacencyMatrixEdge> edges;
	
	public AdjacencyMatrixGraph() {
		vertices = new LinkedList<AdjacencyMatrixVertex>();
		edges = new LinkedList<AdjacencyMatrixEdge>();
	}
	
	private void doubleMatrix() {
		Edge[][] temp = new Edge[matrix.length*2][matrix.length*2];
		for (int i=0;i<matrix.length; i++) {
			System.arraycopy(matrix[i], 0, temp[i], 0, matrix.length);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Vertex<V>[] endVertices(Edge<E> e) {
		AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) e;
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
		for (AdjacencyMatrixEdge edge: edges) {
			if ((edge.start.equals(v)) && (edge.end.equals(w))) return true;
			if ((edge.end.equals(v)) && (edge.start.equals(w))) return true;
		}
		return false;
	}

	@Override
	public V replace(Vertex<V> v, V x) {
		AdjacencyMatrixVertex vertex = (AdjacencyMatrixVertex) v;
		V temp = vertex.element;
		vertex.element = x;
		return temp;
	}

	@Override
	public E replace(Edge<E> e, E x) {
		AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) e;
		E temp = edge.element;
		edge.element = x;
		return temp;
	}

	@Override
	public Vertex<V> insertVertex(V v) {
		AdjacencyMatrixVertex vertex = new AdjacencyMatrixVertex(v, vertices.size());
		Position<AdjacencyMatrixVertex> position = vertices.insertLast(vertex);
		vertex.position = position;
		return vertex;
	}

	@Override
	public Edge<E> insertEdge(Vertex<V> v, Vertex<V> w, E o) {
		AdjacencyMatrixEdge edge = new AdjacencyMatrixEdge((AdjacencyMatrixVertex) v, (AdjacencyMatrixVertex) w, o);
		Position<AdjacencyMatrixEdge> position = edges.insertLast(edge);
		edge.position = position;
		matrix[((AdjacencyMatrixVertex) v).index][((AdjacencyMatrixVertex) w).index] = edge;
		matrix[((AdjacencyMatrixVertex) w).index][((AdjacencyMatrixVertex) v).index] = edge;
		return edge;
	}

	@Override
	public V removeVertex(Vertex<V> v) {
		Iterator<Edge<E>> it = incidentEdges(v).iterator();
		while (it.hasNext()) it.remove();
		
		AdjacencyMatrixVertex vertex = (AdjacencyMatrixVertex) v;
		Position<AdjacencyMatrixVertex> pos = vertices.next(vertex.position);
		
		int index = vertex.index;
		while (index < vertices.size()) {
			for (int i = 0; i < vertices.size(); i++) {
				matrix[index][i] = matrix[index+1][i];
			}
			index++;
		}
		
		index = vertex.index;
		while (index < vertices.size()) {
			for (int i = 0; i < vertices.size(); i++) {
				matrix[i][index] = matrix[i][index+1];
			}
			index++;
		}
		
		vertices.remove(vertex.position);
		
		while (!pos.equals(vertices.last())) {
			pos.element().index--;
			pos = vertices.next(pos);
		}
		pos.element().index--;
		
		return vertex.element;
	}

/**
 * USEFUL DEBUGGING METHODS
	private void showVertexList() {
		for (AdjacencyMatrixVertex vertex : vertices) {
			System.out.print(vertex + " ");
		}
		System.out.println();
	}
	
	private void showMatrix() {
		System.out.println("\n\n----------------------------------------------------------------------");
		for (int i=0;i<vertices.size();i++) {
			for (int j=0;j<vertices.size();j++) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println("----------------------------------------------------------------------\n");
	}
 */
	
	@Override
	public E removeEdge(Edge<E> e) {
		AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) e;
		matrix[edge.start.index][edge.end.index] = null;
		matrix[edge.end.index][edge.start.index] = null;
		edges.remove(edge.position);
		return edge.element;
	}

	@Override
	public List<Edge<E>> incidentEdges(Vertex<V> v) {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		
		for (int i=0; i<vertices.size(); i++) {
			if (matrix[((AdjacencyMatrixVertex) v).index][i] != null) {
				list.insertLast(matrix[((AdjacencyMatrixVertex) v).index][i]);
			}
		}
		
		return list;
	}

	@Override
	public List<Vertex<V>> vertices() {
		LinkedList<Vertex<V>> list = new LinkedList<Vertex<V>>();
		for (AdjacencyMatrixVertex vertex : vertices) {
			list.insertLast(vertex);
		}
		return list;
	}

	@Override
	public List<Edge<E>> edges() {
		LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
		for (AdjacencyMatrixEdge edge : edges) {
			list.insertLast(edge);
		}
		return list;
	}

}
