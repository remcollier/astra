package graph.core;

import graph.util.List;


public interface Graph<V,E> {
    public Vertex<V>[] endVertices(Edge<E> v);
    public Vertex<V> opposite(Vertex<V> v, Edge<E> e);
    public boolean areAdjacent(Vertex<V> v, Vertex<V> w);
    public V replace(Vertex<V> v, V x);
    public E replace(Edge<E> e, E x);
    public Vertex<V> insertVertex(V o);
    public Edge<E> insertEdge(Vertex<V> v, Vertex<V> w, E o);
    public V removeVertex(Vertex<V> v);
    public E removeEdge(Edge<E> e);
    public List<Edge<E>> incidentEdges(Vertex<V> v);
    public List<Vertex<V>> vertices();
    public List<Edge<E>> edges();
}