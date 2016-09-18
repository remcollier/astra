package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class ModuleEvent extends AbstractElement implements IEvent {
	String symbol;
	String module;
	PredicateFormula event;
	
	public ModuleEvent(String symbol, String module, PredicateFormula event, Token start, Token end, String source) {
		super(start, end, source);
		
		this.symbol = symbol;
		this.module = module;
		this.event= event;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}
	
	public String symbol() {
		return symbol;
	}

	public String module() {
		return module;
	}

	public PredicateFormula event() {
		return event;
	}
	
	public String toString() {
		return (symbol==null ? "":symbol)+"$" + module + "." + event;
	}
	
	public String toSignature() {
		return (symbol==null ? "":symbol)+":"+module+":"+ event.toSignature();
	}
}
