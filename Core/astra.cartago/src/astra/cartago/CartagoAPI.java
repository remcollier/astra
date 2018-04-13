package astra.cartago;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import astra.core.Agent;
import astra.core.Intention;
import astra.event.BeliefEvent;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;
import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoEvent;
import cartago.CartagoException;
import cartago.CartagoService;
import cartago.ICartagoListener;
import cartago.ICartagoSession;
import cartago.Op;
import cartago.OpFeedbackParam;
import cartago.Tuple;
import cartago.events.ActionFailedEvent;
import cartago.events.ActionSucceededEvent;
import cartago.events.ArtifactObsEvent;
import cartago.events.FocusSucceededEvent;
import cartago.events.FocussedArtifactDisposedEvent;
import cartago.events.JoinWSPSucceededEvent;
import cartago.events.QuitWSPSucceededEvent;
import cartago.events.StopFocusSucceededEvent;
import cartago.security.AgentIdCredential;

public class CartagoAPI implements ICartagoListener {
	private static Map<String, CartagoAPI> apis = new HashMap<String, CartagoAPI>();
	Agent agent;

	public static CartagoAPI create(Agent agent) {
		CartagoAPI cartagoAPI = new CartagoAPI(agent);
		apis.put(agent.name(), cartagoAPI);
		return cartagoAPI;
	}
	
	public static CartagoAPI get(Agent agent) {
		return apis.get(agent.name());
	}
	
	private static class OperationContext {
		Intention intention;
		Predicate action;

		public OperationContext(Intention intention, Predicate action) {
			this.intention = intention;
			this.action = action;
		}
	}
    private ICartagoSession session;
    private Map<Long, OperationContext> operationRegister = new HashMap<Long, OperationContext>();
    private ArtifactStore artifactStore = new ArtifactStore();
	
    private static Logger log = Logger.getLogger(CartagoAPI.class.getName());
    static {
    	log.setLevel(Level.INFO);
    }
    
	public CartagoAPI(Agent agent) {
		this.agent = agent;
		agent.addSource(artifactStore);
        try {
            session = CartagoService.startSession( "default", new AgentIdCredential( agent.name() ), this );
            log.info( "[" + agent.name() + "] Cartago Session created" );
        }
        catch ( CartagoException e1 ) {
            log.severe("[" + agent.name() + "] Problem creating Cartago Session" );
            e1.printStackTrace();
            System.exit(1);
        }
	}

