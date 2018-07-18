package astra.netty;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import astra.core.ActionParam;
import astra.core.Module;
import astra.event.Event;
import astra.netty.server.WebServer;
import astra.reasoner.Unifier;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class Http extends Module {
	static {
		Unifier.eventFactory.put(GETEvent.class, new GETEventUnifier());
		Unifier.eventFactory.put(POSTEvent.class, new POSTEventUnifier());
		Unifier.eventFactory.put(PUTEvent.class, new PUTEventUnifier());
		Unifier.eventFactory.put(DELETEEvent.class, new DELETEEventUnifier());
	}

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
	
	@EVENT(types = { "ChannelHandlerContext", "FullHttpRequest", "list" }, signature = "$ge", symbols = {})
	public Event get(Term context, Term request, Term arguments) {
		return new GETEvent(context, request, arguments);
	}

	@EVENT(types = { "ChannelHandlerContext", "FullHttpRequest", "list", "list" }, signature = "$pe", symbols = {})
	public Event post(Term context, Term request, Term arguments, Term fields) {
		return new POSTEvent(context, request, arguments, fields);
	}
	
	@EVENT(types = { "ChannelHandlerContext", "FullHttpRequest", "list", "list" }, signature = "$pte", symbols = {})
	public Event put(Term context, Term request, Term arguments, Term fields) {
		return new PUTEvent(context, request, arguments, fields);
	}
	
	@EVENT(types = { "ChannelHandlerContext", "FullHttpRequest", "list" }, signature = "$de", symbols = {})
	public Event delete(Term context, Term request, Term arguments) {
		return new DELETEEvent(context, request, arguments);
	}
	
	@ACTION
	public boolean plain_get(String url, ActionParam<String> response, ActionParam<Integer> code) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "ASTRA/1.0");
			int responseCode = connection.getResponseCode();
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer buf = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				buf.append(inputLine);
			}
			in.close();
			
			code.set(responseCode);
			response.set(buf.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@ACTION
	public boolean json_get(String url, ActionParam<Funct> response, ActionParam<Integer> code) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "ASTRA/1.0");
			int responseCode = connection.getResponseCode();
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer buf = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				buf.append(inputLine);
			}
			in.close();
			
			code.set(responseCode);
			// TODO: JSON conversion
//			response.set(buf.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
