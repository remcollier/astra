package astra.event;

public interface Event {
	public static final char ADDITION = '+';
	public static final char REMOVAL = '-';
	
	public Object getSource();
	
	public String signature();

}
