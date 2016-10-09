package astra.compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;
import astra.ast.visitor.CodeGeneratorVisitor;
import astra.ast.visitor.ComponentStore;
import astra.ast.visitor.ComponentVisitor;
import astra.ast.visitor.GoalCheckVisitor;
import astra.ast.visitor.TypeCheckVisitor;
import graph.core.DirectedGraph;
import graph.core.Edge;
import graph.core.Vertex;
import graph.impl.DirectedAdjacencyListGraph;

public class ASTRAClassHierarchy {
	private IJavaHelper helper;
	
	Map<String, Vertex<ASTRAClass>> classes = new HashMap<String, Vertex<ASTRAClass>>();
	DirectedGraph<ASTRAClass, String> graph = new DirectedAdjacencyListGraph<ASTRAClass, String>();
	
	public ASTRAClassHierarchy(IJavaHelper helper) {
		this.helper = helper;
		
		// add the default Agent type
		// we assume that there are no problems here...
		Vertex<ASTRAClass> vertex = addClass("astra.lang.Agent");
		vertex.element().load(helper);
	}

	private Vertex<ASTRAClass> addClass(String clazz) {
		ASTRAClass node = new ASTRAClass(clazz);
		Vertex<ASTRAClass> vertex = graph.insertVertex(node);
		classes.put(clazz, vertex);
		return vertex;
	}

	/**
	 * This method returns true if the class is already loaded, false otherwise...
	 * 
	 * @param cls
	 * @return
	 */
	public synchronized boolean contains(String cls) {
		return classes.containsKey(cls);
	}
	
	public synchronized void compile(String cls, Map<String, List<ParseException>> errors) {
		List<Vertex<ASTRAClass>> dependencies = new LinkedList<Vertex<ASTRAClass>>(); 

		load(cls, errors, dependencies);
		
		// Add all dependencies to the error map - this is to ensure that all existing
		// problem markers are deleted...
		for (Vertex<ASTRAClass> vertex : dependencies) {
			if (!errors.containsKey(vertex.element().name())) {
				errors.put(vertex.element().name(), new LinkedList<ParseException>());
			}
		}
		
		// Check if the class being recompiled has errors
		if (errors.get(cls) == null || errors.get(cls).isEmpty()) {
			// No errors, so start building it...
			// Compile the main class
			compile(dependencies.remove(0), errors);
			
			// Now refresh the dependencies (needs reload of AST + recompilation)
			refreshDependencies(dependencies, errors);
		} else {
			// Errors - add an error to each dependent indicating that it could
			// not be compiled do to the error in this class...
			dependencies.remove(0);
			for (Vertex<ASTRAClass> vertex : dependencies) {
				if (vertex.element().element() != null) {
					addError(
							vertex, 
							errors, 
							new ParseException("Cannot compile: " + vertex.element().name() + " due to error in " + cls, vertex.element().element()));
				}
			}			
		}
	}

	private void compile(Vertex<ASTRAClass> vertex, Map<String, List<ParseException>> errors) {
		try {
			compile(vertex);
		} catch (ParseException e) {
			addError(vertex, errors, e);
		}
	}

	/**
	 * This method actually loads the specified class and updates the
	 * hierarchy to reflect it. This can include loading of parents
	 * (where the class is new) or invalidating of classes (forcing
	 * them to be recompiled).
	 * 
	 * Upon completion, a list of invalid classes will have been
	 * created. ALL of these classes will need to be recompiled...
	 *  
	 * @param cls
	 */
	private void load(String cls, Map<String, List<ParseException>> errors, List<Vertex<ASTRAClass>> list) {
		Vertex<ASTRAClass> vertex = classes.get(cls);
		if (vertex != null) {
			// Check if we need to recompile...
			if (!vertex.element().sourceChanged(helper)) {
				// class already loaded and source not changed, so
				// do nothing...
				return ;
			}
			
			// Source is newer than compiled code - need to 
			// remove upward dependencies and reload AST.
			// Here we also need to invalidate any children
			// as they will need to be recompiled once the
			// parent is recompiled...
			removeParentDependencies(vertex);
		} else {
			// Class has not been seen before, so add it...
			vertex = addClass(cls);
		}
		invalidate(vertex, list);
		
		// Okay, so the vertex representing the class
		// has been added / existing parent dependencies
		// have been removed.
		//
		// Lets reload the AST and re-add the parent dependencies...
		if (!vertex.element().load(helper)) {
			// Okay, we did not manage to load the class so there should be
			// some errors, lets return them...
			errors.put(cls, vertex.element().errorList());
		} else {
			
			ASTRAClass clazz = vertex.element();
			for (String parent :  clazz.element().getClassDeclaration().parents()) {
				String qualifiedName = parent;
				if (!qualifiedName.contains(".") & !clazz.element().packageElement().packageName().equals("")) {
					qualifiedName = clazz.element().packageElement().packageName()+"."+qualifiedName;
				}
		
				// Recursive call to load(...) method fills error map
				load(qualifiedName, errors, list);
				graph.insertEdge(classes.get(qualifiedName), vertex, "");
			}
		}
	}
	
