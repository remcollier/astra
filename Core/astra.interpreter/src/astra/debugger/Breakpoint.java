package astra.debugger;

public class Breakpoint implements Comparable<Breakpoint> {
	private int line;
	private String cls;
	
	public Breakpoint(String cls, int line) {
		this.line = line;
		this.cls = cls;
	}
	
	public int line() {
		return line;
	}
	
	public String getTargetClass() {
		return cls;
	}
	
	public int hashCode() {
		return cls.hashCode();
	}

	@Override
	public int compareTo(Breakpoint breakpoint) {
		return (line < breakpoint.line) ? -1:((line > breakpoint.line) ? 1:0);
	}
}
