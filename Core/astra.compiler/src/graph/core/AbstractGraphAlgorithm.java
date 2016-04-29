package graph.core;

import graph.util.LinkedList;
import graph.util.List;

public abstract class AbstractGraphAlgorithm<V, E> implements GraphAlgorithm<V, E> {
	protected List<Parameter> parameterList; 

	public AbstractGraphAlgorithm() {
		parameterList = new LinkedList<Parameter>();
	}

	public List<Parameter> parameterList() {
		return parameterList;
	}
	
	protected void addParameter(Parameter parameter) {
		parameterList.insertLast(parameter);
	}

}