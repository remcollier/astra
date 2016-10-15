package astra.netty;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import astra.core.Module;
import astra.netty.server.WebServer;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

public class Http extends Module {
	public static WebServer server;

	@ACTION
	public boolean setup() {
		return setup(9000);
	}

	@ACTION
	public boolean setup(int port) {
		server = new WebServer(port);
		System.out.println("server started at " + port);
		new Thread(new Runnable() {
			public void run() {
				try {
					server.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return true;
	}

	@ACTION
	public boolean register() {
		System.out.println("Exposing agent: /" + agent.name());
		server.createContext("/" + agent.name(), new AgentHandler(agent));
		return true;
	}

	@ACTION
	public boolean sendHTML(ChannelHandlerContext ctx, FullHttpRequest request, String text) {
		try {
	        sendResponse(ctx, request, WebServer.TYPE_HTML, text);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@ACTION
	public boolean sendJSON(ChannelHandlerContext ctx, FullHttpRequest request, Funct funct) {
		String response = encode(funct);
		try {
	        sendResponse(ctx, request, WebServer.TYPE_JSON, response);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String encode(Term term) {
		if (term instanceof Funct)
			return encode((Funct) term);
		if (term instanceof ListTerm)
			return encode((ListTerm) term);
		if (term instanceof Primitive) {
			Object value = ((Primitive<?>) term).value();
			if (value instanceof String)
				return "\"" + value.toString() + "\"";
			if (value instanceof Character)
				return "\'" + value.toString() + "\'";
			return value.toString();
		}
		return null;
	}

	@TERM
	public String stringParam(ChannelHandlerContext ctx, FullHttpRequest request, String name) {
//		Map<String, Object> parameters = (Map<String, Object>) exchange.getAttribute("parameters");
//		System.out.println("parameters: " + parameters);
//		return parameters.get(name).toString();
		return null;
	}

	private static String encode(Funct funct) {
		return "{ \"" + funct.functor() + "\" : " + encode(funct.termAt(0)) + " }";
	}

	private static String encode(ListTerm list) {
		String out = "[ ";
		boolean first = true;
		for (Term term : list) {
			if (first)
				first = false;
			else
				out += ", ";
			out += encode(term);
		}
		return out + " ]";
	}

	private static void sendResponse(ChannelHandlerContext ctx, FullHttpRequest request, String type, String text) throws IOException {
        WebServer.writeResponse(ctx, request, HttpResponseStatus.OK, type, text);
	}

	@ACTION
	public boolean sendView(ChannelHandlerContext ctx, FullHttpRequest request, String view) {
		try {
			File file = new File("view/" + view);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			HttpUtil.setContentLength(response, raf.length());
			ctx.write(response); 
			ctx.write(new DefaultFileRegion(raf.getChannel(), 0, file.length())); 
			ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);  
			raf.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
