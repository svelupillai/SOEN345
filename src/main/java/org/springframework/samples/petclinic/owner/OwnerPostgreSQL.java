package org.springframework.samples.petclinic.owner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class OwnerPostgreSQL {
	
	private int inconsistencies = 0;
	
	public Connection getConnection(){
		return ConnectToPostgreSQL.connectToDatabase();
	}
	
	public void forklift(OwnerRepository repo) {
		List<Owner> owners = repo.getAllOwners();

		for(Owner  owner : owners) {
			String forkliftQuery = "INSERT INTO Owner (id, first_name, last_name, address, city, telephone) VALUES (" + owner.getId().toString() + ", " 
					+ "'" + owner.getFirstName() + "', "
					+ "'" + owner.getLastName() + "', "
					+ "'" + owner.getAddress() + "', "
					+ "'" + owner.getCity() + "', "
					+ "'" + owner.getTelephone().toString()
					+"');";

			Statement statement = null;
			try {
				statement = getConnection().createStatement();
				statement.executeUpdate(forkliftQuery);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void consistencyCheck(OwnerRepository repo) throws SQLException {

		List<Owner> owners = repo.getAllOwners();

		String sqlStatement = "SELECT * FROM Owner;";
		Statement statement = getConnection().createStatement();
		ResultSet postgresOwners = statement.executeQuery(sqlStatement);

		int idColumn = postgresOwners.findColumn("id");
		int firstNameColumn = postgresOwners.findColumn("first_name");
		int lastNameColumn = postgresOwners.findColumn("last_name");
		int addressColumn = postgresOwners.findColumn("address");
		int cityColumn = postgresOwners.findColumn("city");
		int telephoneColumn = postgresOwners.findColumn("telephone");

		int index = 0;

		while(index < owners.size()) {

			int id = 0;
			String firstName = null;
			String lastName = null;
			String address = null;
			String city = null;
			String telephone = null;

			if(postgresOwners.next()) {
				id = (int) postgresOwners.getObject(idColumn);
				firstName = (String) postgresOwners.getObject(firstNameColumn);
				lastName = (String) postgresOwners.getObject(lastNameColumn);
				address = (String) postgresOwners.getObject(addressColumn);
				city = (String) postgresOwners.getObject(cityColumn);
				telephone = (String) postgresOwners.getObject(telephoneColumn);
			}

			Owner owner = owners.get(index);
			if(owner.getId() != id) {
				Inconsistency(owner.getId(), id);
				//fix inconsistency
			}
			if(!(owner.getFirstName().equals(firstName))) {
				Inconsistency(owner.getFirstName(), firstName);
				//fix inconsistency
			}
			if(!(owner.getLastName().equals(lastName))) {
				Inconsistency(owner.getLastName(), lastName);
				//fix inconsistency
			}
			if(!(owner.getAddress().equals(address))) {
				Inconsistency(owner.getAddress(), address);
				//fix inconsistency
			}
			if(!(owner.getCity().equals(city))) {
				Inconsistency(owner.getCity(), city);
				//fix inconsistency
			}
			if(!(owner.getTelephone().equals(telephone))) {
				Inconsistency(owner.getTelephone(), telephone);
				//fix inconsistency
			}

			index++;
		}
	}
	
	private void Inconsistency(Object expected, Object actual) {
		System.out.println("\nConsistency Violation!"
							+ "\n\t Expected: " + expected 
							+ "\n\t Actual: " + actual);
		
		inconsistencies++;
	}
	
	public int getInconsistencies() {
		return inconsistencies;
	}
	
	public void dropTable() throws SQLException {
		String sqlDropTableQuery = "DROP TABLE Owner;";
		Statement statement = getConnection().createStatement();
		statement.executeUpdate(sqlDropTableQuery);
	}

}
