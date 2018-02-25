package org.springframework.samples.petclinic.owner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FakeOwnerTests {

	private int ownerId = 1;
    private String firstName = "George";
    private String lastName = "Franklin";
    private String address = "110 W. Liberty St.";
    private String city = "Madison";
    private String telephone = "6085551023";

	@Test
	public void testGetters() {
		FakeOwner mockFakeOwner = mock(FakeOwner.class);
		when(mockFakeOwner.getId()).thenReturn(ownerId);
		when(mockFakeOwner.getFirstName()).thenReturn(firstName);
		when(mockFakeOwner.getLastName()).thenReturn(lastName);
		when(mockFakeOwner.getAddress()).thenReturn(address);
		when(mockFakeOwner.getCity()).thenReturn(city);
		when(mockFakeOwner.getTelephone()).thenReturn(telephone);
		when(mockFakeOwner.isNew()).thenReturn(true);
		
		assertTrue(mockFakeOwner.getId() == ownerId);
		assertEquals(mockFakeOwner.getFirstName(), firstName);
		assertEquals(mockFakeOwner.getLastName(), lastName);
		assertEquals(mockFakeOwner.getAddress(), address);
		assertEquals(mockFakeOwner.getCity(), city);
		assertEquals(mockFakeOwner.getTelephone(), telephone);
		assertTrue(mockFakeOwner.isNew());
		
		verify(mockFakeOwner).getId();
		verify(mockFakeOwner).getFirstName();
		verify(mockFakeOwner).getLastName();
		verify(mockFakeOwner).getAddress();
		verify(mockFakeOwner).getCity();
		verify(mockFakeOwner).getTelephone();
		verify(mockFakeOwner).isNew();
	}
	
	@Test
	public void testGetSetPet() {
		Pet pet1 = new Pet();
		pet1.setBirthDate(new Date());
		pet1.setId(1);
		pet1.setName("Test1");
		
		Pet pet2 = new Pet();
		pet2.setBirthDate(new Date());
		pet2.setId(1);
		pet2.setName("Test2");
		
		List<Pet> petList = new ArrayList<Pet>() {{add(pet1); add(pet2);}};
		Set<Pet> petSet = new HashSet<Pet>() {{add(pet1); add(pet2);}};
		
		FakeOwner mockFakeOwner = mock(FakeOwner.class);
		doNothing().when(mockFakeOwner).addPet(pet1);
		doNothing().when(mockFakeOwner).addPet(pet2);
		when(mockFakeOwner.getPet(pet1.getName())).thenReturn(pet1);
		when(mockFakeOwner.getPet(pet2.getName())).thenReturn(pet2);
		when(mockFakeOwner.getPets()).thenReturn(petList);
		when(mockFakeOwner.getPetsInternal()).thenReturn(petSet);
		
		assertEquals(mockFakeOwner.getPet("Test1"), pet1);
		assertEquals(mockFakeOwner.getPet("Test2"), pet2);
		assertEquals(mockFakeOwner.getPets(), petList);
		assertEquals(mockFakeOwner.getPetsInternal(), petSet);
		
		verify(mockFakeOwner).getPet("Test1");
		verify(mockFakeOwner).getPets();
		verify(mockFakeOwner).getPetsInternal();
	}

}
