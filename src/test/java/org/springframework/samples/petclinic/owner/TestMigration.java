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

		//owner 1
		Owner george = new Owner();
		george.setId(1);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 Liberty");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		given(this.owners.findById(1)).willReturn(george);

		//owner 2
		Owner maddie = new Owner();
		maddie.setId(2);
		maddie.setFirstName("Maddie");
		maddie.setLastName("Benjamin");
		maddie.setAddress("108 Cedarcrest");
		maddie.setCity("DDO");
		maddie.setTelephone("5146859221");
		given(this.owners.findById(2)).willReturn(maddie);

		ownersList = new PostgresList<Owner>();
		//add the 2 owners to old db only
		ownersList.addTestOnlyOld(george);
		ownersList.addTestOnlyOld(maddie);
		given(this.owners.getAllOwners()).willReturn(ownersList);

	}

	@Test
	public void testNoInconsistencies() throws SQLException {
		//drop exisitng new db so that we do not get primary key violations
		OwnerPostgreSQL ownerPostgres = new OwnerPostgreSQL();
		ownerPostgres.dropTable();

		//forklist the data
		ownerPostgres.forklift(this.owners);

		//check that there are no inconsistencies
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(0, ownerPostgres.getInconsistencies());
	}

	@Test
	public void testInsertInconsistency() throws SQLException {

		//drop exisitng new db so that we do not get primary key violations
		OwnerPostgreSQL ownerPostgres = new OwnerPostgreSQL();
		ownerPostgres.dropTable();

		//forklift the data
		ownerPostgres.forklift(this.owners);

		// **Insert an inconsistency - ensure the consistency checker is working properly**

		//person will be in old db and not in new one
		Owner person = new Owner();
		person.setId(3);
		person.setFirstName("Peson");
		person.setLastName("Peep");
		person.setAddress("309 Elm");
		person.setCity("Westmount");
		person.setTelephone("5432165995");
		given(this.owners.findById(3)).willReturn(person);
		ownersList.addTestOnlyOld(person);
		given(this.owners.getAllOwners()).willReturn(ownersList);

		//all fields in the person object will be inconsistent because it is not in the new db
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(6, ownerPostgres.getInconsistencies());
		//second time around will be 0, since it was fixed before
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(0, ownerPostgres.getInconsistencies());


		// when an owner that exists is updated, there will be an inconsistency
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

		//the first time it will show one inconsistency
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(1, ownerPostgres.getInconsistencies());
		//the second time it will be 0, since the consistency checker fixes it
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(0, ownerPostgres.getInconsistencies());

		// **This is the shadow write, it writes both to the old db list and the new one**

		//object that will be added to both old and new dbs
		Owner simple = new Owner();
		simple.setId(4);
		simple.setFirstName("Simple");
		simple.setLastName("seep");
		simple.setAddress("4825 bourett");
		simple.setCity("Montreal");
		simple.setTelephone("5147333202");
		given(this.owners.findById(4)).willReturn(simple);

		//adds to both dbs
		ownersList.add(simple);
		given(this.owners.getAllOwners()).willReturn(ownersList);

		//consistency check after the shadow write, ensure no inconsistencies
		ownerPostgres.consistencyCheck(this.owners);
		assertEquals(0, ownerPostgres.getInconsistencies());

		// **This is the shadow read to ensure that the values are consistent for a specific row/id**

		//Owner object will be added to both old and new db
		Owner human = new Owner();
		human.setId(5);
		human.setFirstName("Human");
		human.setLastName("Alien");
		human.setAddress("5575 Victoria");
		human.setCity("Montreal");
		human.setTelephone("5147373455");
		given(this.owners.findById(5)).willReturn(human);

		//adds to both dbs
		ownersList.add(human);
		given(this.owners.getAllOwners()).willReturn(ownersList);

		ownerPostgres.shadowRead(this.owners, human.getId());
		assertEquals(0, ownerPostgres.getReadInconsistencies());

		// when an owner that exists is updated, there will be an inconsistency
		Owner georgie = new Owner();
		georgie.setId(1);
		georgie.setFirstName("Georgie");
		georgie.setLastName("Franklin");
		georgie.setAddress("110 Liberty");
		georgie.setCity("Madison");
		georgie.setTelephone("6085551023");
		given(this.owners.findById(1)).willReturn(georgie);
		this.ownersList.set(1, georgie);
		given(this.owners.getAllOwners()).willReturn(ownersList);

		//the first time it will show one consistency
		ownerPostgres.shadowRead(this.owners, georgie.getId());
		assertEquals(1, ownerPostgres.getReadInconsistencies());
		//the second time it will be 0, as the inconsistency will be fixed
		ownerPostgres.shadowRead(this.owners, georgie.getId());
		assertEquals(0, ownerPostgres.getReadInconsistencies());

	}

	@Test
	public void testCallOnlyNewDBWhenPastAThreshold() throws SQLException {
	    int i = 0;
        OwnerPostgreSQL ownerPostgres = new OwnerPostgreSQL();
        ownerPostgres.dropTable();
        while(i<5) {
            Owner ow = new Owner();
            ow.setId(i);
            ow.setFirstName("John" );
            ow.setLastName("Tabla");
            ow.setTelephone("123");
            ow.setAddress("5045");
            ow.setCity("Mtl");
	        ownersList.add(ow); //adds to both
            given(this.owners.getAllOwners()).willReturn(ownersList);
            ownerPostgres.consistencyCheck(this.owners);
	        i++;
        }

        if (ownerPostgres.getTotalInconsistencies() == 0) { // if 100% success rate then only add to new datastore(postgres)
            Owner o = new Owner();
            o.setFirstName("Cassandra");
            o.setLastName("Tabla");
            o.setId(100);
            o.setTelephone("598");
            o.setAddress("5045");
            o.setCity("Mtl");
            ownersList.addTestOnlyNew(o);

            Owner ownerInOldDb = (Owner)this.owners.findById(100);
            assertNull(ownerInOldDb);
        }

    }
}
