// Copyright (C) 2011 Antony Derham <admin@districtmine.net>

package net.districtmine.warrant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

import com.mysql.jdbc.Driver;

public class MySQL {
	public static final Logger log = Logger.getLogger("Minecraft");

	private Connection MySQLConnection;
	private Statement MySQLStatement;
	@SuppressWarnings("unused")
	private Driver MySQLDriver;
	private String MySQLUser, MySQLPass, MySQLHost, MySQLPort, MySQLDataBase, MySQLURL;

	public MySQL(String user, String pass, String host, String port, String db) {
		consoleLog("Running database connection...");
		try {
			MySQLUser = user;
			MySQLPass = pass;
			MySQLHost = host;
			MySQLPort = port;
			MySQLDataBase = db;
			MySQLURL = "jdbc:mysql://" + MySQLHost + ":" + MySQLPort + "/" + MySQLDataBase;
			Class.forName("com.mysql.jdbc.Driver");
			MySQLConnection = DriverManager.getConnection(MySQLURL, MySQLUser, MySQLPass);
			MySQLStatement = MySQLConnection.createStatement();
			MySQLConnection.setAutoCommit(true);
		} catch (Exception e) {
			consoleError("MySQL connection failed: " + e.toString());
		}
	}

	public Connection getConnection() {
		return MySQLConnection;
	}

	public Statement getStatement() {
		return MySQLStatement;
	}

	public void tryUpdate(String sqlString) {
		try {
			getStatement().executeUpdate(sqlString);
		} catch (Exception e) {
			consoleWarning("The following statement failed: " + sqlString);
			consoleError("Statement failed: " + e.toString());
		}
	}

	public ResultSet trySelect(String sqlString) {
		try {
			System.out.println(getStatement().toString());
			return getStatement().executeQuery(sqlString);
		} catch (Exception e) {
			consoleWarning("The following statement failed: " + sqlString);
			consoleError("Statement failed: " + e.toString());
			return null;
		}
	}

	public void consoleLog(String msg) {
		log.info("[Warrant] - " + msg);
	}

	public void consoleWarning(String msg) {
		log.warning("[Warrant] - " + msg);
	}

	public void consoleError(String msg) {
		log.severe("[Warrant] - " + msg);
	}
}
