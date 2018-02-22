/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.model;

import javax.persistence.Column;
import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


/**
 * Simple JavaBean domain object adds a name property to <code>BaseEntity</code>. Used as a base class for objects
 * needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
public class NamedEntity implements BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3303001383091756992L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
    @Column(name = "name")
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
		
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return this.id == null;
	}

}
