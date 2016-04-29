package astra.messaging;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.messaging.AstraMessage;
import astra.term.Primitive;
import astra.term.Term;
import astra.util.ContentCodec;

public class MailboxClient {
	static final String SESSION_URL = "http://www.astralanguage.com/messaging/session";
	static final String MAILBOX_URL = "http://www.astralanguage.com/messaging/mailbox";
	
	static class FormulaInstanceCreator implements InstanceCreator<Formula> {
		public Formula createInstance(Type type) {
			System.out.println("Type: " + type);
			return new Predicate("", new Term[] {});
		}
	}

	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		MailboxClient client = new MailboxClient();
		

		
		// Check the sessions
		System.out.println(client.get(SESSION_URL));
	}

	private HttpClient client;
	private ResponseHandler<String> responseHandler;

	public MailboxClient() {
		client = HttpClientBuilder.create().build();
		responseHandler=new BasicResponseHandler();
	}

	public String post(String url, String json) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);

		StringEntity params =new StringEntity("data="+json);
        request.addHeader("content-type", "application/x-www-form-urlencoded");
        request.setEntity(params);

		//Execute and get the response.
		return client.execute(request, responseHandler);
	}

	public String get(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);

		//Execute and get the response.
		return client.execute(request, responseHandler);
	}
}