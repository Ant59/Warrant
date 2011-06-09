package com.alta189.sqlLibrary.MySQL;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.districtmine.warrant.WarrantLogger;
import net.districtmine.warrant.WarrantSql;

public class DatabaseHandler {
	private WarrantSql core;  
	private Connection connection;
	private String dblocation;
	private String username;
	private String password;
	private String database;
	  
	  
	public DatabaseHandler(WarrantSql core, String dbLocation, String database, String username, String password) {
		this.core = core;
		this.dblocation = dbLocation;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	  
	private void openConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + dblocation + "/" + database, username, password);
	    } catch (ClassNotFoundException e) {
	    	WarrantLogger.mysqlError("ClassNotFoundException! " + e.getMessage());
	    } catch (SQLException e) {
	    	WarrantLogger.mysqlError("SQLException! " + e.getMessage());
	    }
	}

	public Boolean checkConnection() {
		if (connection == null) {
			try {
				openConnection();
				return true;
			} catch (MalformedURLException ex) {
				WarrantLogger.mysqlError("MalformedURLException! " + ex.getMessage());
			} catch (InstantiationException ex) {
				WarrantLogger.mysqlError("InstantiationExceptioon! " + ex.getMessage());
			} catch (IllegalAccessException ex) {
				WarrantLogger.mysqlError("IllegalAccessException! " + ex.getMessage());
			}
			return false;
	    }
		return true;
	  }

	public void closeConnection() {
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			WarrantLogger.mysqlError("Failed to close database connection! " + e.getMessage());
		}
	}
	
	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}
	
	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    ResultSet result = statement.executeQuery(query);
		    
		    return result;
		} catch (SQLException ex) {
			WarrantLogger.mysqlWarning("Error at SQL Query: " + ex.getMessage());
		}
		return null;
	}
	
	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    statement.executeUpdate(query);
		    
		    
		} catch (SQLException ex) {
			
				if (!ex.toString().contains("not return ResultSet")) WarrantLogger.mysqlWarning("Error at SQL INSERT Query: " + ex);
			
			
		}
	}
	
	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    statement.executeUpdate(query);
		    
		    
		} catch (SQLException ex) {
			
				if (!ex.toString().contains("not return ResultSet")) WarrantLogger.mysqlWarning("Error at SQL UPDATE Query: " + ex);
			
		}
	}
	
	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    statement.executeUpdate(query);
		    
		    
		} catch (SQLException ex) {
			
				if (!ex.toString().contains("not return ResultSet")) WarrantLogger.mysqlWarning("Error at SQL DELETE Query: " + ex);
			
		}
	}
	
	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    
		    ResultSet result = statement.executeQuery("SELECT * FROM " + table);
		    
		    if (result == null) return false;
		    if (result != null) return true;
		} catch (SQLException ex) {
			if (ex.getMessage().contains("exist")) {
				return false;
			} else {
				WarrantLogger.mysqlWarning("Error at SQL Query: " + ex.getMessage());
			}
		}
		
		
		if (sqlQuery("SELECT * FROM " + table) == null) return true;
		return false;
	}
	
	public Boolean wipeTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			if (!core.checkTable(table)) {
				WarrantLogger.mysqlError("Error at Wipe Table: table, " + table + ", does not exist");
				return false;
			}
			Connection connection = getConnection();
		    Statement statement = connection.createStatement();
		    String query = "DELETE FROM " + table + ";";
		    statement.executeUpdate(query);
		    
		    return true;
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet")) WarrantLogger.mysqlWarning("Error at SQL WIPE TABLE Query: " + ex);
			return false;
		}
	}
	
	public Boolean createTable(String query) {
		try {
			if (query == null) { WarrantLogger.mysqlError("SQL Create Table query empty."); return false; }
		    
			Statement statement = connection.createStatement();
		    statement.execute(query);
		    return true;
		} catch (SQLException ex){
			WarrantLogger.mysqlError(ex.getMessage());
			return false;
		}
	}
}
