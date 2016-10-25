package astra.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SQLQuery {
	public static final String SELECT 											= "SELECT";
	public static final String INSERT											= "INSERT";
	public static final String UPDATE 											= "UPDATE";
	public static final String DELETE 											= "DELETE";
	
	private Map<String, String> where = new HashMap<String, String>();
	private Map<String, String> values = new HashMap<String, String>();
	private Set<String> fields = new HashSet<String>();
	private String type;
	private String table;
	
	public SQLQuery(String table) {
		this.table = table;
	}

	public void type(String type) {
		this.type = type;
	}
	
	public void table(String table) {
		this.table = table;
	}
	
	public void addWhere(String field, String value) {
		where.put(field, value);
	}
	
	public void addValue(String field, String value) {
		values.put(field, value);
	}
	
	public void addField(String field) {
		fields.add(field);
	}

	public String toSQL() {
		String out = "";
		if (type.equalsIgnoreCase(SELECT)) {
			out += SELECT;
			if (fields.isEmpty()) {
				out += " *";
			} else {
				boolean first = true;
				for (String field : fields) {
					if (first) first=false; else out+=",";
					out += " " + field;
				}
			}
			
			out += " FROM " + table;
			if (!where.isEmpty()) {
				out += " WHERE";
				boolean first = true;
				for (Entry<String, String> entry : where.entrySet()) {
					if (first) first=false; else out+=" AND";
					out += " " + entry.getKey() + "='" + entry.getValue()+"'";
				}
			}
		} else if (type.equalsIgnoreCase(INSERT)) {
			System.out.println("creating insert query...");
			out += INSERT + " INTO " + table + "(";
			String vals = "VALUES(";
			if (!values.isEmpty()) {
				boolean first = true;
				for (Entry<String, String> entry : values.entrySet()) {
					if (first) first=false; else {
						out+=",";
						vals+=",";
					}
					out += entry.getKey();
					vals+="'"+entry.getValue()+"'";
				}
			}
			out += ") " + vals + ")";
		} else {
			throw new UnsupportedOperationException("Invalid action: " + type);
		}
		return out;
	}
}