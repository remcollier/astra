package astra.http;

import java.io.IOException;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import astra.core.Agent;
import astra.event.GoalEvent;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

public class AgentHandler implements HttpHandler {
	private Agent agent;

	public AgentHandler(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String[] path = uri.getPath().substring(1).split("/");
		if (path.length < 2) {
			agent.addEvent(new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate("http_request", new Term[] {
					Primitive.newPrimitive(exchange.getRequestMethod()), Primitive.newPrimitive(exchange) }))));
		} else {
			ListTerm args = new ListTerm();
			for (int i = 2; i < path.length; i++) {
				args.add(Primitive.newPrimitive(path[i]));
			}

			agent.addEvent(new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate(path[1], new Term[] {
					Primitive.newPrimitive(exchange.getRequestMethod()), Primitive.newPrimitive(exchange), args }))));
		}
	}

}