    @Override
    public synchronized boolean notifyCartagoEvent( CartagoEvent ev ) {
//    	System.out.println("[" + agent.name() + "] Event: " + ev.getClass().getCanonicalName());
    	
        if ( ev instanceof ActionSucceededEvent ) {
            ActionSucceededEvent evt = (ActionSucceededEvent) ev;

//            System.out.println("success: " + evt.getActionId());
            OperationContext context = operationRegister.remove( evt.getActionId() );
            if (context == null) {
            	// we have a TR action result (so ignore for now)
            	return true;
            }
            
//           System.out.println("Handling: " + evt.getOp());
            // Handle update of the operation here...
    		// Create bindings for any unbound variables
    		Object paramValues[] = evt.getOp().getParamValues();

    		for (int i=0; i<context.action.size(); i++) {
    			Term term = context.action.termAt(i);
    			if (term instanceof Variable) {
    				Variable var = (Variable) term;
//    				System.out.println("\tEvaluating: " + var);
    				if (paramValues[i] instanceof OpFeedbackParam<?>){
    					OpFeedbackParam<?> feedbackParam = (OpFeedbackParam<?>) paramValues[i];
    					if (!var.type().equals(Type.getType(feedbackParam.get()))) {
    						// We have type incompatibility
    						context.intention.notifyDone("Incompatible type for variable: " + var + " Expected: " + var.type() + " but got: " + Type.getType(feedbackParam.get()), null);
    						return false;
    					}
    					if (var.type().equals(Type.LIST)) {
    						if (!context.intention.updateVariable(var, (ListTerm) feedbackParam.get())) {
    							context.intention.addVariable(var, (ListTerm) feedbackParam.get());
    						}
    					} else {
    						if (!context.intention.updateVariable(var, Primitive.newPrimitive(feedbackParam.get()))) {
    							context.intention.addVariable(var, Primitive.newPrimitive(feedbackParam.get()));
    						}
    					}
    				} else if (context.action.termAt(i) instanceof Variable) {
    					context.intention.notifyDone("[" + context.intention.name() + "] Unexpected variable in cartago operation call: " + term, null);
    					return false;
    				}
    			}
    		}
            context.intention.notifyDone(null);

            if ( ev instanceof FocusSucceededEvent ) {
                FocusSucceededEvent ev1 = (FocusSucceededEvent) ev;
                for ( ArtifactObsProperty prop : ev1.getObsProperties() ) {
                    Predicate property = toPredicate(prop);
                    artifactStore.storeObservableProperty( ev1.getArtifactId(), prop.getFullId(), property );

                    agent.addEvent( new CartagoPropertyEvent( CartagoPropertyEvent.ADDED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		property 
                    ) );
                }
            } else if ( ev instanceof StopFocusSucceededEvent ) {
                StopFocusSucceededEvent ev1 = (StopFocusSucceededEvent) ev;
                for ( ArtifactObsProperty prop : ev1.getObsProperties() ) {
                    artifactStore.removeObservableProperty( ev1.getArtifactId(), prop.getFullId() );

                    agent.addEvent( new CartagoPropertyEvent( CartagoPropertyEvent.REMOVED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		toPredicate(prop) 
                    ));
                }
                context.intention.notifyDone(null);
            } else if ( ev instanceof JoinWSPSucceededEvent ) {
                agent.addEvent( new BeliefEvent( BeliefEvent.ADDITION,
                		new Predicate("joinedWorkspace", new Term[] {
                				Primitive.newPrimitive(((JoinWSPSucceededEvent) ev).getWorkspaceId())
                		})
                ));
            }
        } else if ( ev instanceof ActionFailedEvent ) {
            ActionFailedEvent evt = (ActionFailedEvent) ev;
//            System.out.println("failed: " + evt.getActionId());
//            System.out.println(evt.getFailureMsg());
            OperationContext context = operationRegister.remove( evt.getActionId() );
            
            context.intention.notifyDone("CARTAGO Action failed: " + context.intention.getNextStatement() + ": " + evt.getFailureMsg());
        } else if ( ev instanceof FocussedArtifactDisposedEvent ) {
            FocussedArtifactDisposedEvent ev1 = (FocussedArtifactDisposedEvent) ev;
            for ( ArtifactObsProperty prop : ev1.getObsProperties() ) {
                artifactStore.removeObservableProperty( ev1.getArtifactId(), prop.getFullId() );

                agent.addEvent( new CartagoPropertyEvent( 
                		CartagoPropertyEvent.REMOVED, 
                		Primitive.newPrimitive( prop.getFullId() ), 
                		toPredicate(prop)
                ) );
            }
        }
        else if ( ev instanceof ArtifactObsEvent ) {
            ArtifactObsEvent evt = (ArtifactObsEvent) ev;

            Tuple signal = evt.getSignal();
            if ( signal != null ) {
            	createSignal(evt.getArtifactId(), signal);
            }

            if ( evt.getAddedProperties() != null ) {
                for ( ArtifactObsProperty prop : evt.getAddedProperties() ) {
                    Predicate property = toPredicate(prop);
                    artifactStore.storeObservableProperty( evt.getArtifactId(), prop.getFullId(), property );

                    agent.addEvent( new CartagoPropertyEvent( 
                    		CartagoPropertyEvent.ADDED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		property 
                    ) );
                }
            }

            if ( evt.getRemovedProperties() != null ) {
                for ( ArtifactObsProperty prop : evt.getRemovedProperties() ) {
                    artifactStore.removeObservableProperty( evt.getArtifactId(), prop.getFullId() );

                    agent.addEvent( new CartagoPropertyEvent( 
                    		CartagoPropertyEvent.REMOVED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		toPredicate(prop) 
                    ) );
                }
                artifactStore.removeArtifactProperties(evt.getArtifactId());
            }

            if ( evt.getChangedProperties() != null ) {
                for ( ArtifactObsProperty prop : evt.getChangedProperties() ) {
                    Predicate property = toPredicate(prop);
                    Predicate oldProperty = artifactStore.getObservableProperty(evt.getArtifactId(), prop.getFullId());
                    artifactStore.storeObservableProperty( evt.getArtifactId(), prop.getFullId(), property );

                    agent.addEvent( new CartagoPropertyEvent( 
                    		CartagoPropertyEvent.REMOVED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		oldProperty
                    ) );
                    
                    agent.addEvent( new CartagoPropertyEvent( 
                    		CartagoPropertyEvent.ADDED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		property 
                    ) );
                }
            }
        }
        else if ( ev instanceof QuitWSPSucceededEvent ) {
//            try {
//                currentWorkspace = getObjectId( session.getCurrentWorkspace() );
//            }
//            catch ( CartagoException e ) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
        else {
            System.out.println( "unhandled event: " + ev );
        }
        return true;
    }

    private void createSignal(ArtifactId artifactId, Tuple signal) {
    	List<Term> terms = new LinkedList<Term>();
        for ( Object obj : signal.getContents() ) {
        	if (obj != null) terms.add( Primitive.newPrimitive( obj ) );
        }
        
        agent.addEvent( new CartagoSignalEvent(
        		Primitive.newPrimitive(artifactId.getName()),
        		new Predicate( signal.getLabel(), terms.toArray( new Term[ terms.size() ] ) )
        ) );
    }

	private Predicate toPredicate(ArtifactObsProperty prop) {
        List<Term> list = new LinkedList<Term>();
        for ( Object obj : prop.getValues() ) {
            list.add( Primitive.newPrimitive( obj ) );
        }
        
        return new Predicate( prop.getName(), list.toArray( new Term[ list.size() ] ) );
	}

	public ICartagoSession getSession() {
        return this.session;
    }

	@SuppressWarnings("rawtypes")
	public LinkedList<Object> getArguments(Predicate activity) {
		Term[] terms = activity.terms();
		
		LinkedList<Object> list = new LinkedList<Object>();
		for (int i=0; i<terms.length; i++) {
			if (terms[i] instanceof Variable) {
				list.add(new OpFeedbackParam());
			} else if (terms[i] instanceof Primitive){
				Object value = ((Primitive) terms[i]).value();
				if (value instanceof List) {
					list.add(((List) value).toArray());
				} else {
					list.add(value);
				}
			}
		}
		return list;
	}
	
	public ArtifactStore store() {
		return artifactStore;
	}

	public synchronized void doOperation(ArtifactId artId, Op op, Intention context, Predicate activity) throws CartagoException {
		long id = session.doAction(artId,op, null, -1);
//		System.out.println("AID Registering: " + id);
		operationRegister.put(id, new OperationContext(context, activity ) );
	}

	public synchronized void doOperation(String name, Op op, Intention context, Predicate activity) throws CartagoException {
		long id = session.doAction(name, op, null, -1);
//		System.out.println("AStr Registering: " + id);
		operationRegister.put( id, new OperationContext(context, activity ) );
	}

	public synchronized void doOperation(Op op, Intention context, Predicate activity) throws CartagoException {
		long id = session.doAction(op, null, -1);
//		System.out.println("Blind Registering: " + id);
		operationRegister.put( id, new OperationContext(context, activity ) );
	}

}
