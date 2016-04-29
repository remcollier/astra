package astra.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astra.formula.Formula;
import astra.reasoner.Queryable;

public class Fragment {

	private Map<String, Module> modules = new HashMap<String, Module>();
	ASTRAClass clazz;
	Fragment next;
	List<ASTRAClass> linearization;
	
//	FragmentBeliefManager manager;
	
	public Fragment(ASTRAClass clazz) throws ASTRAClassNotFoundException {
		this.clazz = clazz;
		linearization = clazz.getLinearization();
//		manager = new FragmentBeliefManager();
	}

	public List<ASTRAClass> getLinearization() {
		return linearization;
	}

	public void addModule(String name, Class<?> cls, Agent agent) {
		try {
			Module module = (Module) cls.newInstance();
			module.setAgent(agent);
			modules.put(name,  module);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
	}

	public void addModule(String name, String urn, Agent agent) {
		try {
			Module module = (Module) Class.forName(urn).newInstance();
			module.setAgent(agent);
			modules.put(name,  module);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}

	public ASTRAClass getASTRAClass() {
		return clazz;
	}

	public Module getModule(String key) {
		return modules.get(key);
	}

	public List<Formula> getMatchingFormulae(Formula predicate) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
