package astra.netty;

import java.io.File;
import java.io.IOException;

import astra.core.Module;
import astra.netty.server.WebServer;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

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
		return register("");
	}

	@ACTION
	public boolean register(String ctxt) {
		String context = (ctxt.startsWith("/") ? "":"/") + ctxt + (ctxt.isEmpty() || ctxt.endsWith("/") ? "":"/") + agent.name();
		System.out.println("Exposing agent: " + context);
		server.createContext(context, new AgentHandler(agent));
		return true;
	}

	@ACTION
	public boolean exportFolder(String base_url, String path) {
		System.out.println("Exposing folder: " + path + " under base_url: " + base_url);
		server.createContext(base_url, new FolderHandler(base_url, path));
		return true;
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

	@ACTION
	public boolean sendJSON(ChannelHandlerContext ctx, FullHttpRequest request, ListTerm list) {
		String response = encode(list);
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

	private static void sendResponse(ChannelHandlerContext ctx, FullHttpRequest request, String type, String text)
			throws IOException {
		WebServer.writeResponse(ctx, request, HttpResponseStatus.OK, type, text);
	}

	@ACTION
	public boolean loadView(ChannelHandlerContext ctx, FullHttpRequest request, String view) {
		try {
			String file_uri = "view" + view.replace('/', File.separatorChar);
			WebServer.writeFileResponse(ctx, request, file_uri);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
