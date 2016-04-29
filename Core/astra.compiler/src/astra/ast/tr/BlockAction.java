package astra.ast.tr;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IAction;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class BlockAction extends AbstractElement implements IAction {
	List<IAction> actions;
	
	public BlockAction(List<IAction> actions, Token start, Token end, String source) {
		super(start, end, source);
		this.actions = actions;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public List<IAction> actions() {
		return actions;
	}
}
