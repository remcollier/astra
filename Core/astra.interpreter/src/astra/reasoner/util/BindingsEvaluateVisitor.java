package astra.reasoner.util;

import java.util.Map;

import astra.cartago.CartagoAPI;
import astra.cartago.CartagoProperty;
import astra.core.Agent;
import astra.formula.AcreFormula;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Inference;
import astra.formula.IsNull;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.NullTerm;
import astra.term.Operator;
import astra.term.Primitive;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;

public class BindingsEvaluateVisitor extends AbstractEvaluateVisitor {
	Map<Integer, Term> bindings;
	Agent agent;
	
	public BindingsEvaluateVisitor(Map<Integer, Term> bindings, Agent agent) {
		super(false);
		this.bindings = bindings;
		this.agent = agent;
	}
	
	public Agent agent() {
		return agent;
	}
	
	@Override
	public Object visit(Variable variable) {
		Term term =  bindings.get(variable.id());
//		System.out.println(variable + " (" + variable.id() + ") " + term);
		if (term == null) return variable;
		return term;
	}
	
	public Object visit(ModuleTerm term) {
		return Primitive.newPrimitive(term.evaluate(this));
	}

	@SuppressWarnings("unchecked")
	public <T> T evaluate(Term term) {
		if (term instanceof Primitive) {
			T val =  ((Primitive<T>) term).value();
			return val;
		}
		
		if (term instanceof Variable) {
//			System.out.println("term: " + term);
			Term t = (Term) term.accept(this);
//			System.out.println("t: " + term);
			
			// check if term is unbound...
			if (t instanceof Variable) {
				return null;
			}
			
			return evaluate(t);
		}
		
		if (term instanceof Operator) {
			return ((Primitive<T>) term.accept(this)).value();
		}

		if (term instanceof ModuleTerm) {
			return ((Primitive<T>) term.accept(this)).value();
		}
		
		if (term instanceof ListTerm || term instanceof FormulaTerm || term instanceof Funct) {
			return (T) term.accept(this);
		}
		
		if (term instanceof ModuleTerm) {
			return ((Primitive<T>) term.accept(this)).value();
		}
		
		if (term instanceof NullTerm) {
//			throw new UnsupportedOperationException("handling null term");
			return null;
		}
		
		System.out.println("[ModuleFormulaAdaptor] FAILED TO EVALUATE: " + term.getClass().getName());

		return null;
	}

	@Override
	public Object visit(Inference inference) {
		return new Inference((Predicate) inference.head().accept(this), (Formula) inference.body().accept(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visit(CartagoProperty property) {
		Predicate obs_prop = null;
		if (property.target() != null) {
			obs_prop = CartagoAPI.get(agent).store().getObservableProperty(property.target(), property.content().predicate());
		} else {
			obs_prop = CartagoAPI.get(agent).store().getObservableProperty(property.content().predicate());
		}
		
		if (obs_prop == null) return property;
		
		Map<Integer, Term> b = Unifier.unify(obs_prop, property.content(), agent);
		if (b != null) bindings.putAll(b);
		
		return (b == null) ? Predicate.FALSE : Predicate.TRUE;
	}

	@Override
	public Object visit(AcreFormula formula) {
		return new AcreFormula(
				(Term) formula.cid().accept(this),
				(Term) formula.index().accept(this),
				(Term) formula.type().accept(this), 
				(Term) formula.performative().accept(this), 
				formula.content() instanceof FormulaVariable ? formula.content():(Formula) formula.content().accept(this)
		);
	}

	@Override
	public Object visit(IsNull isNull) {
		Term t = isNull.formula();
		if (t instanceof Variable) {
			Term term = bindings.get(((Variable) t).id());
			return (term == null) ? Predicate.TRUE : Predicate.FALSE;
		} else if (t instanceof FormulaVariable) {
			Term term = bindings.get(((FormulaVariable) t).variable().id());
			return (term == null) ? Predicate.TRUE : Predicate.FALSE;
		}
		return Predicate.FALSE;
	}

	@Override
	public Object visit(QueryTerm term) {
		return Primitive.newPrimitive(agent.query(term.formula(), bindings) == null ? false:true); 
	}
}
