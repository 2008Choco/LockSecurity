package me.choco.locks.utils.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {
	/** Open a new connection to the lockinfo database
	 * @return Connection to the database
	 */
	public Connection openConnection(){
		try{return DriverManager.getConnection("jdbc:sqlite:plugins/LockSecurity/lockinfo.db");}
		catch (SQLException e){e.printStackTrace(); return null;}
	}
	
	/** Close a specific opened connection to a database */
	public void closeConnection(Connection connection){
		try{connection.close();}
		catch (SQLException e){e.printStackTrace();}
	}
	
	/** Create a new statement from a connection to a database
	 * @param connection - The connection to the database in which to create a statement
	 * @return Statement for the database
	 */
	public Statement createStatement(Connection connection){
		try{return connection.createStatement();}
		catch (SQLException e){e.printStackTrace(); return null;}
	}
	
	/** Close a specific statement from a connection to a database */
	public void closeStatement(Statement statement){
		try{statement.close();}
		catch (SQLException e){e.printStackTrace();}
	}
	
	/** Execute an SQL statement from the specified statement object
	 * @param statement - The statement to use
	 * @param sql - The SQL string parameters
	 */
	public void executeStatement(Statement statement, String sql){
		try{statement.execute(sql);}
		catch(SQLException e){e.printStackTrace();}
	}
	
	/** Create a new prepared statement from a connection to a database
	 * @param connection - The connection to the database in which to create a prepared statement
	 * @return Prepared statement for the database
	 */
	public PreparedStatement createPreparedStatement(Connection connection, String sql){
		try{return connection.prepareStatement(sql);}
		catch(SQLException e){e.printStackTrace(); return null;}
	}
	
	/** Close a specific prepared statement from a connection to a database */
	public void closePreparedStatement(PreparedStatement statement){
		try{statement.close();}
		catch(SQLException e){e.printStackTrace();}
	}
	
	/** Query / Return informatin from the database from a specific statement
	 * @param statement - The statement to use
	 * @param sql - The SQL string parameters
	 * @return ResultSet of the query
	 */
	public ResultSet queryDatabase(Statement statement, String sql){
		try{return statement.executeQuery(sql);}
		catch (SQLException e){e.printStackTrace(); return null;}
	}
	
	/** Close a specific result set from a connection to a database
	 * @param set - The ResultSet to close
	 */
	public void closeResultSet(ResultSet set){
		try{set.close();}
		catch(SQLException e){e.printStackTrace();}
	}
}