package astra.debugger;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import astra.core.Agent;
import astra.core.Rule;
import astra.event.Event;
import astra.formula.Formula;
import astra.statement.Block;
import astra.statement.Statement;
import astra.term.Term;
import astra.term.Variable;

public class Breakpoints {
	public static class State {
		public static final int PAUSED = 0;
		public static final int RUNNING = 1;
		public static final int STEPOVER = 2;
		public static final int STEPIN = 3;

		public static final int LEVEL_RULE = 0;
		public static final int LEVEL_CONTEXT = 1;
		public static final int LEVEL_STATEMENT = 2;
		
		int level;
		int mode = RUNNING;
	}
	
	private static Breakpoints instance=null;

	private Map<String, State> states = new HashMap<String, State>();
	private Map<String, List<Integer>> breakpoints = new HashMap<String, List<Integer>>();
	private DebuggerWorker worker=null;
	
	public static Breakpoints getInstance() {
		return (instance == null) ? instance = new Breakpoints():instance;
	}
	
	public void setWorker(DebuggerWorker _worker) {
		worker = _worker;
	}
	
	private State getState(Agent agent) {
		State state = states.get(agent.name());
		if (state == null) {
			states.put(agent.name(), state = new State());
		}
		return state;
	}
	
	public void check(Agent agent, Rule rule, Event _event, Map<Integer, Term> bindings) {
		State state = getState(agent);
		String clazz = rule.getASTRAClass();
		int line = -1; 
		
		// Rule is the top level - all step operations will stop on a rule
		if ((state.mode == State.STEPIN) || (state.mode == State.STEPOVER)) {
			line = rule.beginLine();
		} else {
			List<Integer> lines = breakpoints.get(clazz);
			if (lines != null) {
				for (int i=0; (line == -1) && (i < lines.size()); i++) {
					int l = lines.get(i);
					
					if (l > rule.endLine()) line = l;
					if (l >= rule.beginLine() && l <= rule.endLine()) {
						state.mode = State.PAUSED;
						state.level = State.LEVEL_RULE;
						line = l;
					}
				}
			}
		}
		
		if (line > -1) {
			worker.notify("BP\nRULE\n" + clazz + "\n" + line + "\n" + _event + "\n" + encode(bindings));
			pause();
		}
	}

	private synchronized void pause() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void check(Agent agent, Rule rule, Formula context, List<Map<Integer, Term>> results) {
		State state = getState(agent);
		String clazz = rule.getASTRAClass();
		int line = -1; 
		
		// Rule is the 2nd level - only a STEPIN will get here...
		if (state.mode == State.STEPIN) {
			line = rule.beginLine();
			worker.notify("BP\nCTXT\n" + clazz + "\n" + line + "\n" + context + "\n" + encode(results));
			pause();
		}
	}

	public void check(Agent agent, Statement statement) {
		if (statement instanceof Block) return;
		
		State state = getState(agent);
		String clazz = statement.getASTRAClass();
		int line = -1; 
		
		// Rule is the top level - all step operations will stop on a rule
		if ((state.mode == State.STEPIN) || ((state.mode == State.STEPOVER) && (state.level == State.LEVEL_STATEMENT))) {
			line = statement.beginLine();
		} else {
			List<Integer> lines = breakpoints.get(clazz);
			if (lines != null) {
				for (int i=0; (line == -1) && (i < lines.size()); i++) {
					int l = lines.get(i);
					
					if (l > statement.endLine()) line = l;
					if (l >= statement.beginLine() && l <= statement.endLine()) {
						state.mode = State.PAUSED;
						state.level = State.LEVEL_RULE;
						line = l;
					}
				}
			}
		}
		
		if (line > -1) {
			worker.notify("BP\nSTMT\n" + clazz + "\n" + line + "\n" + statement);
			pause();
		}
	}
	
	private String encode(Map<Integer, Term> bindings) {
		String out="";
		for (Entry<Integer, Term> entry : bindings.entrySet()) {
			if (out.length()>0) out+=" ";
			out += Variable.mapper.fromId(entry.getKey())+"="+entry.getValue().toString();
		}
		return out;
	}
	
	private String encode(List<Map<Integer, Term>> results) {
		String out = "";
		for (Map<Integer, Term> bindings :results) {
			if (out.length() > 0) out +="\n";
			out+=encode(bindings);
		}
		return null;
	}

	public void set(Class<?> clazz, int line) {
		set(clazz.getCanonicalName(), line);
	}

	public void set(String clazz, int line) {
		List<Integer> lines = breakpoints.get(clazz);
		if (lines == null) {
			breakpoints.put(clazz, lines = new LinkedList<Integer>());
		}
		lines.add(line);
		Collections.sort(lines);
	}

	public synchronized void stepIn(String name) {
		State state = getState(Agent.getAgent(name));
		state.mode = State.STEPIN;
		notify();
	}

	public synchronized void stepOver(String name) {
		State state = getState(Agent.getAgent(name));
		state.mode = State.STEPOVER;
		notify();
	}
}
