package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;

@SuppressWarnings({ "hiding", "serial" })
public class PostgresList<E> extends ArrayList<E> {
	
	private OwnerPostgreSQL postgresOwner;
	
	public PostgresList(){
		postgresOwner = new OwnerPostgreSQL();
	}

	@Override
	public boolean add(E e) {
		super.add(e);
		postgresOwner.addToDB((Owner) e);
		
		return true;
	}
	
	public boolean addTestOnlyOld(E e) {
		return super.add(e);
	}


}
