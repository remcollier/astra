package astra.ast.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import astra.ast.core.ParseException;
import astra.ast.element.ModuleElement;
import astra.ast.formula.GoalFormula;

public class ComponentStore {
	public ComponentStore() {
		signatures.add("formula:true");
		signatures.add("formula:false");
//		events.add(e)
	}
	
	public Set<String> signatures = new HashSet<String>();
	public Set<String> types = new HashSet<String>();
	public Set<String> events = new HashSet<String>();
	public Map<String, ModuleElement> modules = new HashMap<String, ModuleElement>();
	public Set<String> plans = new HashSet<String>();
	
	
	public void checkForEvent(GoalFormula formula) throws ParseException {
//		System.out.println("events: " + events);
		if (!events.contains("update:+:"+formula.toSignature())) {
			// REM: This condition is now commented out because
			// -goal events are failure events. 
			// && !events.contains("update:-:"+formula.toSignature())) {
			throw new ParseException("No rule has been declared to handle the goal: " + formula, formula);
		}
	}
}
