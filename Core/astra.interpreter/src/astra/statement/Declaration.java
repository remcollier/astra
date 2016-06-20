package astra.statement;

import astra.core.Intention;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;
import astra.term.Variable;

public class Declaration extends AbstractStatement {
	Variable variable;
	Term value;
	
	public Declaration(Variable variable, Term value) {
		this.variable = variable;
		this.value = value;
	}

	public Declaration(Variable variable, String clazz, int[] data, Term value) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.variable = variable;
		this.value = value;
	}

	public Declaration(Variable variable) {
		this.variable = variable;
	}

	public Declaration(Variable variable, String clazz, int[] data) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.variable = variable;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@Override
			public boolean execute(Intention context) {
				if (value == null) {
					context.addVariable(variable);
				} else {
					try {
						context.addVariable(variable, (Term) value.accept(new ContextEvaluateVisitor(context, true)));
					} catch (Throwable e) {
						context.failed("Variable Declaration Failed: " + variable, e);
						return false;
					}
				}
				
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return Declaration.this;
			}
			
			public String toString() {
				return variable.type() + " " + variable + ((value == null) ? "" : " = " + value);
			}
			
		};
	}
}
