package astra.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import astra.event.Event;
import astra.event.GoalEvent;
import astra.event.ScopedBeliefEvent;
import astra.event.ScopedGoalEvent;
import astra.formula.Formula;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.formula.ScopedGoal;
import astra.messaging.AstraMessage;
import astra.messaging.MessageEvent;
import astra.reasoner.Queryable;
import astra.reasoner.Reasoner;
import astra.reasoner.ResolutionBasedReasoner;
import astra.reasoner.util.ContentCodec;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Term;
import astra.tr.Function;
import astra.tr.TRContext;
import astra.trace.TraceEvent;
import astra.trace.TraceManager;

public class Agent {
	public static Map<String, Long> timings = Collections.synchronizedMap(new HashMap<String, Long>());
	public static Map<String, Long> iterations = Collections.synchronizedMap(new HashMap<String, Long>());
	
	private static final DecimalFormat df = new DecimalFormat("#.000000"); 

	/**
	 * Promises are used to implement WAIT and WHEN statements. When one of these
	 * statements is executed, the agent creates a promise and suspends the intention.
	 * Promises are evaluated on each iteration. When a promise is fulfilled (i.e. the
	 * associated formula is matched), the agent executes the associated act(ion) which
	 * typically resumes the intention.
	 * 
	 * @author Rem
	 *
	 */
	public static abstract class Promise {
		public Formula formula;
		public boolean isTrue;
		
		public Promise(Formula formula) {
			this(formula, false);
		}
		
		public Promise(Formula formula, boolean isTrue) {
			this.formula = formula;
			this.isTrue = isTrue;
		}

		public abstract void act(List<Map<Integer, Term>> bindings);
	}
	
	// Agent Registry
	private static Map<String, Agent> agents = new HashMap<String,Agent>();

	public static Agent getAgent(String name) {
		return agents.get(name);
	}

	public static boolean hasAgent(String name) {
		return agents.containsKey(name);
	}
	
	public static Set<String> agentNames() {
		return agents.keySet();
	}

	// Agent States
	public static final int NEW 									= 0;
	public static final int ACTIVE									= 1;
	public static final int INACTIVE								= 2;
	public static final int RESCHEDULE								= 3;
	public static final int TERMINATING								= 4;
	public static final int TERMINATED 								= 5;
	
	private String name;
	private int state = NEW;
	private Intention intention;
	
	// Synchronization Fields
	private Set<String> tokens = new TreeSet<String>();
    private Map<String, LinkedList<Intention>> lockQueueMap = new TreeMap<String, LinkedList<Intention>>();
    private Map<String, Intention> lockMap = new TreeMap<String, Intention>();
	
    // Event Queue
	private Set<String> filter = new TreeSet<String>();
	private Queue<Event> eventQueue = new LinkedList<Event>();
	
	// Intention Management
	private Queue<Notification> completed = new LinkedList<Notification>();
	private ArrayList<Intention> intentions = new ArrayList<Intention>();
	private int intentionNumber = 0;
    
	// Activated TR Function / null if no function active
	private Predicate trFunction;

	// Class Hierarchy
    private ASTRAClass clazz;
	private Map<String, Fragment> linearization = new TreeMap<String, Fragment>();

	// Reasoning Engine
	private Reasoner reasoner;
	private EventBeliefManager beliefManager;
	private List<Promise> promises = new ArrayList<Promise>();
	
	private List<SensorAdaptor> sensorArray = new LinkedList<SensorAdaptor>();

	// Message Listeners - listener pattern to notify other classes of
	// incoming/outgoing messages.
	private List<AgentMessageListener> messageListeners = new LinkedList<AgentMessageListener>();
	
	/**
	 * This class models the notifications that are generated when an asynchronously executed action
	 * completes. Receipt of an instance of this class allows the agent to resume or fail the associated
	 * intention.
	 * 
	 * @author Rem Collier
	 *
	 */
	public static class Notification {
		Intention intention;
		String message;
		Throwable th;

		public Notification(Intention intention, String message) {
			this.intention = intention;
			this.message = message;
		}

		public Notification(Intention Intention, String message, Throwable th) {
			this.intention = Intention;
			this.message = message;
			this.th = th;
		}
		
