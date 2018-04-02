package org.springframework.samples.petclinic.owner;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToPostgreSQL {

	static Connection connection = null;

	private static void createOwnerTable() throws SQLException {

		String sql_stmt = "CREATE TABLE IF NOT EXISTS Owner (id INTEGER PRIMARY KEY, "
				+ "first_name VARCHAR(255) NOT NULL, "
				+ "last_name VARCHAR(255) NOT NULL, "
				+ "address VARCHAR(255) NOT NULL, "
				+ "city VARCHAR(255) NOT NULL, "
				+ "telephone VARCHAR(15) NOT NULL);";

		Statement statement = connection.createStatement();
		statement.executeUpdate(sql_stmt);
	}


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

			createOwnerTable();
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
