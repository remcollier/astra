package astra.tr;



public interface Action {

	public ActionHandler getStatementHandler();

	public boolean isLinkedToSource();

	public int beginLine();
}
