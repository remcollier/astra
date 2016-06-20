package astra.statement;

import java.util.Map;

import astra.core.Intention;
import astra.formula.Formula;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;

public class If extends AbstractStatement {
	Formula guard;
	Statement ifStatement, elseStatement;
	
	public If(String clazz, int[] data, Formula guard, Statement ifStatement, Statement elseStatement) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.guard = guard;
		this.ifStatement = ifStatement;
		this.elseStatement = elseStatement;
	}
	
	public If(String clazz, int[] data, Formula guard, Statement ifStatement) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.guard = guard;
		this.ifStatement = ifStatement;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			@Override
			public boolean execute(Intention intention) {
				switch(state) {
				case 0:
					try {
						Map<Integer, Term> bindings = intention.query((Formula) guard.accept(new ContextEvaluateVisitor(intention)));
						if (bindings == null) {
							if (elseStatement == null) return false;
							intention.addStatement(elseStatement.getStatementHandler());
						} else {
							intention.addStatement(ifStatement.getStatementHandler(), bindings);
						}
					} catch (Throwable th) {
						intention.failed("Failure matching guard", th);
					}
					state = 1;
					return true;
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return If.this;
			}
			
			public String toString() {
				return "if (" + guard + ") ...";
			}
		};
	}

}
