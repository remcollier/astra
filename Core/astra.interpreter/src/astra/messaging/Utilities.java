package astra.messaging;

import java.io.Serializable;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.Term;
import astra.util.LogicVisitor;

public class Utilities {
	public static class PredicateState implements Formula, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8065767049634724024L;
		public Term[] terms;
		public String predicate;
		
		public PredicateState(Predicate predicate) {
			this.predicate = predicate.predicate();
			terms = predicate.terms();
		}

		@Override
		public Object accept(LogicVisitor visitor) {
			return null;
		}

		@Override
		public boolean matches(Formula formula) {
			return false;
		}
	}

	public static PredicateState toPredicateState(Formula formula) {
		// Transform the state into one that can be sent...
		if (formula instanceof Predicate) {
			return new PredicateState((Predicate) formula);
		}
		System.out.println("[MigrationKit] Could not transform belief: " + formula);
		return null;
	}
	
	public static Formula fromPredicateState(PredicateState state) {
		return new Predicate(state.predicate, state.terms);
	}
}
