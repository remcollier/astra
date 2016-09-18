package astra.gui;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import astra.core.Module;
import astra.event.Event;
import astra.reasoner.Unifier;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

public class GuiModule extends Module implements AstraEventListener {
	private AstraGui gui;

	static {
		Unifier.eventFactory.put(GuiEvent.class, new GuiEventUnifier());
	}
	
	@SuppressWarnings("unchecked")
	@ACTION
	public boolean launch(String className) {
		try {
			Class<AstraGui> clazz = (Class<AstraGui>) Class.forName(className);
			gui = clazz.newInstance();
			gui.launch(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void addEvent(String type, Object[] args) {
		ListTerm list = new ListTerm();
		for (Object object : args) {
			list.add(Primitive.newPrimitive(object));
		}
		agent.addEvent(new GuiEvent(Primitive.newPrimitive(type), list));
	}
	
	@ACTION
	public boolean updateGui(String type, ListTerm args) {
		return gui.receive(type, (List<?>) toPrimitive(args));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ACTION
	public boolean updateGui(Funct funct) {
		List list = new LinkedList();
		for(Term term : funct.terms()) {
			list.add(toPrimitive(term));
		}
		return gui.receive(funct.functor(), (List<?>) list);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object toPrimitive(Term term) {
		if (Primitive.class.isInstance(term)) {
			return ((Primitive<?>) term).value();
		}
		List list = new ArrayList();
		for (Term t : (ListTerm) term) {
			list.add(toPrimitive(t));
		}
		return list;
	}

	@EVENT( types = {"string", "list" }, signature="$gui:", symbols = {} )
	public Event event(Term id, Term args) {
		return new GuiEvent(id, (ListTerm) args);
	}
	
}
