package astra.netty;

import astra.core.Agent;
import astra.event.GoalEvent;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.netty.server.Handler;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class AgentHandler implements Handler {
	private Agent agent;

	public AgentHandler(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String[] path = request.uri().split("/");
//		System.out.println("uri: " + request.uri());
		if (path.length < 3) {
			agent.addEvent(new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate("http_request", new Term[] {
					Primitive.newPrimitive(request.method().asciiName()), Primitive.newPrimitive(ctx), Primitive.newPrimitive(request) }))));
		} else {
			ListTerm args = new ListTerm();
			for (int i = 3; i < path.length; i++) {
				args.add(Primitive.newPrimitive(path[i]));
			}

//			System.out.println("predicate: " + path[1]);
			GoalEvent evt = null;
			agent.addEvent(evt=new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate(path[2], new Term[] {
					Primitive.newPrimitive(request.method().toString()), Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args }))));
//			System.out.println(evt.signature());
		}
	}

}
