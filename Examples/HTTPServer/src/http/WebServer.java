package http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import astra.core.Module;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

public class WebServer extends Module {
	public static HttpServer server;
	
	@ACTION
	public boolean setup() {
		return setup(9000);
	}
	
	@ACTION
	public boolean setup(int port) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			System.out.println("server started at " + port);
			server.setExecutor(null);
			server.start();		
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@ACTION
	public boolean register() {
		server.createContext("/"+agent.name(), new AgentHandler(agent));
		return true;
	}
	
	@ACTION
	public boolean sendHTML(HttpExchange exchange, String response) {
		try {
			exchange.sendResponseHeaders(200, response.length());
			sendResponse(exchange, response);
	        return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@ACTION
	public boolean sendJSON(HttpExchange exchange, Funct funct) {
		String response = encode(funct);
		try {
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, response.length());
			sendResponse(exchange, response);
	        return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static String encode(Term term) {
		if (term instanceof Funct) return encode((Funct) term);
		if (term instanceof ListTerm) return encode((ListTerm) term);
		if (term instanceof Primitive) {
			Object value = ((Primitive<?>) term).value();
			if (value instanceof String) return "\"" + value.toString() + "\"";
			if (value instanceof Character) return "\'" + value.toString() + "\'";
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
			if (first) first=false; else out += ", ";
			out += encode(term);
		}
		return out + " ]";
	}
	
	private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}
}
