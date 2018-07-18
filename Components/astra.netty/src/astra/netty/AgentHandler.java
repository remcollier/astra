package astra.netty;

import java.io.IOException;
import java.util.List;

import astra.core.Agent;
import astra.event.GoalEvent;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.netty.server.Handler;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;

public class AgentHandler implements Handler {
	private Agent agent;

	public AgentHandler(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String[] path = request.uri().split("/");
		ListTerm args = new ListTerm();
		if (path.length > 2) {
			for (int i = 2; i < path.length; i++) {
				args.add(Primitive.newPrimitive(path[i]));
			}
		}
		
		if (request.method() == HttpMethod.GET) {
			agent.addEvent(new GETEvent(Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args));
		} else if (request.method() == HttpMethod.POST) {
			agent.addEvent(new POSTEvent(Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args, getFields(request)));
		} else if (request.method() == HttpMethod.PUT) {
			agent.addEvent(new PUTEvent(Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args, getFields(request)));
		} else if (request.method() == HttpMethod.DELETE) {
			agent.addEvent(new DELETEEvent(Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args));
		} else {
			agent.addEvent(new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate(path[2], new Term[] {
					Primitive.newPrimitive(request.method().toString()), Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args }))));
		}
	}

	private Term getFields(FullHttpRequest request) throws IOException {
		ListTerm fields = new ListTerm();
		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
	    List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
	    for (InterfaceHttpData data : datas){
	    	fields.add(new Funct(data.getName(), new Term[] { Primitive.newPrimitive(((MixedAttribute) data).getValue())}));
	    }
		return fields;
	}

}
