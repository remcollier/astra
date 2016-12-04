package astra.statement;



public interface Statement {

	public StatementHandler getStatementHandler();

	public boolean isLinkedToSource();

	public int beginLine();
	public int endLine();
	public String getASTRAClass();
}