	private void invalidate(Vertex<ASTRAClass> vertex, List<Vertex<ASTRAClass>> list) {
		list.add(vertex);
		for(Edge<String> edge : graph.outEdges(vertex)) {
			invalidate(graph.target(edge), list);
		}
	}
	
	public synchronized void refreshDependencies(List<Vertex<ASTRAClass>> list, Map<String, List<ParseException>> errors) {
		while (!list.isEmpty()) {
			// Only try to compile the class if there are no syntax
			// errors...
			list.get(0).element().load(helper);
			if (list.get(0).element().errorList().isEmpty()) {
				list.get(0).element().load(helper);
				compile(list.remove(0), errors);
			}
		}
	}

	private void addError(Vertex<ASTRAClass> vertex, Map<String, List<ParseException>> errors, ParseException e) {
		List<ParseException> l = errors.get(vertex.element().name());
		if (l == null) {
			l = new LinkedList<ParseException>();
			errors.put(vertex.element().name(), l);
		}
		l.add(e);
		
	}

	private void compile(Vertex<ASTRAClass> vertex) throws ParseException {
		if (vertex.element().element() == null) {
			throw new ParseException("Cannot compile: " + vertex.element().name() + " source is not local", vertex.element().element());
		}
		
		if  (!vertex.element().element().local()) {
			throw new ParseException("Cannot compile: " + vertex.element().name() + " source is not local", vertex.element().element());
		}
		
		// The class is loaded, but only syntactic checks have been carried out. Now do
		// the semantic checks...
		LinkedList<ASTRAClass> linearisation = getLinearisation(vertex);
		
		// Step 1: Iterate through the linearised list of classes (general-specific order)
		//         building a representation of the ontologies & triggering events.
		ComponentStore store = new ComponentStore();
		ComponentVisitor visitor = new ComponentVisitor(helper, store);
		for (int i=linearisation.size()-1; i >= 0; i--) {
			ASTRAClass node = linearisation.get(i);
			if (!node.isLoaded()) {
				throw new ParseException("Could not compile: " + vertex.element().name() + " due to error in: " + node.element().getQualifiedName(), node.element());
			}
			
			// Construct the component store for the class: contains a set of resolved modules,
			// ontologies and rule trigger events.
			node.element().accept(visitor, store);
		}
		// Now get a reference to the class you are actually compiling...
		ASTRAClass node = linearisation.get(0);

		// Step 2: Check that the formulae and goals have corresponding entries in
		//         the component store.
		node.element().accept(new TypeCheckVisitor(), store);
		node.element().accept(new GoalCheckVisitor(), store);

		// Step 3: Generate the source code and save it to disk
		CodeGeneratorVisitor cgv = new CodeGeneratorVisitor(helper,store);
		node.element().accept(cgv, null);
		helper.createTarget(node.element(), cgv.toString());
	}

	private void removeParentDependencies(Vertex<ASTRAClass> vertex) {
		for(Edge<String> edge : graph.inEdges(vertex)) {
			graph.removeEdge(edge);
		}
	}

	public synchronized boolean checkClass(String cls) {
		Vertex<ASTRAClass> vertex = classes.get(cls);
		if (vertex != null) {
			// check if the class has been modified...
			return vertex.element().sourceChanged(helper);
		}
		return false;		
	}

	/**
	 * Linearisation of Class Hierarchy using Breadth First Traversal of
	 * the Directed Graph.
	 * 
	 * @param clazz
	 * @return
	 * @throws ParseException 
	 */
	public synchronized LinkedList<ASTRAClass> getLinearisation(Vertex<ASTRAClass> vertex) throws ParseException {
		LinkedList<ASTRAClass> linearization = new LinkedList<ASTRAClass>();
		
		Queue<Vertex<ASTRAClass>> queue = new LinkedList<Vertex<ASTRAClass>>();
		
		queue.add(vertex);
		while (!queue.isEmpty()) {
			vertex = queue.poll();
			if (!linearization.contains(vertex.element())) {
				for (Edge<String> edge: graph.inEdges(vertex)) {
					Vertex<ASTRAClass> source = graph.source(edge);
					if (!queue.contains(source)) queue.add(source);
				}
			}
			linearization.add(vertex.element());
		}
		return linearization;
	}

	public synchronized void deleteClass(String cls, Map<String, List<ParseException>> errors) {
		System.out.println("Deleting:  " + cls);
		Vertex<ASTRAClass> vertex = classes.remove(cls);
		if (vertex == null) {
			System.out.println("VERTEX is null");
			return;
		} 
		List<Vertex<ASTRAClass>> list = new LinkedList<Vertex<ASTRAClass>>();
		if (vertex.element() != null && vertex.element().isLoaded()) invalidate(vertex, list);
		graph.removeVertex(vertex);
		
		//remove vertex from the list
		list.remove(0);
		refreshDependencies(list, errors);
	}

	public synchronized LinkedList<ASTRAClass> getLinearisation(String cls) throws ParseException {
		return getLinearisation(classes.get(cls));
	}

	public synchronized ASTRAClass getClass(String cls) {
		return classes.get(cls).element();
	}
}
