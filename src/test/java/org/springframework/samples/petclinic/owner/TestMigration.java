package org.springframework.samples.petclinic.owner;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    
    List<Owner> ownersList;

    @Before
    public void setup() {
    	
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
        
        ownersList = new ArrayList<Owner>();
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

}
