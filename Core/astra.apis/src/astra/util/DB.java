package astra.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import astra.core.ActionParam;
import astra.core.Module;
import astra.core.ModuleException;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import astra.type.Type;

/**
 * This class implements support for Database Acccess via JDBC.
 * 
 * <p>
 * To use this class, you must also have the correct JDBC driver jar, and this must 
 * be installed on the project classpath.
 * </p>
 * <p>
 * This is an early release and so its functionality may change, but will definitely 
 * be extended in future releases.
 * <p>
 * Example Usage:
 * </p>
 * <p>
 * <code>
 * module DB db;<br/><br/>
 * rule +!main(list args) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;db.installDriver("org.sqlite.JDBC");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;db.getConnection("jdbc:sqlite:test.db", object<java.sql.Connection> connection);<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;if (db.tableExists(connection, "users")) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;db.rawUpdateQuery(connection, "DROP TABLE users");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;db.rawUpdateQuery(connection, "CREATE TABLE users (id INT PRIMARY KEY, username CHAR(50) NOT NULL, password CHAR(50) NOT NULL, email CHAR(50))");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;db.rawUpdateQuery(connection, "INSERT INTO users (username, password, email) VALUES('user', 'pass', 'user@mail.com')");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;db.rawSelectQuery(connection, "SELECT * FROM users", list results);<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;forall( list row : results) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C.println("row: " + row);<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;db.close(connection);<br/>
 * }
 * </code>
 * </p>
 * @author Rem Collier
 *
 */
