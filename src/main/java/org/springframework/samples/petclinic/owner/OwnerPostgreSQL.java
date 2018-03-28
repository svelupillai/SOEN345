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
	
	//adds to DB
	public void addToDB(Owner o){
		String addQuery = "INSERT INTO Owner (id, first_name, last_name, address, city, telephone) VALUES (" + o.getId().toString() + ", " 
				+ "'" + o.getFirstName() + "', "
				+ "'" + o.getLastName() + "', "
				+ "'" + o.getAddress() + "', "
				+ "'" + o.getCity() + "', "
				+ "'" + o.getTelephone().toString()
				+"');";

		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(addQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
	}
	
	//update db
	public void updateDB(Owner owner){
		String updateQuery = "UPDATE Owner "
							+ "SET first_name= " +"'"+ owner.getFirstName() + "', "
							+ "last_name= " +"'"+ owner.getLastName() + "', "
							+ "address= " +"'"+ owner.getAddress() + "', "
							+ "city= " +"'"+ owner.getCity() + "', "
							+ "telephone= " +"'"+ owner.getTelephone().toString() + "'"
							+ "WHERE id=" + owner.getId() + ";";
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(updateQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void consistencyCheck(OwnerRepository repo) throws SQLException {
		
		inconsistencies = 0;
		List<Owner> owners = repo.getAllOwners();

		String sqlStatement = "SELECT * FROM Owner ORDER BY id;";
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
			
			//this owner is not in the postgres db
			if(id == 0 && firstName == null && lastName ==null &&  address ==null && address == null && city == null && telephone ==null){
				Inconsistency(owner.getId(), id);
				Inconsistency(owner.getFirstName(), firstName);
				Inconsistency(owner.getLastName(), lastName);
				Inconsistency(owner.getAddress(), address);
				Inconsistency(owner.getCity(), city);
				Inconsistency(owner.getTelephone(), telephone);
				addToDB(owner);
			}
			
			//the old db owner and new db owner are inconsistent
			else {
				
				if(!(owner.getFirstName().equals(firstName))){
					Inconsistency(owner.getFirstName(), firstName);
					updateDB(owner);
				}
				
				if(!(owner.getLastName().equals(lastName))){
					Inconsistency(owner.getLastName(), lastName);
					updateDB(owner);
				}
				
				if(!(owner.getAddress().equals(address))){
					Inconsistency(owner.getAddress(), address);
					updateDB(owner);
				}
				
				if(!(owner.getCity().equals(city))){
					Inconsistency(owner.getCity(), city);
					updateDB(owner);
				}
				
				if(!(owner.getTelephone().equals(telephone))){
					Inconsistency(owner.getTelephone(), telephone);
					updateDB(owner);
				}
			
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
