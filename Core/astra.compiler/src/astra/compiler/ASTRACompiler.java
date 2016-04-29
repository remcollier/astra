package astra.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;
import astra.ast.reflection.ReflectionHelper;
import astra.ast.visitor.CodeGeneratorVisitor;
import astra.ast.visitor.ComponentStore;
import astra.ast.visitor.ComponentVisitor;
import astra.ast.visitor.GoalCheckVisitor;
import astra.ast.visitor.TypeCheckVisitor;
import astra.dgraph.ASTRANode;
import astra.dgraph.DependencyManager;

public class ASTRACompiler {
	static IJavaHelper helper = new ReflectionHelper();
	static DependencyManager manager = new DependencyManager(helper);
	
	public static void compile(String cls) {
		compile(cls, "src/");
	}
	
	public static void compile(String cls, String target) {
		try {
			if (!manager.isClassLoaded(cls)) {
				if (!manager.loadClass(cls)) {
					System.out.println("There was an error loading the class: " + cls);
	
					// The error could occur in cls or any parent of cls, so iterate through
					// the linearisation of the class hierachy, looking for and printing the
					// errors.
					for (ASTRANode node : manager.getLinearisation(cls)) {
						if (!node.loaded()) {
							for (ParseException error : node.getErrorList()) {
								error.printStackTrace();
							}
						}
					}
					
					System.exit(0);
				}
			}
			
			// The class is loaded, but only syntactic checks have been carried out. Now do
			// the semantic checks...
			LinkedList<ASTRANode> linearisation = manager.getLinearisation(cls);
			
			// Step 1: Iterate through the linearised list of classes (general-specific order)
			//         building a representation of the ontologies & triggering events.
			ComponentStore store = new ComponentStore();
			ComponentVisitor visitor = new ComponentVisitor(helper, store);
			for (int i=linearisation.size()-1; i >= 0; i--) {
				ASTRANode node = linearisation.get(i);
				if (!node.loaded()) throw new ParseException("Could not compile: " + cls + " due to error in: " + node.element().getQualifiedName(), node.element());
				
				// Construct the component store for the class: contains a set of resolved modules,
				// ontologies and rule trigger events.
				node.element().accept(visitor, store);
			}
			// Now get a reference to the class you are actually compiling...
			ASTRANode node = linearisation.get(0);
	
			// Step 2: Check that the formulae and goals have corresponding entries in
			//         the component store.
			node.element().accept(new TypeCheckVisitor(), store);
			node.element().accept(new GoalCheckVisitor(), store);
	
			// Step 3: Generate the source code and save it to disk
			CodeGeneratorVisitor cgv = new CodeGeneratorVisitor(helper,store);
			node.element().accept(cgv, null);
			try {
				System.out.println("Generating Target File: " + node.element().getFilename());
				File file = new File(target + node.element().getFilename());
				file.createNewFile();
				FileWriter out = new FileWriter(file);
				out.write(cgv.toString());
				out.close();
				System.out.println("PROGRAM COMPILED SUCCESSFULLY");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}