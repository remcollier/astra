package astra.eis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import astra.formula.Predicate;
import astra.term.Funct;
import astra.term.Primitive;
import astra.term.Term;
import eis.AgentListener;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.EnvironmentListener;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.EntityException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import eis.iilang.Percept;

public class EISService {
	private static Logger logger = Logger.getLogger(EISService.class.getCanonicalName());
	static {
		logger.setLevel(Level.OFF);
	}
    // Map to store active EIS instances for the given platform...
    private static Map<String, EISService> services = new HashMap<String, EISService>();
    
	public static synchronized EISService newService(String id, String jar) {
		EISService service = services.get(id);
		if (service == null) {
			service = new EISService(id);
			service.setup(jar);
			services.put(id, service);
		}
		return service;
	}

	public static synchronized EISService getService(String id) {
		return services.get(id);
	}
	
	public static Collection<EISService> getServices() {
		return services.values();
	}
	
	private EnvironmentInterfaceStandard ei = null;
	private Map<String, EISAgent> agents = new HashMap<String, EISAgent>();
	private String id;
	private Primitive<String> pid;
	
	private class EISEnvironmentListener implements EnvironmentListener {
		@Override
		public void handleNewEntity(String entity) {
			broadcastEvent(new EISEvent(EISEvent.ENVIRONMENT, new Funct("newEntity", new Term[] { Primitive.newPrimitive(entity) })));
		}

		@Override
		public void handleDeletedEntity(String entity, Collection<String> EISAgents) {
			broadcastEvent(new EISEvent(EISEvent.ENVIRONMENT, new Funct("deletedEntity", new Term[] { Primitive.newPrimitive(entity) })));
		}

		@Override
		public void handleFreeEntity(String entity, Collection<String> EISAgents) {
			broadcastEvent(new EISEvent(EISEvent.ENVIRONMENT, new Funct("freedEntity", new Term[] { Primitive.newPrimitive(entity) })));
		}

		@Override
		public void handleStateChange(EnvironmentState newState) {
			broadcastEvent(new EISEvent(EISEvent.ENVIRONMENT, new Funct("state", new Term[] { Primitive.newPrimitive(newState.toString()) })));
		}
	}
	
	public EISService(String id) {
		this.id = id;
		this.pid = Primitive.newPrimitive(id);
	}

	private void broadcastEvent(EISEvent event) {
		List<EISAgent> list = new ArrayList<EISAgent>();
		list.addAll(agents.values());
		for (EISAgent agent : list) {
			agent.addEvent(event);
		}
	}

	public boolean eisStart() {
		try {
			ei.start();
		} catch (ManagementException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean reset(Map<String, Parameter> parameters) {
		// TODO: Uncomment for 0.5 support
//		try {
//			ei.reset(parameters);
//		} catch (ManagementException e) {
//			e.printStackTrace();
//			return false;
//		}
		return true;
	}
	
	public boolean isStartSupported() {
		return ei.isStartSupported();
	}
	
	public String getEnvironmentState() {
		EnvironmentState state = ei.getState();
		if (state.equals(EnvironmentState.PAUSED)) {
			return "paused";
		} else if (state.equals(EnvironmentState.INITIALIZING)) {
			return "initializing";
		} else if (state.equals(EnvironmentState.RUNNING)) {
			return "running";
		} else if (state.equals(EnvironmentState.KILLED)) {
			return "killed";
		}
		return "unknown";
	}
	
    public boolean setup(String jarFile) {
		try {
			if (jarFile.startsWith("jar:")) {
				System.out.println("[UNSUPPORTED] Using URL-based download: " + jarFile);
//				ei = EILoader.fromJarFile(new URL(jarFile));
			} else {
				ei = EILoader.fromJarFile(new File(jarFile));
			}
			EISEnvironmentListener listener = new EISEnvironmentListener();
			ei.attachEnvironmentListener(listener);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
    }

	public boolean init(Map<String, Parameter> params) {
		try {
			ei.init(params);
		} catch (ManagementException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    public void registerAgent(final EISAgent agent) {
    	try {
			ei.registerAgent(agent.name());
			agents.put(agent.name(), agent);
			ei.attachAgentListener(agent.name(), new AgentListener() {

				@Override
				public void handlePercept(String arg0, Percept arg1) {
					System.out.println("agent: " +arg0 + " / percept: " +arg1.toProlog());
					
				}
				
			});
		} catch (AgentException e) {
			e.printStackTrace();
		}
    }

    public Map<String, Collection<Percept>> collectBeliefs(EISAgent agent) {
		try {
			if (ei.getState().equals(EnvironmentState.RUNNING)) {
				try {
					Collection<String> entities = ei.getAssociatedEntities(agent.name());
					if (!entities.isEmpty()) {
				    	return ei.getAllPercepts(agent.name(), entities.toArray(new String[entities.size()]));
					}
				} catch (AgentException e) {
					logger.log(Level.WARNING, "[" + agent.name() + "] Agent Problem: " + e.getMessage(), e);
				}
			}
		} catch (PerceiveException e) {
			logger.log(Level.WARNING, "[" + agent.name() + "] Perception Problem: " + e.getMessage(), e);
		} catch (NoEnvironmentException e) {
			logger.log(Level.WARNING, "[" + agent.name() + "] Environment Problem: " + e.getMessage(), e);
		}
		return new HashMap<String, Collection<Percept>>();
    }

    public void unregisterAgent(EISAgent EISAgent) {
    	try {
			ei.unregisterAgent(EISAgent.name());
		} catch (AgentException e) {
			e.printStackTrace();
		}
    }
    
    public EISAgent get(String name) {
    	return agents.get(name);
    }
    
    public Collection<String> getAssociatedEntities(String name) throws AgentException {
    	return ei.getAssociatedEntities(name);
    }

    public Collection<String> getAssociatedAgents(String name) throws EntityException {
    	return ei.getAssociatedAgents(name);
    }

    public Collection<String> getFreeEntities() {
    	return ei.getFreeEntities();
    }
        
    public Collection<String> getAllEntities() {
    	return ei.getEntities();
    }
        
    public boolean associateEntity(String name, String entity) {
    	try {
			ei.associateEntity(name, entity);
		} catch (RelationException e) {
			e.printStackTrace();
			return false;
		}
		return true;
    }

    public String queryEntityType(String entity) {
    	try {
			return ei.getType(entity);
		} catch (EntityException e) {
		}
		return null;
    }

    public Map<String, Percept> performAction(String agent, String entity, Action act) throws ActException, NoEnvironmentException {
		return ei.performAction(agent, act, entity);
    }

	public String id() {
		return id;
	}

	public Primitive<String> pid() {
		return pid;
	}

	public boolean hasAssociatedEntity(String name, String entity) {
		try {
			return ei.getAssociatedEntities(name).contains(entity);
		} catch (AgentException e) {
			e.printStackTrace();
			return false;
		}
	}
}
