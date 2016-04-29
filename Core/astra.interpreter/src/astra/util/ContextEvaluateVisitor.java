package astra.util;

import java.util.Map;
import java.util.Map.Entry;

import astra.cartago.CartagoProperty;
import astra.core.Intention;
import astra.formula.AcreFormula;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.IsNull;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.term.ModuleTerm;
import astra.term.NullTerm;
import astra.term.Primitive;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;

public class ContextEvaluateVisitor extends AbstractEvaluateVisitor {
	Intention context;
	
	public ContextEvaluateVisitor(Intention context) {
		this(context, false);
	}

	public ContextEvaluateVisitor(Intention context, boolean passByValue) {
		super(passByValue);
		this.context = context;
	}

	@Override
	public Object visit(Variable variable) {
		Term term = context.getValue(variable);
		if (term == null) return variable;
		return term;
	}

	public Object visit(ModuleTerm term) {
		Object value = term.evaluate(context);
		if (value == null) return new NullTerm();

		if (Type.getType(value).equals(Type.LIST) || Type.getType(value).equals(Type.FORMULA)) {
			return value;
		}
		
		return Primitive.newPrimitive(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visit(CartagoProperty property) {
		Predicate obs_prop = null;
		if (property.target() != null) {
			obs_prop = context.getCartagoAPI().store().getObservableProperty(((Primitive<String>) property.target().accept(this)).value(), property.content().predicate());
		} else {
			obs_prop = context.getCartagoAPI().store().getObservableProperty(property.content().predicate());
		}
		
		if (obs_prop == null) return property;
		
		Map<Integer, Term> b = Unifier.unify(obs_prop, property.content());
		for (Entry<Integer, Term> entry : b.entrySet()) {
			context.addVariable(new Variable(entry.getValue().type(), entry.getKey()), entry.getValue());
		}
		
		return (b == null) ? Predicate.FALSE : Predicate.TRUE;
	}
	
	@Override
	public Object visit(AcreFormula formula) {
		return new AcreFormula(
				(Term) formula.cid().accept(this),
				(Term) formula.index().accept(this),
				(Term) formula.type().accept(this), 
				(Term) formula.performative().accept(this), 
				(Formula) formula.content().accept(this)
		);
	}
//		// Step 1: Get an initial list of matching conversations based on the conversation id.
//		List<Conversation> conversations = new ArrayList<Conversation>();
//		Term cid = (Term) formula.cid().accept(this);
//		if (cid instanceof Variable) {
//			conversations.addAll(context.getAcreAPI().getConversationManager().getAllConversations().values());
//		} else {
//			conversations.add(context.getAcreAPI().getConversationManager().getConversationByID(((Primitive<String>) cid).value()));
//		}
//		
//		// Check all possible conversations
//		for (Conversation conversation : conversations) {
//			Map<Integer, Term> b = Unifier.unify(new Term[] {formula.type()}, new Term[] {Primitive.newPrimitive(conversation)});
//		}
//		return null;
//	}

	@Override
	public Object visit(IsNull isNull) {
		Term t = isNull.formula();
		if (t instanceof Variable) {
			Term term = context.getValue((Variable) t);
			return (term == null) ? Predicate.TRUE : Predicate.FALSE;
		} else if (t instanceof FormulaVariable) {
			Term term = context.getValue(((FormulaVariable) t).variable());
			return (term == null) ? Predicate.TRUE : Predicate.FALSE;
		}
		return Predicate.FALSE;
	}

	@Override
	public Object visit(QueryTerm term) {
		return Primitive.newPrimitive(context.query((Formula) term.formula().accept(this)) == null ? false:true); 
	}
}
