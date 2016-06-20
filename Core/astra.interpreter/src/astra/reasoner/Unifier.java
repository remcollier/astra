package astra.reasoner;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;

import astra.acre.AcreEvent;
import astra.cartago.CartagoASTRAEvent;
import astra.cartago.CartagoEventUnifier;
import astra.eis.EISEvent;
import astra.event.BeliefEvent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.formula.AND;
import astra.formula.AcreFormula;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.messaging.MessageEvent;
import astra.reasoner.unifier.AcreEventUnifier;
import astra.reasoner.unifier.BeliefEventUnifier;
import astra.reasoner.unifier.EISEventUnifier;
import astra.reasoner.unifier.GoalEventUnifier;
import astra.reasoner.unifier.MessageEventUnifier;
import astra.reasoner.util.Utilities;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListSplitter;
import astra.term.ListTerm;
import astra.term.Term;
import astra.term.Variable;
import astra.type.ObjectType;

/**
 * This class performs unification of various logical formulae: {@link Predicate},
 * {@link Goal}, and {@link MentalEvent}.
 * 
 * The core work in this class is done in the private method.
 *  
 * @author rem
 *
 */
public class Unifier {
	public static Map<Class<?>, EventUnifier> eventFactory = new HashMap<Class<?>, EventUnifier>();
	
	static {
		eventFactory.put(BeliefEvent.class, new BeliefEventUnifier());
		eventFactory.put(GoalEvent.class, new GoalEventUnifier());
		eventFactory.put(MessageEvent.class, new MessageEventUnifier());
		eventFactory.put(EISEvent.class, new EISEventUnifier());
		eventFactory.put(AcreEvent.class, new AcreEventUnifier());
	}
	
	public static Map<Integer, Term> unify(Event source, Event target) {
//		System.out.println("in unify: event: " +event.getClass().getName());
//		System.out.println("in unify: event2: " +event2.getClass().getName());

		EventUnifier unifier = eventFactory.get(source.getClass());
		if (unifier != null) {
			return unifier.unify(source, target);
		}

		return null;
	}

	/**
	 * Generate variable bindings for two predicates or return null if there is no binding...
	 * 
	 * @param source first predicate
	 * @param target second predicate
	 * @return a {@link Bindings} object or null
	 */
	public static Map<Integer, Term> unify(Predicate source, Predicate target) {
		return unify(source, target, new HashMap<Integer, Term>());
	}
	
	/**
	 * Generate variable bindings for two predicates or return null if there is no binding...
	 * 
	 * @param source first predicate
	 * @param target second predicate
	 * @param target the existing bindings (if any exist)
	 * @return a {@link Bindings} object or null
	 */
	public static Map<Integer, Term> unify(Predicate source, Predicate target, Map<Integer, Term> bindings) {
		if  (source.id() == target.id() && source.size() == target.size()) {
			return unify(source.terms(), target.terms(), bindings);
		}
		return null;
	}

	public static Map<Integer, Term> unify(AcreFormula source, AcreFormula target, Map<Integer, Term> bindings) {
		Map<Integer, Term> b = unify(new Term[] {source.cid(), source.index(), source.type(), source.performative()}, new Term[] {target.cid(), target.index(), target.type(), target.performative()}, bindings);
//		System.out.println("source: " + source + " / target: " + target + " bindings: " + b);
		if (b != null) {
			b = unify(source.content(), target.content(), b);
//			System.out.println("bindings: " + b);
			if (b != null) {
				return b;
			}
		}
		
		return null;
	}
	
	/**
	 * Generate variable bindings for two achievement goals or return null if there is no binding...
	 * 
	 * @param source first goal
	 * @param target second goal
	 * @return a {@link Bindings} object or null
	 */
	public static Map<Integer, Term> unify(Goal source, Goal target) {
		return unify(source.formula(), target.formula());
	}

