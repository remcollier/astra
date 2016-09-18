package astra.dgraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;
import graph.core.DirectedGraph;
import graph.core.Edge;
import graph.core.Vertex;
import graph.impl.DirectedAdjacencyListGraph;

public class DependencyManager {
	IJavaHelper helper;
	
	public DependencyManager(IJavaHelper helper) {
		this.helper = helper;
		
		// load the default Agent type
		try {
			ASTRANode node = new ASTRANode("astra.lang.Agent", helper);
			Vertex<ASTRANode> vertex = graph.insertVertex(node);
			sources.put(node.element().getQualifiedName(), vertex);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	Map<String, Vertex<ASTRANode>> sources = new HashMap<String, Vertex<ASTRANode>>();
	DirectedGraph<ASTRANode, String> graph = new DirectedAdjacencyListGraph<ASTRANode, String>();
	
	/**
	 * This class adds the specified class and any dependent classes that have not already 
	 * been added to the dependency manager.
	 * 
	 * @param cls
	 * @throws ParseException
	 */
	public boolean loadClass(String cls) throws ParseException {
		Vertex<ASTRANode> vertex = sources.get(cls);
		if (vertex == null) {
			ASTRANode node = new ASTRANode(cls, helper);
			
			if (!node.loaded) {
				for (ParseException pe: node.getErrorList()) {
					System.out.println(pe.getMessage());
					pe.printStackTrace();
				}
				throw new ParseException("Unknown class: " + cls, 1, 1, 1);
			}
			vertex = graph.insertVertex(node);
			sources.put(node.element().getQualifiedName(), vertex);
			validateParentLinks(vertex);
			if (node.element() == null) return false;

			boolean loaded = node.loaded();
			for (String parent : node.element().getClassDeclaration().parents()) {
				String qualifiedName = parent;
				if (!qualifiedName.contains(".") & !node.element().packageElement().packageName().equals("")) {
					qualifiedName = node.element().packageElement().packageName()+"."+qualifiedName;
				}
	
				loaded = loadClass(qualifiedName) && loaded;
				graph.insertEdge(sources.get(qualifiedName), vertex, "");
			}
			node.loaded(loaded);
			return loaded;
		}
		return vertex.element().loaded();
	}

	public boolean deleteClass(String cls) throws ParseException {
		if (!sources.containsKey(cls)) return false;
		
		Vertex<ASTRANode> vertex = sources.remove(cls);
		graph.removeVertex(vertex);
		return true;
	}
	
	public boolean reloadClass(String cls) throws ParseException {
		System.out.println("reloading: " + cls);
		if (!sources.containsKey(cls)) throw new ParseException("Unknown class: " + cls, 1, 1, 1);
		return doReload(sources.get(cls), true);
	}
	
	public LinkedList<ASTRANode> dependencyList(String cls) {
		return addToDependencyList(new LinkedList<ASTRANode>(), sources.get(cls));
	}

	public boolean isClassLoaded(String cls) {
		return sources.containsKey(cls);
	}
	
	/**
	 * Linearisation of Class Hierarchy using Breadth First Traversal of
	 * the Directed Graph.
	 * 
	 * @param clazz
	 * @return
	 * @throws ParseException 
	 */
	public LinkedList<ASTRANode> getLinearisation(String clazz) throws ParseException {
		LinkedList<ASTRANode> linearization = new LinkedList<ASTRANode>();
		
		Queue<Vertex<ASTRANode>> queue = new LinkedList<Vertex<ASTRANode>>();
		
		Vertex<ASTRANode> vertex = sources.get(clazz);
		
		// If there is no record of the class, return null...
		if (vertex == null) return null;
			
		queue.add(vertex);
		while (!queue.isEmpty()) {
			vertex = queue.poll();
			if (!linearization.contains(vertex.element())) {
				for (Edge<String> edge: graph.inEdges(vertex)) {
					Vertex<ASTRANode> source = graph.source(edge);
					if (!queue.contains(source)) queue.add(source);
				}
			}
			linearization.add(vertex.element());
		}
		return linearization;
	}

	private boolean doReload(Vertex<ASTRANode> vertex, boolean parentFailure) throws ParseException {
		boolean result = vertex.element().reload(helper, parentFailure);
		validateParentLinks(vertex);
		for (Edge<String> edge : graph.outEdges(vertex)) {
			doReload(graph.target(edge), result);
		}
		return result;
	}
	
	private void validateParentLinks(Vertex<ASTRANode> target) throws ParseException {
		for (Edge<String> edge : graph.inEdges(target)) {
			graph.removeEdge(edge);
		}

		ASTRANode element = target.element();
		for(String parent : element.element().getClassDeclaration().parents()) {
			String cls = parent;
			if (!sources.containsKey(cls)) {
				cls = element.element().packageElement().packageName()+"."+cls;
				if (!sources.containsKey(cls)) 
					throw new ParseException("Unknown ASTRA Class: " + parent, element.element().getClassDeclaration());
			}
			Vertex<ASTRANode> source = sources.get(cls);
			graph.insertEdge(source, target, null);
		}
	}

	private LinkedList<ASTRANode> addToDependencyList(LinkedList<ASTRANode> list, Vertex<ASTRANode> target) {
		if (target != null && target.element() != null) {
			list.add(target.element());
			for (Edge<String> edge : graph.outEdges(target)) {
				addToDependencyList(list, graph.target(edge));
			}
		}
		return list;
	}

	public String toString() {
		return graph.toString();
	}

	public ASTRANode getClass(String cls) {
		if (!sources.containsKey(cls)) {
			return null;
		}
		return sources.get(cls).element();
	}
}
