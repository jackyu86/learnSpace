package com.insightfullogic.java8.demo;

import java.util.Optional;
import java.util.function.Supplier;





public class QuoteDemo1 {
	
	
	
	public static void main(String[] args) {
		//静态方法引入
		Converter<String,Integer> converter = Integer ::valueOf;
		String a="123";
		Integer conInt  = converter.convert(a);
		System.out.println(conInt);
		
		//赋值对象属性
		CharFormatClass  cfc = new CharFormatClass();
		Converter<String,String> strConverter =cfc::startWith;
		String startStr = strConverter.convert("Java");
		System.out.println(startStr);
		//工厂
		//使用::关键字引用构造函数
		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("jack", "yu");
		System.out.println(person);
		
		Supplier<Person> supplier = Person::new;
		
		Person p1 = supplier.get();
		System.out.println(p1);
		
		Optional<Person> optional = Optional.of(new Person("jack", "yu"));
		System.out.println(optional.isPresent());
		
	}

}
class CharFormatClass {
	String startWith(String str){
		return String.valueOf(str.charAt(0));
	}
}
class Person{
	String firstName;
	String lastName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Person() {
		super();
	}
	public Person(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}
	@Override
	public String toString() {
		return "Person [firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}
	
	
}