		public void evaluate() {
			if (message != null) {
				intention.failed(message, th);
			}
			intention.resume();
		}
	}
	
	public Agent(String name) {
		this.name = name;

		// initialize the timings table
		timings.put(name, 0l);
		iterations.put(name, 0l);
		
		beliefManager = new EventBeliefManager(this); 
		reasoner = new ResolutionBasedReasoner(this);
		reasoner.addSource(beliefManager);
		agents.put(name, this);
		
		TraceManager.getInstance().recordEvent(new TraceEvent(TraceEvent.NEW_AGENT, Calendar.getInstance().getTime(), this));
	}
	
	public void addSource(Queryable source) {
		reasoner.addSource(source);
	}
	
	public String name() {
		return name;
	}
	
	public void setMainClass(ASTRAClass clazz) throws ASTRAClassNotFoundException {
		this.clazz = clazz;

		List<ASTRAClass> list = clazz.getLinearization();
        for ( ASTRAClass claz : list ) {
            filter.addAll( claz.filter() );
            reasoner.addSource(claz);
        }
        
        Fragment prev = null;      
        for ( ASTRAClass claz : list ) {
        	claz.initialize(this);
        	Fragment fragment = claz.createFragment(this);
        	if (prev!=null) {
        		prev.next = fragment;
        	}
        	linearization.put(claz.getClass().getCanonicalName(), fragment);
        	prev = fragment;
        }
	}
	
	private List<ASTRAClass> filteredClassList(List<ASTRAClass> classList, String scope) throws ASTRAClassNotFoundException {
		for (ASTRAClass c : classList) {
			if (c.getCanonicalName().equals(scope) || c.getClass().getSimpleName().equals(scope)) {
				return c.getLinearization();
			}
		}
		return null;
	}

	public boolean handleEvent(Event event) {
//		System.out.println("["+getClass().getCanonicalName()+"] handling: "+ event);
		try {
			List<ASTRAClass> classList = clazz.getLinearization();
			
			if (event instanceof ScopedGoalEvent) {
				classList = filteredClassList(classList, ((ScopedGoalEvent) event).scopedGoal().scope());
			}

			if (event instanceof ScopedBeliefEvent) {
				classList = filteredClassList(classList, ((ScopedBeliefEvent) event).scope());
				event = ((ScopedBeliefEvent) event).beliefEvent();
			}
			
			for (ASTRAClass cls : classList) {
				Fragment fragment = linearization.get(cls.getClass().getCanonicalName());
				if (fragment.getASTRAClass().handleEvent(event, this)) return true;
			}
	
		} catch (Throwable e) {
			System.err.println("Problem generating linearisation of: " + clazz.getClass().getCanonicalName());
			e.printStackTrace();
		}
		
		if (event.getSource() != null) {
			System.err.println("Event: " + event +" was not handled");
			((Intention) event.getSource()).failed("Event was not matched to rule: " + event, null);
			((Intention) event.getSource()).resume();
//		} else {
//			System.err.println("Event: " + event +" was not handled");
		}
		return false;
	}

	private long sum = 0;
	private long count = 0;
	
	public void execute() {
		long start = System.currentTimeMillis();
		for (SensorAdaptor adaptor : sensorArray) {
			adaptor.sense(this);
		}
		
		this.beliefManager.update();

		for (int i=0; i<promises.size(); i++) {
			Promise promise = promises.get(i);
			List<Map<Integer, Term>> bindings = query(promise.formula, new HashMap<Integer, Term>());
			if ((promise.isTrue && (bindings == null)) || (!promise.isTrue && (bindings != null))) {
//				System.out.println("promise met: " + promise.formula);
				promises.remove(i).act(bindings);
			}
		}
		synchronized (completed) {
			while (!completed.isEmpty()) {
				Notification notif = completed.poll();
				if (notif != null) notif.evaluate();
			}
		}
		
        synchronized (this) {
	        while (!eventQueue.isEmpty() && !handleEvent(eventQueue.poll()));
        }
        
		if (!intentions.isEmpty()) {
			intention = getNextIntention();
//			System.out.println("["+name+"] Processing: " + intention.event);
			if (intention != null) {
				if (intention.isFailed()) {
					if (!intention.rollback()) {
						intention.printStackTrace();
						intentions.remove(intention);
					}
				} else {
					if (!intention.execute()) {
						intentions.remove(intention);
					}
				}
			}
		}

		// Execute active functions
		if (trFunction != null) {
			new TRContext(this, trFunction).execute();
		}
		
		TraceManager.getInstance().recordEvent(new TraceEvent(TraceEvent.END_OF_CYCLE, Calendar.getInstance().getTime(), this));

		// Record Interpreter Timings
		long duration = System.currentTimeMillis()-start;
//		if(duration > 0) {
			timings.put(name, sum = timings.get(name) + duration);
			iterations.put(name, count = iterations.get(name) + 1);
//		}
	}
	
