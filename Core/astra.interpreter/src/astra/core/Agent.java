package astra.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import astra.acre.ACREService;
import astra.acre.AcreAPI;
import astra.cartago.CartagoAPI;
import astra.eis.EISAgent;
import astra.eis.EISService;
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
import is.lill.acre.message.ACREMessage;
import is.lill.acre.protocol.ProtocolManager;

public class Agent {
	// Agent Registry
	private static Map<String, Agent> agents = new HashMap<String,Agent>();

	public static Agent getAgent(String name) {
		return agents.get(name);
	}

	public static boolean hasAgent(String name) {
		return agents.containsKey(name);
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
	private Set<String> tokens = new HashSet<String>();
    private Map<String, LinkedList<Intention>> lockQueueMap = new HashMap<String, LinkedList<Intention>>();
    private Map<String, Intention> lockMap = new HashMap<String, Intention>();
	
    // Event Queue
	private Set<String> filter = new HashSet<String>();
	private Queue<Event> eventQueue = new LinkedList<Event>();
	
	// Intention Management
	private Queue<Notification> completed = new LinkedList<Notification>();
	private Queue<Intention> intentions = new LinkedList<Intention>();
    
	// TR Functions
	private List<Predicate> activeFunctions = new LinkedList<Predicate>();

	// Class Hierarchy
    private ASTRAClass clazz;
	private Map<String, Fragment> linearization = new HashMap<String, Fragment>();

	// Reasoning Engine
	private Reasoner reasoner;
	private EventBeliefManager beliefManager;
	
	private List<SensorAdaptor> sensorArray = new LinkedList<SensorAdaptor>();

	// CARTAGO Interface
	private CartagoAPI cartagoAPI;
	
	// ACRE Interface
	private AcreAPI acreAPI;
	public static final ProtocolManager protocolManager = new ProtocolManager();
	private boolean useAcre = false; 

	// EIS Interface
	private Map<String, EISAgent> eisAgents = new HashMap<String, EISAgent>();
	private String defaultEnvironment;
	private boolean debugging = false;
	
	/**
	 * This class models the notifications that are generated when an asynchronously executed action
	 * completes. Receipt of an instance of this class allows the agent to resume or fail the associated
	 * intention.
	 * 
	 * @author Rem Collier
	 *
	 */
	public static class Notification {
		Intention Intention;
		String message;
		Throwable th;

		public Notification(Intention Intention, String message) {
			this.Intention = Intention;
			this.message = message;
		}

		public Notification(Intention Intention, String message, Throwable th) {
			this.Intention = Intention;
			this.message = message;
			this.th = th;
		}
		
		public void evaluate() {
			if (message != null) {
				Intention.failed(message, th);
			}
			Intention.resume();
		}
	}
	
	public Agent(String name) {
		this.name = name;
		beliefManager = new EventBeliefManager(this); 
		reasoner = new ResolutionBasedReasoner(this);
		reasoner.addSource(beliefManager);
		agents.put(name, this);
		
		acreAPI = new AcreAPI(this);
		reasoner.addSource(acreAPI);
	}
	
	public boolean linkToEISService(String id, EISService service) {
		if (eisAgents.containsKey(id)) return false;
		
		EISAgent agt = new EISAgent(this, service);
		synchronized (reasoner) {
			reasoner.addSource(agt);
			eisAgents.put(id, agt);
			defaultEnvironment = id;
		}
		return true;
	}
	
	public void linkToCartago() {
		cartagoAPI = new CartagoAPI();
		cartagoAPI.start(this);
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

	public void handleEvent(Event event) {
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
				if (fragment.getASTRAClass().handleEvent(event, this)) return;
			}
	
		} catch (Throwable e) {
			System.err.println("Problem generating linearisation of: " + clazz.getClass().getCanonicalName());
			e.printStackTrace();
		}
		
		if (event.getSource() != null) {
			System.err.println("Event: " + event +" was not handled");
			((Intention) event.getSource()).failed("Event was not matched to rule: " + event, null);
			((Intention) event.getSource()).resume();
		} else {
			if (debugging ) System.err.println("Event: " + event +" was not handled");
		}
	}

