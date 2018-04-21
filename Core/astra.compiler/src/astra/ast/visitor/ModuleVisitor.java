package astra.ast.visitor;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ILanguageDefinition;
import astra.ast.core.ParseException;
import astra.ast.element.GRuleElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
import astra.ast.element.TypesElement;

/**
 * This class iterates through the parse tree adding types where necessary.
 * 
 * @author Rem Collier
 *
 */
public class ModuleVisitor extends AbstractVisitor {
	protected IJavaHelper helper;
	
	public ModuleVisitor(IJavaHelper helper) {
		this.helper = helper;
	}

	public Object visit(ASTRAClassElement element, Object data) throws ParseException {
		helper.setup(element.packageElement(), element.imports());
		
		ComponentStore store = (ComponentStore) data;
		
		// Check each module exists and store a reference to the module in the component store
		for (ModuleElement module : element.getModules()) {
			String qualifiedName = helper.resolveModule(module.className());
			if (qualifiedName == null)
				throw new ParseException("Unknown module declaration: " + module.className(), module);
//			System.out.println("[SRV] Map: " + module.className() + " = " + qualifiedName);
			module.setQualifiedName(qualifiedName);
			store.modules.put(module.name(), module);
		}

		// Record any ontologies that you find
		for (TypesElement ontology : element.getOntologies()) {
			if (store.types.contains(ontology.name())) 
				throw new ParseException("Duplicate Ontology: " + ontology.name(), 
						ontology.start, ontology.end);

			for (ILanguageDefinition definition : ontology.definitions()) {
				if (store.signatures.contains(definition.toSignature())) 
					throw new ParseException("Conflict in ontology: " + ontology.name() + " for term: " + definition, 
							ontology.start, ontology.end);
				
				store.signatures.add(definition.toSignature());
			}
			
			// Ontology was loaded without conflict so we are okay...
			store.types.add(ontology.name());
		}
		
		// Record the event part of any events that you find.
		for (RuleElement rule : element.getRules()) {
			String signature = rule.event().toSignature();
			if (!store.events.contains(signature)) store.events.add(signature);
		}
		
		// Record the event part of any events that you find.
		for (GRuleElement rule : element.getGRules()) {
			String signature = rule.event().toSignature();
			if (!store.events.contains(signature)) store.events.add(signature);
		}

		// Record all partial plans that you find
		for (PlanElement plan : element.getPlans()) {
			String signature = plan.signature().toSignature();
			if (!store.plans.contains(signature)) store.plans.add(signature);
		}
		
		return null;
	}
}