	private synchronized Intention getNextIntention() {
		if (intentions.isEmpty()) return null;
		
//		System.out.println("Intention number: " + intentionNumber + "/" + intentions.size());
//		for (int i=0;i<intentions.size(); i++) {
//			System.out.println("("+i+") "+intentions.get(i).event);
//		}
		int i = 0;
		while (i < intentions.size() && intentions.get((i+intentionNumber) % intentions.size()).isSuspended()) {
			i++;
		}
		
		if (i == intentions.size()) return null;
		Intention intent = intentions.get((intentionNumber+i) % intentions.size());
		intentionNumber = (intentionNumber +i+1) % intentions.size();
		return intent;
	}

	public List<Map<Integer, Term>> query(Formula formula, Map<Integer, Term> bindings) {
		return reasoner.query(formula, bindings);
	}		

	public List<Map<Integer, Term>> queryAll(Formula formula) {
		return reasoner.queryAll(formula);
	}


	public void initialize(Goal goal) {
		eventQueue.add(new GoalEvent('+', goal));
	}

	public void initialize(ScopedGoal goal) {
		eventQueue.add(new ScopedGoalEvent('+', goal));
	}

	public void initialize(Predicate predicate) {
		beliefManager.addBelief(predicate);
	}

	public synchronized void addIntention(Intention intention) {
		intentions.add(intention);
	}

	public Module getModule(String classname, String key) {
		Fragment fragment = linearization.get(classname == null ? this.clazz.getCanonicalName():classname);
		for (ASTRAClass claz : fragment.getLinearization()) {
			fragment = linearization.get(claz.getClass().getCanonicalName());
			Module module = fragment.getModule(key);
            if (module != null) {
            	return module;
            }
		}
		return null;
	}

	public EventBeliefManager beliefs() {
		return beliefManager;
	}

	public synchronized void receive(AstraMessage message) {
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		System.out.println("from: "+ message.sender + " to: " + message.receivers + " content: " + message.content);
		for(AgentMessageListener listener : messageListeners) {
			listener.receive(message);
		}
		
        // rebuild params...
        ListTerm list = new ListTerm();
        if (message.protocol != null) {
        	list.add(new Funct("protocol", new Term[] {Primitive.newPrimitive(message.protocol) }));
        }
        if (message.conversationId != null) {
        	list.add(new Funct("conversation_id", new Term[] {Primitive.newPrimitive(message.conversationId) }));
        }
        
        addEvent( new MessageEvent( new Performative(message.performative), Primitive.newPrimitive( message.sender ), ContentCodec.getInstance().decode(message.content), list ) );
    }
	
	public synchronized void addEvent(Event event) {
//		System.out.println("[" + this.name + "] unfiltered event: " + event);
		if (filter.contains(event.signature())) {
			eventQueue.add(event);
			
//			System.out.println("[" + this.name + "] event: " + event);
//			System.out.println("[" + this.name + "] state: " + Scheduler.getState(this));
			
			// Checking if an intelligent scheduling strategy is being used
			// That pauses agents with nothing to do...
			if (Scheduler.getState(this) == Scheduler.WAITING) {
				Scheduler.setState(this, Scheduler.ACTIVE);
				Scheduler.schedule(this);
//				System.out.println("RESUMING: " + name);
			}
		}
		
		
	}

	public ArrayList<Intention> intentions() {
		return intentions;
	}

	public void addSensorAdaptor(SensorAdaptor adaptor) {
		sensorArray.add(adaptor);
	}
	
