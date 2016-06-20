package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.reasoner.util.StringMapper;
import astra.type.Type;

public class Variable implements Term {
	public static StringMapper mapper = new StringMapper();

	private Type type;
	private int id;
	
	public Variable(Type type, String name) {
		this.id = mapper.toId(name);
		this.type = type;
	}
	
	public Variable(Type type, int key) {
		this.id = key;
		this.type = type;
	}

	@Override
	public Type type() {
		return type;
	}
	
	public int id() {
		return id;
	}
	
	public String identifier() {
		return mapper.fromId(id);
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}
	
	public String toString() {
		return mapper.fromId(id) + " <" + id + ">"; 
	}

	@Override
	public boolean matches(Term right) {
		return type.equals(right.type());
	}

	@Override
	public String signature() {
		// TODO Auto-generated method stub
		return null;
	}

}
