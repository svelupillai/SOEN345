package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;

@SuppressWarnings({ "hiding", "serial" })
public class PostgresList<E> extends ArrayList<E> {
	
	private OwnerPostgreSQL postgresOwner;
	
	public PostgresList(){
		postgresOwner = new OwnerPostgreSQL();
	}

	@Override
	//add the object to the old db
	//add the object to the new db
	public boolean add(E e) {
		super.add(e);
		postgresOwner.addToDB((Owner) e);
		return true;
	}
	
	//this is for testing purposes only
	//will add the object only to old db
	public boolean addTestOnlyOld(E e) {
		return super.add(e);
	}


}
