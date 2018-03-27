package org.springframework.samples.petclinic.owner;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToPostgreSQL {

	static Connection connection = null;

	public static Connection connectToDatabase() {
		try {
			Class.forName("org.postgresql.Driver");
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		try {
			DriverManager.setLoginTimeout(23);
			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/petclinic", "postgres", "soen");
		} 
		catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection == null) {
			System.out.println("Failed to make connection!");
		}

		return connection;
	}

}
