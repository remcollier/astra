package astra.reasoner.util;

import java.util.HashMap;
import java.util.Map;

import astra.core.Intention;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.IsNull;
import astra.formula.Predicate;
import astra.term.Count;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.NullTerm;
import astra.term.Primitive;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;

public class ContextEvaluateVisitor extends AbstractEvaluateVisitor {
	public static interface Handler<T> {
		public Class<T> getType();
		public Object handle(LogicVisitor visitor, T object, Intention context);
	}

	private static Map<Class<? extends Formula>, Handler<? extends Formula>> formulaHandlers = new HashMap<Class<? extends Formula>, Handler<? extends Formula>>();
	private static Map<Class<? extends Term>, Handler<? extends Term>> termHandlers = new HashMap<Class<? extends Term>, Handler<? extends Term>>();

	public static <T extends Formula> void addFormulaHandler(Handler<T> handler) {
		formulaHandlers.put(handler.getType(), handler);
	}
	
	public static <T extends Term> void addTermHandler(Handler<T> handler) {
		termHandlers.put(handler.getType(), handler);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Formula> Handler<Formula> getFormulaHandler(Class<T> cls) {
		return (Handler<Formula>) formulaHandlers.get(cls);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Term> Handler<Term> getTermHandler(Class<T> cls) {
		return (Handler<Term>) termHandlers.get(cls);
	}
	
	Intention context;
	
	public ContextEvaluateVisitor(Intention context) {
		this(context, false);
	}

	public ContextEvaluateVisitor(Intention context, boolean passByValue) {
		super(passByValue);
		this.context = context;
	}

	public Object visit(Formula formula) {
		Handler<Formula> handler = getFormulaHandler(formula.getClass());
		if (handler == null) {
			return super.visit(formula);
		}
		return handler.handle(this, formula, context);
	}
	
	public Object visit(Term term) {
		Handler<Term> handler = getTermHandler(term.getClass());
		if (handler == null) {
			return super.visit(term);
		}
		return handler.handle(this, term, context);
	}
	
	static {
		addTermHandler(new Handler<Variable>() {
			@Override public Class<Variable> getType() { return Variable.class; }
			@Override public Object handle(LogicVisitor visitor, Variable variable, Intention context) {
				Term term = context.getValue(variable);
//				System.out.println(variable + " (" + variable.id() + ") " + term);
				
				
				// NOTE: Have added "|| term instanceof Variable" here to stop situations
				// where variables that have been bound to other variables are substituted
				// for the original variable.
				// Specifically, this was causing a problem when an unbound variable is passed to
				// a subgoal, and then the corresponding subgoal variable (which is still unbound)
				// is passed to another subgoal:
				//
				// agent Test {
				//     module Console C;
				//
				//     rule +!main(list args) {
				//         !subTest(string X);
				//         C.println("X="+X);
				//     }
				// 
				//     rule +!subTest(string Y) {
				//         !subsubTest(Y);
				//         C.println("Y="+Y);
				//     }
				// 
				//     rule +!subsubTest(string Z) {
				//         Z = "yo! boyo";
				//     }
				// }
				// The error occurs when declaring goal !subsubTest(Y)...				
				if (term == null || term instanceof Variable) return variable;
				return term;
			}
		});
		addTermHandler(new Handler<ModuleTerm>() {
			@Override public Class<ModuleTerm> getType() { return ModuleTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleTerm term, Intention context) {
				Object value = term.evaluate(context);
				if (value == null) return new NullTerm();

				if (Type.getType(value).equals(Type.LIST) || Type.getType(value).equals(Type.FORMULA)) {
					return value;
				}
				
				return Primitive.newPrimitive(value);
			}
		});
		addTermHandler(new Handler<QueryTerm>() {
			@Override public Class<QueryTerm> getType() { return QueryTerm.class; }
			@Override public Object handle(LogicVisitor visitor, QueryTerm term, Intention context) {
				return Primitive.newPrimitive(context.query((Formula) term.formula().accept(visitor)) == null ? false:true); 
			}
		});
		addFormulaHandler(new Handler<IsNull>() {
			@Override public Class<IsNull> getType() { return IsNull.class; }
			@Override public Object handle(LogicVisitor visitor, IsNull isNull, Intention context) {
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
		});
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public Object visit(CartagoProperty property) {
//		Predicate obs_prop = null;
//		if (property.target() != null) {
//			obs_prop = CartagoAPI.get(context.agent).store().getObservableProperty(property.target(), property.content().predicate());
//		} else {
//			obs_prop = CartagoAPI.get(context.agent).store().getObservableProperty(property.content().predicate());
//		}
//		
//		if (obs_prop == null) return property;
//		
//		Map<Integer, Term> b = Unifier.unify(obs_prop, property.content(), context.agent);
//		for (Entry<Integer, Term> entry : b.entrySet()) {
//			context.addVariable(new Variable(entry.getValue().type(), entry.getKey()), entry.getValue());
//		}
//		
//		return (b == null) ? Predicate.FALSE : Predicate.TRUE;
//	}
//	
//	@Override
//	public Object visit(AcreFormula formula) {
//		return new AcreFormula(
//				(Term) formula.cid().accept(this),
//				(Term) formula.index().accept(this),
//				(Term) formula.type().accept(this), 
//				(Term) formula.performative().accept(this), 
//				(Formula) formula.content().accept(this)
//		);
//	}
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

}
