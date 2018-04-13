package astra.reasoner.util;

import java.util.HashMap;
import java.util.Map;

import astra.formula.AND;
import astra.formula.Bind;
import astra.formula.BracketFormula;
import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Goal;
import astra.formula.ModuleFormula;
import astra.formula.NOT;
import astra.formula.OR;
import astra.formula.Predicate;
import astra.term.Brackets;
import astra.term.Count;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListSplitter;
import astra.term.ListTerm;
import astra.term.Operator;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;

@SuppressWarnings("rawtypes")
public abstract class AbstractEvaluateVisitor implements LogicVisitor {
	public static interface Handler<T> {
		public Class<T> getType();
		public Object handle(LogicVisitor visitor, T object,boolean passByValue);
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
	
	protected boolean passByValue=true;

	public AbstractEvaluateVisitor(boolean passByValue) {
		this.passByValue = passByValue;
	}

	public Object visit(Formula formula) {
		Handler<Formula> handler = getFormulaHandler(formula.getClass());
//		System.out.println("AEV Formula:" + formula + " / handler="+ handler);
		if (handler == null) return null;
		return handler.handle(this, formula, passByValue);
	}
	
	public Object visit(Term term) {
//		System.out.println("AEV: Term: " + term.getClass().getCanonicalName());
		Handler<Term> handler = getTermHandler(term.getClass());
//		System.out.println("AEV: Term: " + term + " / handler="+ handler);
		if (handler == null) return null;
		return handler.handle(this, term, passByValue);
	}
	
