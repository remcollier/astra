package astra.reasoner.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.eis.EISAgent;
import astra.eis.EISFormula;
import astra.formula.AND;
import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.Inference;
import astra.formula.OR;
import astra.formula.Predicate;
import astra.reasoner.Queryable;
import astra.reasoner.ResolutionBasedReasoner;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;
import eis.iilang.Identifier;
import eis.iilang.Percept;

public class Test implements Queryable {
	List<Formula> formulae;
	public Test() {
		formulae = new LinkedList<Formula>();
		formulae.add(new Predicate("check", new Term[] {Primitive.newPrimitive("xxx"), Primitive.newPrimitive("yyy")}));
//		formulae.add(new Inference(new Predicate("free", new Term[] {new Variable(Type.STRING, "X")}),
//				new OR(
//						new EISFormula(new Predicate("square", new Term[] {new Variable(Type.STRING, "X"), Primitive.newPrimitive("empty")})),
//						new EISFormula(new Predicate("square", new Term[] {new Variable(Type.STRING, "X"), Primitive.newPrimitive("dusty")}))
//				)
////				new OR(
////						new Predicate("square", new Term[] {new Variable(Type.STRING, "X"), Primitive.newPrimitive("empty")}),
////						new Predicate("square", new Term[] {new Variable(Type.STRING, "X"), Primitive.newPrimitive("dusty")})
////				)
//		));
	}
	
	@Override
	public List<Formula> getMatchingFormulae(Formula predicate) {
		return formulae;
	}
	
	public static void main(String[] args) {
		ResolutionBasedReasoner reasoner = new ResolutionBasedReasoner(null);
//		EISAgent agt = new EISAgent(null);
//		agt.addPercept(new Percept("square", new Identifier("forward"), new Identifier("dusty")));
//		agt.completed();
		reasoner.addSource(new Test());
//		reasoner.addSource(agt);
//		List<Map<Integer, Term>> bindings = reasoner.query(new AND(
//				new Predicate("check", new Term[] {new Variable(Type.STRING, "X"), new Variable(Type.STRING, "Y")}),
//				new Comparison(Comparison.NOT_EQUAL, new Variable(Type.STRING, "X"), new Variable(Type.STRING, "Y"))
//		));
		List<Map<Integer, Term>> bindings = reasoner.query(new AND(
				new Predicate("check", new Term[] {Primitive.newPrimitive("xxx"), Primitive.newPrimitive("yyy")}),
				new Predicate("check", new Term[] {Primitive.newPrimitive("xxx"), Primitive.newPrimitive("yyy")})
		));
		System.out.println(bindings);
	}

}
