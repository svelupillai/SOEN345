package org.springframework.samples.petclinic.owner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.scheduling.annotation.Async;


public class OwnerPostgreSQL {

	private int inconsistencies = 0;

	private int readInconsistencies = 0;
	
	private HashMap<Integer, List<String>> inconsistenciesHashMap = new HashMap<>();
	
	private HashMap<Integer, List<String>> readInconsistenciesHashMap = new HashMap<>();
	
	public void resetInconsistenciesHash() {
		inconsistenciesHashMap = new HashMap<>();
	}
	
	public void resetReadInconsistenciesHash() {
		readInconsistenciesHashMap = new HashMap<>();
	}
	
	public HashMap<Integer, List<String>> getInconsistenciesHash(){
		return inconsistenciesHashMap;
	}
	
	public HashMap<Integer, List<String>> getReadInconsistenciesHash(){
		return readInconsistenciesHashMap;
	}

	public Connection getConnection(){
		return ConnectToPostgreSQL.connectToDatabase();
	}

	public void forklift(OwnerRepository repo) {
		//getting all owners in the current db
		List<Owner> owners = repo.getAllOwners();

		for(Owner  owner : owners) {
			String forkliftQuery = "INSERT INTO Owner (id, first_name, last_name, address, city, telephone) VALUES (" + owner.getId().toString() + ", " 
					+ "'" + owner.getFirstName() + "', "
					+ "'" + owner.getLastName() + "', "
					+ "'" + owner.getAddress() + "', "
					+ "'" + owner.getCity() + "', "
					+ "'" + owner.getTelephone().toString()
					+"');";

			//inserting the current owners into the new postgreSQL db
			Statement statement = null;
			try {
				statement = getConnection().createStatement();
				statement.executeUpdate(forkliftQuery);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//adds owner to postgreSQL db
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

	//update first name postgreSQL db
	private void updateFirstName(Owner owner){
		String updateQuery = "UPDATE Owner "
				+ "SET first_name= " +"'"+ owner.getFirstName() + "'"
				+ "WHERE id=" + owner.getId() + ";";
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(updateQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//update last name in postgreSQL db
	private void updateLastName(Owner owner){
		String updateQuery = "UPDATE Owner "
				+ "SET last_name= " +"'"+ owner.getLastName() + "'"
				+ "WHERE id=" + owner.getId() + ";";
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(updateQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//update address in postgreSQL db
	private void updateAddress(Owner owner){
		String updateQuery = "UPDATE Owner "
				+ "SET address= " +"'"+ owner.getAddress() + "'"
				+ "WHERE id=" + owner.getId() + ";";
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(updateQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//update city in postgreSQL db
	private void updateCity(Owner owner){
		String updateQuery = "UPDATE Owner "
				+ "SET city= " +"'"+ owner.getCity() + "'"
				+ "WHERE id=" + owner.getId() + ";";
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(updateQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//update telephone in postgreSQL db
	private void updateTelephone(Owner owner){
		String updateQuery = "UPDATE Owner "
				+ "SET telephone= " +"'"+ owner.getTelephone() + "'"
				+ "WHERE id=" + owner.getId() + ";";
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(updateQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Async
	public void consistencyCheck(OwnerRepository repo) throws SQLException {

		inconsistencies = 0;
		//all current owners
		List<Owner> owners = repo.getAllOwners();

		//all owners in postgreSQL db
		String sqlStatement = "SELECT * FROM Owner ORDER BY id;";
		Statement statement = getConnection().createStatement();
		ResultSet postgresOwners = statement.executeQuery(sqlStatement);

		//gets the column number
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

			//pointer starts off on the row above the first one
			//if row exists, get the value according to the column number
			if(postgresOwners.next()) {
				id = (int) postgresOwners.getObject(idColumn);
				firstName = (String) postgresOwners.getObject(firstNameColumn);
				lastName = (String) postgresOwners.getObject(lastNameColumn);
				address = (String) postgresOwners.getObject(addressColumn);
				city = (String) postgresOwners.getObject(cityColumn);
				telephone = (String) postgresOwners.getObject(telephoneColumn);
			}

			//owner in current db
			Owner owner = owners.get(index);

			//this owner is not in the postgreSQL db but it is in the old db
			if(id == 0 && firstName == null && lastName ==null &&  address ==null && address == null && city == null && telephone ==null){
				int ownerId = owner.getId();
				Inconsistency(owner.getId(), id);
				Inconsistency(owner.getFirstName(), firstName);
				Inconsistency(owner.getLastName(), lastName);
				Inconsistency(owner.getAddress(), address);
				Inconsistency(owner.getCity(), city);
				Inconsistency(owner.getTelephone(), telephone);
				addToDB(owner);
				inconsistenciesHashMap.put(ownerId, new ArrayList<String>() {{add("Id");
																			  add("FirstName");
																			  add("LastName");
																			  add("Address");
																			  add("City");
																			  add("Telephone");
																			  }});
			}

			//the old db owner and new db owner are inconsistent
			//update postgreSQL with values of old db
			else {
				int ownerId = owner.getId();
				List<String> columns = new ArrayList<String>();
				if(!(owner.getFirstName().equals(firstName))){
					columns.add("FirstName");
					Inconsistency(owner.getFirstName(), firstName);
					inconsistenciesHashMap.put(ownerId, columns);
					updateFirstName(owner);
				}

				if(!(owner.getLastName().equals(lastName))){
					columns.add("LastName");
					Inconsistency(owner.getLastName(), lastName);
					inconsistenciesHashMap.put(ownerId, columns);
					updateLastName(owner);
				}

				if(!(owner.getAddress().equals(address))){
					columns.add("Address");
					Inconsistency(owner.getAddress(), address);
					inconsistenciesHashMap.put(ownerId, columns);
					updateAddress(owner);
				}

				if(!(owner.getCity().equals(city))){
					columns.add("City");
					Inconsistency(owner.getCity(), city);
					inconsistenciesHashMap.put(ownerId, columns);
					updateCity(owner);
				}

				if(!(owner.getTelephone().equals(telephone))){
					columns.add("Telephone");
					Inconsistency(owner.getTelephone(), telephone);
					inconsistenciesHashMap.put(ownerId, columns);
					updateTelephone(owner);
				}

			}

			index++;
		}

	}

	//print inconsistency
	private void Inconsistency(Object expected, Object actual) {
		System.out.println("\nConsistency Violation!"
				+ "\n\t Expected: " + expected 
				+ "\n\t Actual: " + actual);

		inconsistencies++;
	}

	public int getInconsistencies() {
		return inconsistencies;
	}

	@Async
	public void shadowRead(OwnerRepository repo, int id) throws SQLException{

		readInconsistencies = 0;

		//current owner in old db with the specific id
		Owner owner = (Owner) repo.findById(id);

		//Owner in postgreSQL db with the specific id
		String sqlStatement = "SELECT * FROM Owner WHERE id = "+id+" ORDER BY id;";
		Statement statement = getConnection().createStatement();
		ResultSet postgresOwner = statement.executeQuery(sqlStatement);

		//get the column number in the postgreSQL db
		int firstNameColumn = postgresOwner.findColumn("first_name");
		int lastNameColumn = postgresOwner.findColumn("last_name");
		int addressColumn = postgresOwner.findColumn("address");
		int cityColumn = postgresOwner.findColumn("city");
		int telephoneColumn = postgresOwner.findColumn("telephone");

		//get the value according to the column number
		String firstName = null;
		String lastName = null;
		String address = null;
		String city = null;
		String telephone = null;

		//if row exists, get the value according to the column number
		if(postgresOwner.next()) {
			firstName = (String) postgresOwner.getObject(firstNameColumn);
			lastName = (String) postgresOwner.getObject(lastNameColumn);
			address = (String) postgresOwner.getObject(addressColumn);
			city = (String) postgresOwner.getObject(cityColumn);
			telephone = (String) postgresOwner.getObject(telephoneColumn);
		}

		//check if the old db owner and the postgreSQL db owner have same values
		//if inconsistent, update the postgreSQL with values of old db
		int ownerId = owner.getId();
		List<String> columns = new ArrayList<String>();
		if(!(owner.getFirstName().equals(firstName))){
			columns.add("FirstName");
			ReadInconsistency(owner.getFirstName(), firstName);
			readInconsistenciesHashMap.put(ownerId, columns);
			updateFirstName(owner);
		}

		if(!(owner.getLastName().equals(lastName))){
			columns.add("LastName");
			ReadInconsistency(owner.getLastName(), lastName);
			readInconsistenciesHashMap.put(ownerId, columns);
			updateLastName(owner);
		}

		if(!(owner.getAddress().equals(address))){
			columns.add("Address");
			ReadInconsistency(owner.getAddress(), address);
			readInconsistenciesHashMap.put(ownerId, columns);
			updateAddress(owner);
		}

		if(!(owner.getCity().equals(city))){
			columns.add("City");
			ReadInconsistency(owner.getCity(), city);
			readInconsistenciesHashMap.put(ownerId, columns);
			updateCity(owner);
		}

		if(!(owner.getTelephone().equals(telephone))){
			columns.add("Telephone");
			ReadInconsistency(owner.getTelephone(), telephone);
			readInconsistenciesHashMap.put(ownerId, columns);
			updateTelephone(owner);
		}
	}

	//print read inconsistency
	private void ReadInconsistency(Object expected, Object actual) {
		System.out.println("\nRead Consistency Violation!"
				+ "\n\t Expected: " + expected 
				+ "\n\t Actual: " + actual);

		readInconsistencies++;
	}

	public int getReadInconsistencies() {
		return readInconsistencies;
	}

	public void dropTable() throws SQLException {
		String sqlDropTableQuery = "DROP TABLE Owner;";
		Statement statement = getConnection().createStatement();
		statement.executeUpdate(sqlDropTableQuery);
	}
}