	static {
		addFormulaHandler(new Handler<Predicate>() {
			@Override public Class<Predicate> getType() { return Predicate.class; }
			@Override public Object handle(LogicVisitor visitor, Predicate predicate,boolean passByValue) {
				Term[] terms = new Term[predicate.size()];
				
				for (int i=0; i < predicate.size(); i++) {
					terms[i] = (Term) predicate.getTerm(i).accept(visitor);
				}
				
				return new Predicate(predicate.predicate(), terms);
			}
		});
		addFormulaHandler(new Handler<ModuleFormula>() {
			@Override public Class<ModuleFormula> getType() { return ModuleFormula.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleFormula formula,boolean passByValue) {
				return new ModuleFormula(formula.module(), (Predicate) formula.predicate().accept(visitor), formula.adaptor());
			}
		});
		addFormulaHandler(new Handler<AND>() {
			@Override public Class<AND> getType() { return AND.class; }
			@Override public Object handle(LogicVisitor visitor, AND and,boolean passByValue) {
				return new AND((Formula) and.left().accept(visitor), (Formula) and.right().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<OR>() {
			@Override public Class<OR> getType() { return OR.class; }
			@Override public Object handle(LogicVisitor visitor, OR or,boolean passByValue) {
				return new OR((Formula) or.left().accept(visitor), (Formula) or.right().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<NOT>() {
			@Override public Class<NOT> getType() { return NOT.class; }
			@Override public Object handle(LogicVisitor visitor, NOT not,boolean passByValue) {
				return new NOT((Formula) not.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Goal>() {
			@Override public Class<Goal> getType() { return Goal.class; }
			@Override public Object handle(LogicVisitor visitor, Goal goal,boolean passByValue) {
				return new Goal((Predicate) goal.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<FormulaVariable>() {
			@Override public Class<FormulaVariable> getType() { return FormulaVariable.class; }
			@Override public Object handle(LogicVisitor visitor, FormulaVariable variable,boolean passByValue) {
				return variable.variable().accept(visitor);
			}
		});
		addFormulaHandler(new Handler<BracketFormula>() {
			@Override public Class<BracketFormula> getType() { return BracketFormula.class; }
			@Override public Object handle(LogicVisitor visitor, BracketFormula formula,boolean passByValue) {
				return new BracketFormula((Formula) formula.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Bind>() {
			@Override public Class<Bind> getType() { return Bind.class; }
			@Override public Object handle(LogicVisitor visitor, Bind bind,boolean passByValue) {
				Bind b = new Bind(bind.variable(), (Term) bind.term().accept(visitor));
//				System.out.println("bind: " + b);
				return b;
			}
		});
		addFormulaHandler(new Handler<Comparison>() {
			@Override public Class<Comparison> getType() { return Comparison.class; }
			@SuppressWarnings("unchecked")
			@Override public Object handle(LogicVisitor visitor, Comparison comparison,boolean passByValue) {
				// Need to check if the bindings have been generated yet...
//				System.out.println("\tvisitor: " + visitor.getClass().getCanonicalName());
//				System.out.println("\tl: "+ comparison.left());
				Term il = (Term) comparison.left().accept(visitor);
//				System.out.println("\til: "+ il);
//				System.out.println("\tr: "+ comparison.right());
				Term ir = (Term) comparison.right().accept(visitor);
//				System.out.println("\tir: "+ ir);
				if (il instanceof Variable || ir instanceof Variable) return new Comparison(comparison.operator(), il,ir);
				
				if (Type.isNumeric(il) && Type.isNumeric(ir)) {
					Primitive<?> l = (Primitive<?>) il;
					Primitive<?> r = (Primitive<?>) ir;
					if (comparison.operator() == Comparison.LESS_THAN) {
						return Type.doubleValue(l) < Type.doubleValue(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.LESS_THAN_OR_EQUAL) {
						return Type.doubleValue(l) <= Type.doubleValue(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.GREATER_THAN) {
						return Type.doubleValue(l) > Type.doubleValue(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.GREATER_THAN_OR_EQUAL) {
						return Type.doubleValue(l) >= Type.doubleValue(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.EQUAL) {
						return Type.doubleValue(l) == Type.doubleValue(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.NOT_EQUAL || comparison.operator() == Comparison.OTHER_NOT_EQUAL) {
						return Type.doubleValue(l) != Type.doubleValue(r) ? Predicate.TRUE : Predicate.FALSE;
					}
	//			} else if (il.type() == Type.performativeType && ir.type() == Type.performativeType) {
	//				Performative l = (Performative) il;
	//				Performative r = (Performative) ir;
	//				if (comparison.operator() == Comparison.EQUAL) {
	//					return l.equals(r) ? Predicate.TRUE : Predicate.FALSE;
	//				} else if (comparison.operator() == Comparison.NOT_EQUAL) {
	//					return l.equals(r) ? Predicate.FALSE : Predicate.TRUE;
	//				}
				} else if (il.type() == Type.STRING && ir.type() == Type.STRING) {
					Primitive<String> l = (Primitive<String>) il;
					Primitive<String> r = (Primitive<String>) ir;
					if (comparison.operator() == Comparison.EQUAL) {
						return l.equals(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.NOT_EQUAL) {
	//					System.out.println("Comparison: " + comparison);
	//					System.out.println("result: " + l.equals(r));
						return l.equals(r) ? Predicate.FALSE : Predicate.TRUE;
					} else {
						System.out.println("unknown opeator: " + comparison.operator());
					}
				} else if (il.type() == Type.BOOLEAN && ir.type() == Type.BOOLEAN) {
				
					Primitive<?> l = (Primitive<?>) il;
					Primitive<?> r = (Primitive<?>) ir;
					if (comparison.operator() == Comparison.EQUAL) {
						return l.equals(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.NOT_EQUAL) {
						return l.equals(r) ? Predicate.FALSE : Predicate.TRUE;
					}
				} else if (il.type() == Type.LIST && ir.type() == Type.LIST) {
					ListTerm l = (ListTerm) il;
					ListTerm r = (ListTerm) ir;
					if (comparison.operator() == Comparison.EQUAL) {
						return l.equals(r) ? Predicate.TRUE : Predicate.FALSE;
					} else if (comparison.operator() == Comparison.NOT_EQUAL) {
						return l.equals(r) ? Predicate.FALSE : Predicate.TRUE;
					}
				} else {
					System.out.println("Comparison Not Supported: " + comparison);
					System.out.println("Left Type: " + il.type());
					System.out.println("Right Type: " + ir.type());
				}
				
				throw new RuntimeException("Problem comparing non-numeric terms in astra.plan.EvaluateVisitor: " + comparison);
			}
		});
		addTermHandler(new Handler<Primitive>() {
			@Override public Class<Primitive> getType() { return Primitive.class; }
			@Override public Object handle(LogicVisitor visitor, Primitive term,boolean passByValue) {
				return term;
			}
		});
		addTermHandler(new Handler<Funct>() {
			@Override public Class<Funct> getType() { return Funct.class; }
			@Override public Object handle(LogicVisitor visitor, Funct function,boolean passByValue) {
				Term[] terms = new Term[function.size()];
				
				for (int i=0; i < function.size(); i++) {
					terms[i] = (Term) function.getTerm(i).accept(visitor);
				}
				return new Funct(function.functor(), terms);
			}
		});
		addTermHandler(new Handler<ListTerm>() {
			@Override public Class<ListTerm> getType() { return ListTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ListTerm list, boolean passByValue) {
				if (passByValue) {
					ListTerm out = new ListTerm();
					for (int i=0; i < list.size(); i++) {
						out.add((Term) list.get(i).accept(visitor));
					}
					return out;
				} else {
					for (int i=0; i < list.size(); i++) {
						list.set(i, (Term) list.get(i).accept(visitor));
					}
					return list;
				}
			}
		});
		addTermHandler(new Handler<Operator>() {
			@Override public Class<Operator> getType() { return Operator.class; }
			@Override public Object handle(LogicVisitor visitor, Operator operator, boolean passByValue) {
				((AbstractEvaluateVisitor) visitor).passByValue=true;
				Term l = (Term) operator.left().accept(visitor);
				Term r = (Term) operator.right().accept(visitor);
				((AbstractEvaluateVisitor) visitor).passByValue=passByValue;

//				System.out.println("l: " + l);
//				System.out.println("r: " + r + " / " + r.getClass().getCanonicalName());
//				System.out.println("term types: " + l.type() + " / " + r.type());
//				System.out.println("operator type: " + operator.type());

				if (l instanceof Variable || r instanceof Variable) return Operator.newOperator(operator.op(), l, r);
//				if (r instanceof Variable) {
//					System.out.println("Right hand operand is unbound: " + r);
//					System.exit(0);
//				}
				
				if (operator.type().equals(Type.STRING)) {
					try {
						if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.stringValue(l) + Type.stringValue(r));
					} catch (Throwable th) {
						th.printStackTrace();
						System.exit(0);
					}
				} else if (operator.type().equals(Type.LIST)) {
					if (operator.op() == Operator.PLUS) {
						return ((ListTerm) l).merge((ListTerm) r);
					}
				} else if (operator.type().equals(Type.INTEGER)) {
					if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.integerValue(l) + Type.integerValue(r)); 
					if (operator.op() == Operator.MINUS) return Primitive.newPrimitive(Type.integerValue(l) - Type.integerValue(r)); 
					if (operator.op() == Operator.MULTIPLY) return Primitive.newPrimitive(Type.integerValue(l) * Type.integerValue(r)); 
					if (operator.op() == Operator.DIVIDE) return Primitive.newPrimitive(Type.integerValue(l) / Type.integerValue(r)); 
					if (operator.op() == Operator.MODULO) return Primitive.newPrimitive(Type.integerValue(l) % Type.integerValue(r)); 
				} else if (operator.type().equals(Type.LONG)) {
//					System.out.println("l=" + l + " / r=" + r + " / p(l)=" + Type.longValue(l) + " / p(r)=" + Type.longValue(r));
					if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.longValue(l) + Type.longValue(r)); 
					if (operator.op() == Operator.MINUS) return Primitive.newPrimitive(Type.longValue(l) - Type.longValue(r)); 
					if (operator.op() == Operator.MULTIPLY) return Primitive.newPrimitive(Type.longValue(l) * Type.longValue(r)); 
					if (operator.op() == Operator.DIVIDE) return Primitive.newPrimitive(Type.longValue(l) / Type.longValue(r)); 
					if (operator.op() == Operator.MODULO) return Primitive.newPrimitive(Type.longValue(l) % Type.longValue(r)); 
				} else if (operator.type().equals(Type.FLOAT)) {
					if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.floatValue(l) + Type.floatValue(r)); 
					if (operator.op() == Operator.MINUS) return Primitive.newPrimitive(Type.floatValue(l) - Type.floatValue(r)); 
					if (operator.op() == Operator.MULTIPLY) return Primitive.newPrimitive(Type.floatValue(l) * Type.floatValue(r)); 
					if (operator.op() == Operator.DIVIDE) return Primitive.newPrimitive(Type.floatValue(l) / Type.floatValue(r)); 
				} else if (operator.type().equals(Type.DOUBLE)) {
					if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.doubleValue(l) + Type.doubleValue(r)); 
					if (operator.op() == Operator.MINUS) return Primitive.newPrimitive(Type.doubleValue(l) - Type.doubleValue(r)); 
					if (operator.op() == Operator.MULTIPLY) return Primitive.newPrimitive(Type.doubleValue(l) * Type.doubleValue(r)); 
					if (operator.op() == Operator.DIVIDE) return Primitive.newPrimitive(Type.doubleValue(l) / Type.doubleValue(r)); 
				}

				return null;
			}
		});
		addTermHandler(new Handler<Performative>() {
			@Override public Class<Performative> getType() { return Performative.class; }
			@Override public Object handle(LogicVisitor visitor, Performative performative, boolean passByValue) {
				return performative;
			}
		});
		addTermHandler(new Handler<FormulaTerm>() {
			@Override public Class<FormulaTerm> getType() { return FormulaTerm.class; }
			@Override public Object handle(LogicVisitor visitor, FormulaTerm formula, boolean passByValue) {
				if (formula.value() != null) formula.value().accept(visitor);
				return null;
			}
		});
		addTermHandler(new Handler<Brackets>() {
			@Override public Class<Brackets> getType() { return Brackets.class; }
			@Override public Object handle(LogicVisitor visitor, Brackets term, boolean passByValue) {
				return term.term().accept(visitor);
			}
		});
		addTermHandler(new Handler<ListSplitter>() {
			@Override public Class<ListSplitter> getType() { return ListSplitter.class; }
			@Override public Object handle(LogicVisitor visitor, ListSplitter term, boolean passByValue) {
				return term;
			}
		});
		addTermHandler(new Handler<Count>() {
			@Override public Class<Count> getType() { return Count.class; }
			@Override public Object handle(LogicVisitor visitor, Count count, boolean passByValue) {
//				System.out.println("(AEV) Handling count: " + count);
				Term c = count.term();
				if (c instanceof Variable) {
					c = (Term) count.term().accept(visitor);
				}
				if (c instanceof ListTerm) {
					return Primitive.newPrimitive(((ListTerm) c).size());
				}
				return count;
			}
		});
	}
	
//	@Override
//	public Object visit(AcreHistory history) {
//		history.getConversationId().accept(this);
//		history.getIndex().accept(this);
//		history.getType().accept(this);
//		history.getContent().accept(this);
//		return null;
//	}
}
