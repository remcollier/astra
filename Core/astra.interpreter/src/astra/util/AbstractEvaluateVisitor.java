package astra.util;

import astra.cartago.CartagoProperty;
import astra.eis.EISFormula;
import astra.formula.AND;
import astra.formula.AcreFormula;
import astra.formula.Bind;
import astra.formula.BracketFormula;
import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Goal;
import astra.formula.Inference;
import astra.formula.IsNull;
import astra.formula.ModuleFormula;
import astra.formula.NOT;
import astra.formula.OR;
import astra.formula.Predicate;
import astra.formula.ScopedGoal;
import astra.term.Brackets;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListSplitter;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.Operator;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;

public abstract class AbstractEvaluateVisitor implements LogicVisitor {
	protected boolean passByValue=true;

	public AbstractEvaluateVisitor(boolean passByValue) {
		this.passByValue = passByValue;
	}

	@Override
	public Object visit(Primitive<?> term) {
		return term;
	}

	@Override
	public Object visit(Predicate predicate) {
		Term[] terms = new Term[predicate.size()];
		
		for (int i=0; i < predicate.size(); i++) {
			terms[i] = (Term) predicate.getTerm(i).accept(this);
		}
		return new Predicate(predicate.predicate(), terms);
	}

	@Override
	public Object visit(Funct function) {
		Term[] terms = new Term[function.size()];
		
		for (int i=0; i < function.size(); i++) {
			terms[i] = (Term) function.getTerm(i).accept(this);
		}
		return new Funct(function.functor(), terms);
	}

	@Override
	public Object visit(ListTerm list) {
		if (passByValue) {
			ListTerm out = new ListTerm();
			for (int i=0; i < list.size(); i++) {
				out.add((Term) list.get(i).accept(this));
			}
			return out;
		} else {
			for (int i=0; i < list.size(); i++) {
				list.set(i, (Term) list.get(i).accept(this));
			}
			return list;
		}
	}

	@Override
	public Object visit(EISFormula formula) {
		if (formula.id() != null) {
			return new EISFormula((Term) formula.id().accept(this), (Term) formula.entity().accept(this), (Predicate) formula.predicate().accept(this));
		} else if (formula.entity() != null) {
			return new EISFormula((Term) formula.entity().accept(this), (Predicate) formula.predicate().accept(this));
		}
		return new EISFormula((Predicate) formula.predicate().accept(this));
	}

	@Override
	public Object visit(ModuleFormula formula) {
		return new ModuleFormula(formula.module(), (Predicate) formula.predicate().accept(this), formula.adaptor());
	}
	
	@Override
	public Object visit(Operator operator) {
		Term l = (Term) operator.left().accept(this);
		Term r = (Term) operator.right().accept(this);

//		System.out.println("l: " + l);
//		System.out.println("r: " + r + " / " + r.getClass().getCanonicalName());
//		System.out.println("term types: " + l.type() + " / " + r.type());

		if (l instanceof Variable || r instanceof Variable) return Operator.newOperator(operator.op(), l, r);
//		if (r instanceof Variable) {
//			System.out.println("Right hand operand is unbound: " + r);
//			System.exit(0);
//		}
		
		if (operator.type().equals(Type.STRING)) {
			if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.stringValue(l) + Type.stringValue(r)); 
		} else if (operator.type().equals(Type.LIST)) {
			if (operator.op() == Operator.PLUS) return ((ListTerm) l).merge((ListTerm) r); 
		} else if (operator.type().equals(Type.INTEGER)) {
			if (operator.op() == Operator.PLUS) return Primitive.newPrimitive(Type.integerValue(l) + Type.integerValue(r)); 
			if (operator.op() == Operator.MINUS) return Primitive.newPrimitive(Type.integerValue(l) - Type.integerValue(r)); 
			if (operator.op() == Operator.MULTIPLY) return Primitive.newPrimitive(Type.integerValue(l) * Type.integerValue(r)); 
			if (operator.op() == Operator.DIVIDE) return Primitive.newPrimitive(Type.integerValue(l) / Type.integerValue(r)); 
			if (operator.op() == Operator.MODULO) return Primitive.newPrimitive(Type.integerValue(l) % Type.integerValue(r)); 
		} else if (operator.type().equals(Type.LONG)) {
//			System.out.println("l=" + l + " / r=" + r + " / p(l)=" + Type.longValue(l) + " / p(r)=" + Type.longValue(r));
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

	@SuppressWarnings("unchecked")
	@Override
	public Object visit(Comparison comparison) {
		// Need to check if the bindings have been generated yet...
		Term il = (Term) comparison.left().accept(this);
		Term ir = (Term) comparison.right().accept(this);
//		System.out.println("\til: "+ il);
//		System.out.println("\tir: "+ ir);
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
//		} else if (il.type() == Type.performativeType && ir.type() == Type.performativeType) {
//			Performative l = (Performative) il;
//			Performative r = (Performative) ir;
//			if (comparison.operator() == Comparison.EQUAL) {
//				return l.equals(r) ? Predicate.TRUE : Predicate.FALSE;
//			} else if (comparison.operator() == Comparison.NOT_EQUAL) {
//				return l.equals(r) ? Predicate.FALSE : Predicate.TRUE;
//			}
		} else if (il.type() == Type.STRING && ir.type() == Type.STRING) {
			Primitive<String> l = (Primitive<String>) il;
			Primitive<String> r = (Primitive<String>) ir;
			if (comparison.operator() == Comparison.EQUAL) {
				return l.equals(r) ? Predicate.TRUE : Predicate.FALSE;
			} else if (comparison.operator() == Comparison.NOT_EQUAL) {
//				System.out.println("Comparison: " + comparison);
//				System.out.println("result: " + l.equals(r));
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
	
	@Override
	public Object visit(AND and) {
		return new AND((Formula) and.left().accept(this), (Formula) and.right().accept(this));
	}

	@Override
	public Object visit(OR or) {
		return new OR((Formula) or.left().accept(this), (Formula) or.right().accept(this));
	}

	@Override
	public Object visit(NOT not) {
		return new NOT((Formula) not.formula().accept(this));
	}

	@Override
	public Object visit(Goal goal) {
		return new Goal((Predicate) goal.formula().accept(this));
	}
	
	public Object visit(ModuleTerm term) {
		return null;
	}

//	@Override
//	public Object visit(AcreHistory history) {
//		history.getConversationId().accept(this);
//		history.getIndex().accept(this);
//		history.getType().accept(this);
//		history.getContent().accept(this);
//		return null;
//	}

	@Override
	public Object visit(Performative performative) {
		return performative;
	}

	@Override
	public Object visit(CartagoProperty property) {
		return new CartagoProperty((Term) property.target().accept(this), (Predicate) property.content().accept(this));
	}

	@Override
	public Object visit(FormulaVariable variable) {
		return variable.variable().accept(this);
	}

	@Override
	public Object visit(FormulaTerm formula) {
		if (formula.value() != null) formula.value().accept(this);
		return null;
	}	

	@Override
	public Object visit(Inference inference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Variable variable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IsNull isNull) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ScopedGoal goal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AcreFormula acreFormula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BracketFormula formula) {
		return new BracketFormula((Formula) formula.formula().accept(this));
	}

	@Override
	public Object visit(Brackets term) {
		return term.term().accept(this);
	}
	
	@Override
	public Object visit(ListSplitter term) {
		return term;
	}
	
	@Override
	public Object visit(Bind bind) {
		Bind b = new Bind(bind.variable(), (Term) bind.term().accept(this));
//		System.out.println("bind: " + b);
		return b;
	}
}
