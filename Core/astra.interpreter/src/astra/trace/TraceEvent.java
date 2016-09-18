package astra.trace;

import java.util.Date;

import astra.core.Agent;

public class TraceEvent {
	public static final String END_OF_CYCLE	= "end-of-cycle";
	public static final String NEW_AGENT = "new-agent";
	
	private String type;
	private Date timestamp;
	private Agent source;
	
	public TraceEvent(String type, Date timestamp, Agent source) {
		this.type = type;
		this.timestamp = timestamp;
		this.source = source;
	}
	
	public String type() {
		return type;
	}
	
	public Date timestamp() {
		return timestamp;
	}
	
	public Agent source() {
		return source;
	}
}
