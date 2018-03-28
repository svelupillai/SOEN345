package org.springframework.samples.petclinic.owner;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(OwnerController.class)
public class TestMigration {

    @MockBean
    private OwnerRepository owners;
    
    PostgresList<Owner> ownersList;

    @Before
    public void setup() throws SQLException {
    	OwnerPostgreSQL ownerPostgres = new OwnerPostgreSQL();
		ownerPostgres.dropTable();
    	
        Owner george = new Owner();
        george.setId(1);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 Liberty");
        george.setCity("Madison");
        george.setTelephone("6085551023");
        given(this.owners.findById(1)).willReturn(george);
        
        Owner maddie = new Owner();
        maddie.setId(2);
        maddie.setFirstName("Maddie");
        maddie.setLastName("Benjamin");
        maddie.setAddress("108 Cedarcrest");
        maddie.setCity("DDO");
        maddie.setTelephone("5146859221");
        given(this.owners.findById(2)).willReturn(maddie);
        
        ownersList = new PostgresList<Owner>();
        ownersList.add(george);
        ownersList.add(maddie);
        given(this.owners.getAllOwners()).willReturn(ownersList);
        
    }
	
	@Test
	public void testNoInconsistencies() throws SQLException {
		
		OwnerPostgreSQL ownerPostgres = new OwnerPostgreSQL();
		ownerPostgres.dropTable();
		
		ownerPostgres.forklift(this.owners);
		ownerPostgres.consistencyCheck(this.owners);
		
		assertEquals(0, ownerPostgres.getInconsistencies());
	}
	
	@Test
	public void testInsertInconsistency() throws SQLException {
		
		OwnerPostgreSQL ownerPostgres = new OwnerPostgreSQL();
		ownerPostgres.dropTable();
		
		ownerPostgres.forklift(this.owners);
		
		// Insert an inconsistency - ensure the consistency checker is working properly
        Owner person = new Owner();
        person.setId(3);
        person.setFirstName("Peson");
        person.setLastName("Peep");
        person.setAddress("309 Elm");
        person.setCity("Westmount");
        person.setTelephone("5432165995");
        given(this.owners.findById(3)).willReturn(person);
        ownersList.add(person);
        given(this.owners.getAllOwners()).willReturn(ownersList);
		
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(6, ownerPostgres.getInconsistencies());
		
		//second time around will be 0, since it was fixed before
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(0, ownerPostgres.getInconsistencies());
		
		// when an owner that exists is updated, there will be an inconsistency the first time
		//the second time it will be 0 because we fix it
		Owner maddie = new Owner();
        maddie.setId(2);
        maddie.setFirstName("Maddy");
        maddie.setLastName("Benjamin");
        maddie.setAddress("108 Cedarcrest");
        maddie.setCity("DDO");
        maddie.setTelephone("5146859221");
        given(this.owners.findById(2)).willReturn(maddie);
		this.ownersList.set(1,maddie);
		given(this.owners.getAllOwners()).willReturn(ownersList);
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(1, ownerPostgres.getInconsistencies());
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(0, ownerPostgres.getInconsistencies());
        
		
		
		
		
	}

}
