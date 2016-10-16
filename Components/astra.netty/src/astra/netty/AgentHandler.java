package astra.netty;

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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;

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
			for (int i = 3; i < path.length; i++) {
				args.add(Primitive.newPrimitive(path[i]));
			}
		}
		
		if (request.method() == HttpMethod.POST || request.method() == HttpMethod.PUT) {
			ListTerm fields = new ListTerm();
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
		    List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
		    for (InterfaceHttpData data : datas){
		    	fields.add(new Funct(data.getName(), new Term[] { Primitive.newPrimitive(((MixedAttribute) data).getValue())}));
		    }
			agent.addEvent(new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate(path[2], new Term[] {
					Primitive.newPrimitive(request.method().toString()), Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args, fields}))));
			
		} else {
			agent.addEvent(new GoalEvent(GoalEvent.ADDITION, new Goal(new Predicate(path[2], new Term[] {
					Primitive.newPrimitive(request.method().toString()), Primitive.newPrimitive(ctx), Primitive.newPrimitive(request), args }))));
		}
	}

}
