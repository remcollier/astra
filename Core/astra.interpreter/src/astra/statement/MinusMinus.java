package astra.statement;

import astra.core.Intention;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;

public class MinusMinus extends AbstractStatement {
	Variable variable;
	
	public MinusMinus(Variable variable) {
		this.variable = variable;
	}

	public MinusMinus(Variable variable, String clazz, int[] data, Term value) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.variable = variable;
	}

	public MinusMinus(Variable variable, String clazz, int[] data) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.variable = variable;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean execute(Intention context) {
				context.updateVariable(variable, Primitive.newPrimitive(((Primitive<Integer>) context.getValue(variable)).value().intValue()-1));
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return MinusMinus.this;
			}
			
			public String toString() {
				return variable + "++";
			}
			
		};
	}
}
