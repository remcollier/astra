package astra.statement;

import astra.core.Intention;

public class SpecialOp extends AbstractStatement {
	SpecialOpAdaptor adaptor;
	
	public SpecialOp(String clazz, int[] data, SpecialOpAdaptor adaptor) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.adaptor = adaptor;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@Override
			public boolean execute(Intention context) {
				adaptor.invoke(context);
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return SpecialOp.this;
			}
			
		};
	}

}
