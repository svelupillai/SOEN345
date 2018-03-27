package org.springframework.samples.petclinic.owner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class OwnerPostgreSQL {
	
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

}
