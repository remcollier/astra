package astra.cartago;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.util.LogicVisitor;
import astra.term.Term;
import cartago.ArtifactId;

public class CartagoProperty implements Formula {
	ArtifactId aid;
	Predicate content;
	
	public CartagoProperty(ArtifactId aid, Predicate content) {
		this.aid = aid;
		this.content = content;
	}

	public CartagoProperty(Predicate content) {
		this.content = content;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public Predicate content() {
		return content;
	}

	public ArtifactId target() {
		return aid;
	}

	@Override
	public boolean matches(Formula formula) {
		if (formula instanceof CartagoProperty) {
			CartagoProperty p = (CartagoProperty) formula;
			return aid.equals(p.aid) && content.matches(p.content);
		}
		return false;
	}
	
	public String toString() {
		return "CARTAGO."+content.toString();
	}
}
