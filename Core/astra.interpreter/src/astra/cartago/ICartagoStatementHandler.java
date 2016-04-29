package astra.cartago;

import astra.statement.StatementHandler;
import cartago.Op;

public interface ICartagoStatementHandler extends StatementHandler {
	public void setOperation(Op op);
}
