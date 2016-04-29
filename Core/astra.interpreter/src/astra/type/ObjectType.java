package astra.type;

public class ObjectType extends Type {
	Class<?> subtype;
	
	public ObjectType(Class<?> subtype) {
		super("object");
		this.subtype = subtype;
	}

	public Class<?> subtype() {
		return subtype;
	}
}