public class DB extends Module {
	/**
	 * Action that installs a JDBC driver.
	 * 
	 * <p>
	 * The parameter is the fully qualified class name of the JDBC Driver
	 * </p>
	 * @param driver a qualified class name for a driver 
	 * @return
	 */
	@ACTION
	public boolean installDriver(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new ModuleException("Could not locate JDBC Driver", e);
		}
		return true;
	}
	
	/**
	 * Formula that checks if the provided table exists.
	 * 
	 * @param connection a SQL Connection
	 * @param table the name of the table
	 * @return
	 */
	@FORMULA
	public Formula tableExists(Connection connection, String table) {
		try {
			Statement stmt = connection.createStatement();
			ResultSet set = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'");
			int x = 0;
			while (set.next()) x++;
			stmt.close();
			return x > 0 ? Predicate.TRUE:Predicate.FALSE;
		} catch (SQLException e) {
			throw new ModuleException(e);
		}
	}
	
	/**
	 * Action to create a connection to a database.
	 * 
	 * @param connectionString the connection string
	 * @param connection a reference to an SQL Collection object.
	 * @return
	 */
	@ACTION
	public boolean getConnection(String connectionString, ActionParam<Connection> connection) {
		try {
			Connection c = DriverManager.getConnection(connectionString);
			connection.set(c);
		} catch (SQLException e) {
			throw new ModuleException("Could not connect to the database: ", e);
		}
		return true;
	}
	
	/**
	 * Action to close an existing SQL Connection
	 * 
	 * @param connection the SQL connection
	 * @return
	 */
	@ACTION
	public boolean close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new ModuleException("Could not close the connection: ", e);
		}
		return true;
	}

	/**
	 * Action to execute a basic SQL Query.
	 * 
	 * <p>
	 * This action is considered raw because it takes an SQL statement in string form as the input.
	 * </p>
	 * 
	 * @param connection and SQL connection
	 * @param sql the SQL update query
	 * @return
	 */
	@ACTION
	public boolean rawUpdateQuery(Connection connection, String sql) {
		try {
			Statement stmt = connection.createStatement();
			stmt.execute(sql);
			stmt.close();
		} catch (SQLException e) {
			throw new ModuleException("Error Executing Query: ", e);
		}
		return true;
	}

	/**
	 * Action that executes a basic select query on a connection.
	 * 
	 * <p>
	 * This action is considered raw because it takes an SQL statement in string form as the input.
	 * </p>
	 * 
	 * @param connection a SQL connection
	 * @param sql the SQL Query
	 * @param results a list that contains the results of the query (list of list)
	 * @return
	 */
	@ACTION
	public boolean rawSelectQuery(Connection connection, String sql, ActionParam<ListTerm> results) {
		try {
			Statement stmt = connection.createStatement();
			ResultSet set = stmt.executeQuery(sql);
			ResultSetMetaData md = set.getMetaData();
			
			ListTerm list = new ListTerm();
			while ( set.next() ) {
				ListTerm row = new ListTerm();
				for (int i=1; i <= md.getColumnCount(); i++) {
					if (md.getColumnTypeName(i).equals("INT")) {
						row.add(Primitive.newPrimitive(set.getInt(i)));
					} else if (md.getColumnTypeName(i).equals("CHAR")) {
						row.add(Primitive.newPrimitive(set.getString(i)));
					} else {
						System.out.println("column "+ i + ": " + md.getColumnTypeName(i));
					}
				}
				set.next();
				list.add(row);
			} 
			results.set(list);
			stmt.close();
		} catch (SQLException e) {
			throw new ModuleException("Error Executing Query: ", e);
		}
		return true;
	}
	
	// -----------------------------------------------------------------------------------------------------
	// QUERY OBJECT OPERATIONS
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Action to add a where constrain for a string value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, String value) {
		query.addWhere(field, value.toString());
		return true;
	}

	/**
	 * Action to add a where constrain for an int value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, int value) {
		query.addWhere(field, new Integer(value).toString());
		return true;
	}

	/**
	 * Action to add a where constrain for a long value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, long value) {
		query.addWhere(field, new Long(value).toString());
		return true;
	}

	/**
	 * Action to add a where constrain for a float value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, float value) {
		query.addWhere(field, new Float(value).toString());
		return true;
	}

	/**
	 * Action to add a where constrain for a double value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, double value) {
		query.addWhere(field, new Double(value).toString());
		return true;
	}

	/**
	 * Action to add a where constrain for a boolean value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, boolean value) {
		query.addWhere(field, new Boolean(value).toString());
		return true;
	}

	/**
	 * Action to add a where constrain for a char value
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, char value) {
		query.addWhere(field, new Character(value).toString());
		return true;
	}
	
	/**
	 * Action to add a where constrain for an object
	 * 
	 * @param query the Query object
	 * @param field the field name
	 * @param value the value
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, String field, Object value) {
		query.addWhere(field, value.toString());
		return true;
	}

	/**
	 * Action that allows you to add multiple where constrains in a single step.
	 * 
	 * <p>
	 * The number of fields and values should match.
	 * </p>
	 * <p>
	 * All fields should be of type string
	 * </p>
	 * <p>
	 * Values should not be lists
	 * </p>
	 * 
	 * @param query the Query object
	 * @param fields a list of field names (strings)
	 * @param values a list of values
	 * @return
	 */
	@ACTION
	public boolean where(SQLQuery query, ListTerm fields, ListTerm values) {
		if (fields.size() != values.size()) {
			throw new ModuleException("Mismatch in number of fields and terms.");
		}
		
		for (int i=0; i<fields.size(); i++) {
			if (fields.get(i) instanceof Primitive) {
				Primitive<?> field = (Primitive<?>) fields.get(i);
				if (field.type() != Type.STRING) {
					throw new ModuleException("Syntax error: invalid field: " + field.value());
				}
				Term value = values.get(i);
				if (value instanceof Primitive) {
					where(query, (String) field.value(), ((Primitive<?>) value).value());
				} else {
					throw new ModuleException("Syntax error: invalid value: " + value);
				}
			}
		}
		return true;
	}

	
	/**
	 * Term that creates a SELECT query of the form: SELECT * FROM table
	 *  
	 * @param table the name of the table
	 * @return
	 */
	@TERM
	public SQLQuery select(String table) {
		SQLQuery query = new SQLQuery(table);
		query.type(SQLQuery.SELECT);
		return query;
	}

	/**
	 * Term that creates a SELECT query of the form: SELECT field FROM table
	 *  
	 * @param table the name of the table
	 * @param field the field name
	 * @return
	 */
	@TERM
	public SQLQuery select(String table, String field) {
		SQLQuery query = new SQLQuery(table);
		query.addField(field);
		query.type(SQLQuery.SELECT);
		return query;
	}

	/**
	 * Term that creates a SELECT query of the form: SELECT listitem1, listitem2, ... FROM table
	 *  
	 * @param table the name of the table
	 * @param fields a list of field names
	 * @return
	 */
	@TERM
	public SQLQuery select(String table, ListTerm fields) {
		SQLQuery query = new SQLQuery(table);
		for (int i=0; i<fields.size(); i++) {
			if (fields.get(i) instanceof Primitive) {
				Primitive<?> field = (Primitive<?>) fields.get(i);
				if (field.type() != Type.STRING) {
					throw new ModuleException("Syntax error: invalid field: " + field.value());
				}
				query.addField((String) field.value());
			}
		}
		query.type(SQLQuery.SELECT);
		return query;
	}
	
	/**
	 * Term that creates a SELECT query of the form:<br/>
	 * SELECT * FROM table WHERE wf1=wv1 AND wf2=wv2 AND ...
	 *  
	 * @param table the name of the table
	 * @param where_fields a list of field names for the where clause
	 * @param where_values a list of values for the where clause
	 * @return
	 */
	@TERM
	public SQLQuery select(String table, ListTerm where_fields, ListTerm where_values) {
		SQLQuery query = select(table);
		where(query, where_fields, where_values);
		return query;
	}

	/**
	 * Term that creates a SELECT query of the form:<br/>
	 * SELECT listitem1, listitem2, ... FROM table WHERE wf1=wv1 AND wf2=wv2 AND ...
	 *  
	 * @param table the name of the table
	 * @param fields a list of field names
	 * @param where_fields a list of field names for the where clause
	 * @param where_values a list of values for the where clause
	 * @return
	 */
	@TERM
	public SQLQuery select(String table, ListTerm fields, ListTerm where_fields, ListTerm where_values) {
		SQLQuery query = select(table, fields);
		where(query, where_fields, where_values);
		return query;
	}

	/**
	 * Action that executes a select query on the database.
	 * 
	 * @param connection the database connection
	 * @param query a Query object
	 * @param results a container for the results
	 * @return
	 */
	@ACTION
	public boolean get(Connection connection, SQLQuery query, ActionParam<ListTerm> results) {
		rawSelectQuery(connection, query.toSQL(), results);
		return true;
	}

	/**
	 * Term that creates a SELECT query of the form:<br/>
	 * INSERT INTO table VALUES (wf1=wv1, wf2=wv2)
	 *  
	 * @param table the name of the table
	 * @param where_fields a list of field names for the where clause
	 * @param where_values a list of values for the where clause
	 * @return
	 */
	@TERM
	public SQLQuery insert(String table, ListTerm fields, ListTerm values) {
		SQLQuery query = new SQLQuery(table);
		for (int i=0; i<fields.size(); i++) {
			if (fields.get(i) instanceof Primitive) {
				Primitive<?> field = (Primitive<?>) fields.get(i);
				if (field.type() != Type.STRING) {
					throw new ModuleException("Syntax error: invalid field: " + field.value());
				}
				Term value = values.get(i);
				if (value instanceof Primitive) {
					query.addValue((String) field.value(), ((Primitive<?>) value).value().toString());
				} else {
					throw new ModuleException("Syntax error: invalid value: " + value);
				}
			}
		}
		query.type(SQLQuery.INSERT);
		return query;
	}

	/**
	 * Action that executes an update query on the database.
	 * 
	 * @param connection the database connection
	 * @param query a Query object
	 * @param results a container for the results
	 * @return
	 */
	@ACTION
	public boolean update(Connection connection, SQLQuery query) {
		System.out.println("query: " + query.toSQL());
		rawUpdateQuery(connection, query.toSQL());
		return true;
	}
}
