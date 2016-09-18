package astra.reasoner.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import astra.type.ObjectType;

public class ContentCodec {
	private static ContentCodec codec;
	
	public static ContentCodec getInstance() {
		if (codec == null) codec = new ContentCodec();
		return codec;
	}
	public String encode(Formula formula) {
		if (formula instanceof Predicate) return encode((Predicate) formula);
		return null;
	}
		
	private String encode(Predicate predicate) {
		String out = "{ \"predicate\":\""+predicate.predicate()+"\", \"terms\" : [";
		
		for (int i=0; i < predicate.size(); i++) {
			if (i > 0) out += ", ";
			String term = encode(predicate.termAt(i));
			if (term == null) {
				System.out.println("Whoops: " + predicate);
				return null;
			}
			out += term;
		}
			
		return out + "] }";
	}
	
	public String encode(Term term) {
		if (term instanceof Primitive<?>) return encode((Primitive<?>) term);
		if (term instanceof ListTerm) return encode((ListTerm) term);
		if (term instanceof Funct) return encode((Funct) term);
		System.out.println("term: " + term);
		return null;
	}
	
	private String encode(Primitive<?> primitive) {
		if (primitive.type() instanceof ObjectType) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(bout);
				out.writeObject(primitive.value());
				
				return  "{ \"type\":\"object\", \"value\" : \"" + bout.toString() + "\" }";
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return "{ \"type\":\"" + primitive.type().toString() +"\", \"value\":\""+primitive.value()+"\" }";
	}

	private String encode(ListTerm list) {
		String out = "{ \"type\" : \"list\", \"value\" : [ ";
		for (int i=0; i < list.size(); i++) {
			if (i > 0) out += ", ";
			out += encode(list.get(i));
		}
		return out + " ] }";
	}

	private String encode(Funct funct) {
		String out = "{ \"type\" : \"funct\", \"functor\" : \"" + funct.functor() + "\", \"value\" : [ ";
		for (int i=0; i < funct.size(); i++) {
			if (i > 0) out += ", ";
			out += encode(funct.getTerm(i));
		}
		return out + " ] }";
	}

	public Formula decode(String json) {
		JSONParser parser = new JSONParser();
		try {
			return decode((JSONObject) parser.parse(json));
		} catch (ParseException e) {
			System.err.println("Invalid JSON: " + json);
			e.printStackTrace();
			return null;
		}
	}
	
	public Formula decode(JSONObject jsonObject) {
		String predicate = (String) jsonObject.get("predicate");
		if (predicate != null) return decode_predicate(predicate, jsonObject);
		return null;
	}

	private Predicate decode_predicate(String predicate, JSONObject jsonObject) {
		JSONArray array = (JSONArray) jsonObject.get("terms");
		Term[] terms = new Term[array.size()];
		for (int i=0; i < array.size(); i++) {
			terms[i] = decode_term((JSONObject) array.get(i));
		}
		return new Predicate(predicate, terms);
	}

	private Term decode_term(JSONObject object) {
		String type = (String) object.get("type");
		if (type.equals("string")) return Primitive.newPrimitive((String) object.get("value"));
		if (type.equals("integer")) return Primitive.newPrimitive(Integer.parseInt((String) object.get("value")));
		if (type.equals("long")) return Primitive.newPrimitive(Long.parseLong((String) object.get("value")));
		if (type.equals("float")) return Primitive.newPrimitive(Float.parseFloat((String) object.get("value")));
		if (type.equals("double")) return Primitive.newPrimitive(Double.parseDouble((String) object.get("value")));
		if (type.equals("char")) return Primitive.newPrimitive(((String) object.get("value")).charAt(0));
		if (type.equals("boolean")) return Primitive.newPrimitive(Boolean.parseBoolean((String) object.get("value")));
		if (type.equals("list")) return decode_list((JSONArray) object.get("value"));
		if (type.equals("funct")) return decode_funct(object);
		if (type.equals("object")) return decode_object(object);
		return null;
	}

	private Term decode_object(JSONObject object) {
		ByteArrayInputStream bin = new ByteArrayInputStream(((String) object.get("value")).getBytes());
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(bin);
			Object obj = in.readObject();
			return Primitive.newPrimitive(obj);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	private Term decode_funct(JSONObject object) {
		String functor = (String) object.get("functor");
		JSONArray array = (JSONArray) object.get("value");
		Term[] terms = new Term[array.size()];
		for (int i=0; i < array.size(); i++) {
			terms[i] = decode_term((JSONObject) array.get(i));
		}
		return new Funct(functor, terms);
	}

	private Term decode_list(JSONArray array) {
		Term[] terms = new Term[array.size()];
		for (int i=0; i < terms.length; i++) {
			terms[i] = decode_term((JSONObject) array.get(i));
		}
		
		return new ListTerm(terms);
	}

	private static class Test implements Serializable {
		String value = "hello world";
//		public String toString() { return value; }
	}

	public static void main(String[] args) {
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive("happy")}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive(42)}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive(42l)}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive(42.0f)}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive(42.5)}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive('a')}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive(true)}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), new ListTerm(new Term[] {Primitive.newPrimitive(104), Primitive.newPrimitive(105)})}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), new Funct("test", new Term[] {Primitive.newPrimitive(true)})}));
		test_predicate(new Predicate("test", new Term[] {Primitive.newPrimitive("rem"), Primitive.newPrimitive(new Test())}));
	}
	
	private static void test_predicate(Predicate p) {
		String json = ContentCodec.getInstance().encode(p);
		System.out.println(json);

		Formula f = ContentCodec.getInstance().decode(json);
		System.out.println("predicate: " + f.toString());
	}

}
