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
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoEvent;
import cartago.CartagoException;
import cartago.CartagoService;
import cartago.ICartagoListener;
import cartago.ICartagoSession;
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
	Agent agent;

    private ICartagoSession session;
    private Map<Long, Intention> operationRegister = new HashMap<Long, Intention>();
    private ArtifactStore artifactStore = new ArtifactStore();
	
    private static Logger log = Logger.getLogger(CartagoAPI.class.getName());
    static {
    	log.setLevel(Level.INFO);
    }
    
	public void start(Agent agent) {
		this.agent = agent;
		
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

            Intention context = operationRegister.remove( evt.getActionId() );
            if (context == null) {
            	// we have a TR action result (so ignore for now)
            	return true;
            }
            ((ICartagoStatementHandler) context.getNextStatement()).setOperation( evt.getOp() );
            context.notifyDone(null);

            if ( ev instanceof FocusSucceededEvent ) {
                FocusSucceededEvent ev1 = (FocusSucceededEvent) ev;
                for ( ArtifactObsProperty prop : ev1.getObsProperties() ) {
                    Predicate property = toPredicate(prop);
                    artifactStore.storeObservableProperty( ev1.getArtifactId(), prop.getFullId(), property );

                    agent.addEvent( new CartagoASTRAEvent( CartagoASTRAEvent.ADDED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		property 
                    ) );
                }
            }
            else if ( ev instanceof StopFocusSucceededEvent ) {
                StopFocusSucceededEvent ev1 = (StopFocusSucceededEvent) ev;
                for ( ArtifactObsProperty prop : ev1.getObsProperties() ) {
                    artifactStore.removeObservableProperty( ev1.getArtifactId(), prop.getFullId() );

                    agent.addEvent( new CartagoASTRAEvent( CartagoASTRAEvent.REMOVED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		toPredicate(prop) 
                    ));
                }
            }
            else if ( ev instanceof JoinWSPSucceededEvent ) {
                agent.addEvent( new BeliefEvent( BeliefEvent.ADDITION,
                		new Predicate("joinedWorkspace", new Term[] {
                				Primitive.newPrimitive(((JoinWSPSucceededEvent) ev).getWorkspaceId())
                		})
                ));
            }
        }
        else if ( ev instanceof ActionFailedEvent ) {
            ActionFailedEvent evt = (ActionFailedEvent) ev;
            Intention context = operationRegister.remove( evt.getActionId() );
            ((ICartagoStatementHandler) context.getNextStatement()).setOperation( evt.getOp() );
            context.notifyDone("CARTAGO Action failed: " + context.getNextStatement() + ": " + evt.getFailureMsg());
            
//            Tuple signal = evt.getFailureDescr();
//                
//            if (signal != null) { 
//            	createSignal(evt.getsignal);
//            }
        }
        else if ( ev instanceof FocussedArtifactDisposedEvent ) {
            FocussedArtifactDisposedEvent ev1 = (FocussedArtifactDisposedEvent) ev;
            for ( ArtifactObsProperty prop : ev1.getObsProperties() ) {
                artifactStore.removeObservableProperty( ev1.getArtifactId(), prop.getFullId() );

                agent.addEvent( new CartagoASTRAEvent( 
                		CartagoASTRAEvent.REMOVED, 
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

                    agent.addEvent( new CartagoASTRAEvent( 
                    		CartagoASTRAEvent.ADDED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		property 
                    ) );
                }
            }

            if ( evt.getRemovedProperties() != null ) {
                for ( ArtifactObsProperty prop : evt.getRemovedProperties() ) {
                    artifactStore.removeObservableProperty( evt.getArtifactId(), prop.getFullId() );

                    agent.addEvent( new CartagoASTRAEvent( 
                    		CartagoASTRAEvent.REMOVED, 
                    		Primitive.newPrimitive( prop.getFullId() ), 
                    		toPredicate(prop) 
                    ) );
                }
                artifactStore.removeArtifactProperties(evt.getArtifactId());
            }

            if ( evt.getChangedProperties() != null ) {
                for ( ArtifactObsProperty prop : evt.getChangedProperties() ) {
                    Predicate property = toPredicate(prop);
                    artifactStore.storeObservableProperty( evt.getArtifactId(), prop.getFullId(), property );

                    agent.addEvent( new CartagoASTRAEvent( 
                    		CartagoASTRAEvent.UPDATED, 
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
        
        Predicate p = null;
        agent.addEvent( new CartagoASTRAEvent(
        		CartagoASTRAEvent.SIGNAL,
        		Primitive.newPrimitive(artifactId.getName()),
        		p = new Predicate( signal.getLabel(), terms.toArray( new Term[ terms.size() ] ) )
        ) );
//        System.out.println("\tp=" + p);
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

    public synchronized void registerOperation( long actId, Intention context ) {
        operationRegister.put( actId, context );
    }

	@SuppressWarnings("rawtypes")
	public LinkedList<Object> getArguments(Predicate activity) {
		LinkedList<Object> list = new LinkedList<Object>();
		for (int i=0; i<activity.size(); i++) {
			if (activity.termAt(i) instanceof Variable) {
				list.add(new OpFeedbackParam());
			} else if (activity.termAt(i) instanceof Primitive){
				Object value = ((Primitive) activity.termAt(i)).value();
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
}