	public static Map<Integer, Term> unify(Term[] source, Term[] target, Map<Integer, Term> bindings) {
		for (int i=0; i < source.length; i++) {
			Term sourceTerm = source[i];
			Term targetTerm = target[i];
//			System.out.println("\n\n--------------------\nsourceTerm: " + sourceTerm + " / " +sourceTerm.type());
//			System.out.println("targetTerm: " + targetTerm+ " / " +targetTerm.type());
			
			// Check that the types match
			if (!sourceTerm.type().equals(targetTerm.type())) {
				boolean failed = true;

//				System.out.println("type mismatch");
//				if (sourceTerm.type() instanceof ObjectType && targetTerm.type() instanceof ObjectType) {
//					failed = !sourceTerm.type().equals(targetTerm.type());
//				}
				
				if (failed) {
//					System.out.println("[Unifier] checking: " + sourceTerm + " / " + targetTerm);
//					System.out.println("[Unifier] type mismatch..." + sourceTerm.type() + " / "+ targetTerm.type());
					return null;
				}
			}
			
//			System.out.println("[Unifier] match: " + sourceTerm + " / " + targetTerm);
			
			// Do the actual unification of the terms...
			if (Variable.class.isInstance(sourceTerm)) {
				Variable var = (Variable) sourceTerm;
				
				Term term = bindings.get(var.id());
				if (term == null) {
					bindings.put(var.id(), targetTerm);
				} else {
					if (!term.equals(targetTerm)) {
						return null;
					}
				}
			} else if (Variable.class.isInstance(targetTerm)) {
				Variable var = (Variable) targetTerm;
				Term term = bindings.get(var.id());
				if (term == null) {
					bindings.put(var.id(), sourceTerm);
				} else {
					if (!term.equals(sourceTerm)) {
						return null;
					}
				}
			} else if (ListTerm.class.isInstance(sourceTerm)) {
				if (ListTerm.class.isInstance(targetTerm)) {
					if (((ListTerm) sourceTerm).size() != ((ListTerm) targetTerm).size()) return null;
					if (unify(((ListTerm) sourceTerm).terms(), ((ListTerm) targetTerm).terms(),bindings) == null) return null;
				} else if (ListSplitter.class.isInstance(targetTerm)) {
					ListTerm list = (ListTerm) sourceTerm;
					if (list.size() < 2) return null;
					ListTerm tail = new ListTerm(list.subList(1, list.size()).toArray(new Term[list.size()-1]));
					if (unify(
							new Term[] {((ListTerm) sourceTerm).get(0), tail}, 
							new Term[] {((ListSplitter) targetTerm).head(), ((ListSplitter) targetTerm).tail()}, bindings) == null) return null;
				} else 
					return null;
			} else if (ListTerm.class.isInstance(targetTerm)) {
				if (ListSplitter.class.isInstance(sourceTerm)) {
					ListTerm list = (ListTerm) targetTerm;
					if (list.size() > 1) {
						ListTerm tail = new ListTerm(list.subList(1, list.size()).toArray(new Term[list.size()-1]));
						if (unify(
								new Term[] {((ListTerm) targetTerm).get(0), tail}, 
								new Term[] {((ListSplitter) sourceTerm).head(), ((ListSplitter) sourceTerm).tail()}, bindings) == null) return null;
					}
				} else 
					return null;
			} else if (Funct.class.isInstance(sourceTerm)) {
				Funct sf = (Funct) sourceTerm;
				Funct tf = (Funct) targetTerm;
				if (sf.id() != tf.id() || sf.size() != tf.size()) return null;
				System.out.println("in funct comparison: " + sourceTerm + " / " + targetTerm);
//				System.out.println("\tWE HAVE AN ID MATCH");
				if (unify(((Funct) sourceTerm).terms(), ((Funct) targetTerm).terms(), bindings) == null) return null;
//				System.out.println("\tWE HAVE A TERM MATCH");
			} else if (!sourceTerm.equals(targetTerm)) {
//				System.out.println("terms are not equal: " + sourceTerm + " / " + targetTerm);
				return null;
			}
			
		}
		
		return bindings;
	}

	
	public static Map<Integer, Term> unify(Formula source, Formula target, Map<Integer,Term> bindings) {
		if (source instanceof FormulaVariable) {
			Variable variable = ((FormulaVariable) source).variable();
			bindings.put(variable.id(), new FormulaTerm(target));
			return bindings;
		} else if (target instanceof FormulaVariable) {
			Variable variable = ((FormulaVariable) target).variable();
			bindings.put(variable.id(), new FormulaTerm(source));
			return bindings;
		} else if (source instanceof Predicate && target instanceof Predicate) {
			return unify((Predicate) source, (Predicate) target, bindings);
		} else if (source instanceof AcreFormula && target instanceof AcreFormula) {
			return unify((AcreFormula) source, (AcreFormula) target, bindings);
		} else if (source instanceof AND && target instanceof AND) {
			AND s = (AND) source;
			AND t = (AND) target;
			
			Map<Integer, Term> temp = unify(s.left(), t.left(), bindings);
			if (temp == null) return null;
			
			temp = unify(s.right(), t.right(), Utilities.merge(temp, bindings));
			if (temp == null) return null;
			
			return temp;
		}

		return null;
		
	}
}