	public void notifyDone(Notification notification) {
		synchronized (completed) {
			completed.add(notification);
		}
	}

	public void schedule(Task task) {
		Scheduler.schedule(task);
	}

    public synchronized boolean hasLock( String token, Intention Intention ) {
        return Intention.equals( lockMap.get( token ) );
    }

    public synchronized boolean requestLock( String token, Intention Intention ) {
        if ( tokens.contains( token ) ) {
            // No lock, so queue it..
            lockQueueMap.get( token ).addLast( Intention );
            Intention.suspend();
            return false;
        }

        tokens.add( token );
        lockQueueMap.put( token, new LinkedList<Intention>() );
        lockMap.put( token, Intention );
        return true;
    }

    public synchronized void releaseLock( String token, Intention Intention) {
        if ( !tokens.contains( token ) ) {
            System.err.println( "[" + name() + "] Could not release lock on token: " + token );
        } else {
            if ( !lockMap.remove( token ).equals( Intention ) ) {
                System.out.println( "[ASTRAAgent.releaseLock()] Something strange: look at lock releasing" );
            }

            LinkedList<Intention> queue = lockQueueMap.get( token );
            if ( queue.isEmpty() ) {
                tokens.remove( token );
            }
            else {
                Intention ctxt = queue.removeFirst();
                lockMap.put( token, ctxt );
                ctxt.resume();
            }
        }
    }

	public void unrequestLock(String token, Intention Intention) {
        if ( !tokens.contains( token ) ) {
            System.err.println( "[" + name() + "] Could not unrequest lock on token: " + token );
        } else {
	        LinkedList<Intention> queue = lockQueueMap.get( token );
	        queue.remove(Intention);
	        if ( queue.isEmpty() ) {
	            tokens.remove( token );
	        }
        }		
	}
	
	public synchronized void state(int state) {
		this.state = state;
	}
	
	public synchronized int state() {
		return state;
	}
	
	public synchronized boolean isActive() {
		if (!clazz.hasFunctions()) {
			return !eventQueue.isEmpty() || !intentions.isEmpty() || (!sensorArray.isEmpty()) || beliefManager.hasUpdates() || (trFunction != null);
		}
		return true;
	}

	public synchronized void terminate() {
		state = TERMINATING;
		agents.remove(name);
	}
	
	public synchronized boolean isTerminating() {
		return state == TERMINATING;
	}
	
	public Queue<Event> events() {
		return eventQueue;
	}

	public boolean startFunction(Predicate function) {
		if (trFunction != null) return false;
		trFunction = function;
		return true;
	}

	public boolean stopFunction() {
		if (trFunction == null) return false;
		trFunction = null;
		return true;
	}

	public Function getFunction(Predicate predicate) {
		Function function;
		Fragment fragment = linearization.get(clazz.getClass().getCanonicalName());
		while (fragment != null) {
			function = fragment.getASTRAClass().getFunction(predicate);
            if (function != null) {
            	return function;
            }
			fragment = fragment.next;
		}
		return null;
	}

	public Plan getPlan(String scope, Predicate id) {
		Plan plan;
		Fragment fragment;
		try {
			List<ASTRAClass> classList = clazz.getLinearization();
			
			if (scope != null) {
				classList = filteredClassList(classList, scope); 
			}
			
			for (ASTRAClass cls : classList) {
				fragment = linearization.get(cls.getClass().getCanonicalName());
				plan = fragment.getASTRAClass().getPlan(id);
	            if (plan != null) {
	            	return plan;
	            }
			}
	
		} catch (ASTRAClassNotFoundException e) {
			System.err.println("Problem generating linearisation of: " + clazz.getClass().getCanonicalName());
			e.printStackTrace();
		}
		
		return null;
	}

	public ASTRAClass getASTRAClass() {
		return this.clazz;
	}

	public Intention intention() {
		return intention;
	}

	public void addAgentMessageListener(AgentMessageListener listener) {
		messageListeners.add(listener);
	}

	public void addPromise(Promise promise) {
		promises.add(promise);
	}

	public void dropPromise(Promise promise) {
		promises.remove(promise);
	}

	public boolean hasActiveFunction() {
		return this.trFunction != null;
	}
}
