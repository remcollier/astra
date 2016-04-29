package astra.statement;

import astra.core.Intention;
import astra.core.UnboundVariableException;
import astra.term.Term;
import astra.term.Variable;
import astra.util.ContextEvaluateVisitor;

public class Assignment extends AbstractStatement {
	Variable variable;
	Term value;
	
	public Assignment(Variable variable, Term value) {
		this.variable = variable;
		this.value = value;
	}

	public Assignment(Variable variable, String clazz, int[] data, Term value) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.variable = variable;
		this.value = value;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@Override
			public boolean execute(Intention intention) {
				try {
					intention.updateVariable(variable, (Term) value.accept(new ContextEvaluateVisitor(intention, true)));
				} catch (Throwable e) {
					intention.failed("Variable Assignment Failed: " + variable, e);
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return Assignment.this;
			}
			
			public String toString() {
				return variable.toString() + " = " + (value == null ? "" : value.toString());
			}
		};
	}
}
