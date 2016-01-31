package com.insightfullogic.java8.demo;

public interface PersonFactory<P extends Person> {
	
	P create(String firstName,String lastName);

}