	public void execute() {
		for (SensorAdaptor adaptor : sensorArray) {
			adaptor.sense(this);
		}
		
		this.beliefManager.update();

		// remove finished protocols from the active list
        if (useAcre) acreAPI.update();
		
		synchronized (completed) {
			while (!completed.isEmpty()) {
				Notification notif = completed.poll();
				if (notif != null) notif.evaluate();
			}
		}
		
        synchronized (this) {
	        if (!eventQueue.isEmpty()) {
				handleEvent(eventQueue.poll());
			}
        }
        
		if (!intentions.isEmpty()) {
			intention = getNextIntention();
			if (intention != null) {
				if (intention.isFailed()) {
					if (intention.rollback()) {
						intentions.add(intention);
					} else {
						intention.printStackTrace();
					}
				} else {
					if (intention.execute()) {
						intentions.add(intention);
					}
				}
				intentions.poll();
			}			
		}

		// Execute active functions
		for (Predicate predicate : activeFunctions) {
			new TRContext(this, predicate).execute();
		}
	}
	
	private synchronized Intention getNextIntention() {
		if (intentions.isEmpty()) return null;
		
		int i = 0;
		while (i < intentions.size() && intentions.peek().isSuspended()) {
			intentions.add(intentions.poll());
			i++;
		}
		
		if (i == intentions.size()) return null;
		return intentions.peek();
	}

	public List<Map<Integer, Term>> query(Formula formula, Map<Integer, Term> bindings) {
//		System.out.println("QUery: " + formula);
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
//		System.out.println("from: "+ message.sender + " to: " + message.receivers + " content: " + message.content);
        if (useAcre) {
        	ACREMessage m = ACREService.message( message );
            acreAPI.getConversationManager().processMessage( m );
        }
        
        // rebuild params...
        ListTerm list = new ListTerm();
        if (message.protocol != null) {
        	list.add(new Funct("protocol", new Term[] {Primitive.newPrimitive(message.protocol) }));
        }
        if (message.conversationId != null) {
        	list.add(new Funct("conversationId", new Term[] {Primitive.newPrimitive(message.conversationId) }));
        }
        addEvent( new MessageEvent( new Performative(message.performative), Primitive.newPrimitive( message.sender ), ContentCodec.getInstance().decode(message.content), list ) );
    }
	
	public synchronized void addEvent(Event event) {
//		System.out.println("[" + this.name + "] event: " + event);
		if (filter.contains(event.signature())) {
			synchronized (this) {
//				System.out.println("[" + name + "] state: " + state);
				if (state == INACTIVE) {
					state = RESCHEDULE;
				}
			}
			
			eventQueue.add(event);
			if (state == RESCHEDULE) {
//				System.out.println("rescheduling: " + name);
				Scheduler.schedule(this);
			}
		}
	}

	public Queue<Intention> intentions() {
		return intentions;
	}

	public void addSensorAdaptor(SensorAdaptor adaptor) {
		sensorArray.add(adaptor);
	}
	
	public synchronized void notifyDone(Notification notification) {
		completed.add(notification);
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
		if (!clazz.hasFunctions() && eisAgents.isEmpty()) {
			return !eventQueue.isEmpty() || !intentions.isEmpty() || (!sensorArray.isEmpty()) || beliefManager.hasUpdates() || !activeFunctions.isEmpty();
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

	public void startFunction(Predicate function) {
		if (!activeFunctions.contains(function)) activeFunctions.add(function);
	}

	public void stopFunction(Predicate function) {
		activeFunctions.remove(function);
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
		
	public CartagoAPI getCartagoAPI() {
		return cartagoAPI;
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

	public AcreAPI getAcreAPI() {
		return acreAPI;
	}

	public Map<String, EISAgent> eisAgents() {
		return eisAgents;
	}

	public EISAgent defaultEISAgent() {
		return eisAgents.get(defaultEnvironment);
	}
	
	public void defaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}

	public String defaultEnvironment() {
		return defaultEnvironment;
	}

	public Intention intention() {
		return intention;
	}

	public void useAcre(boolean useAcre) {
		this.useAcre = useAcre;
	}

	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
		
	}
}
