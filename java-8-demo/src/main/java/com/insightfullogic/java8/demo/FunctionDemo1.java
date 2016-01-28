package com.insightfullogic.java8.demo;

import java.util.function.Function;

import com.insightfullogic.java8.demo.pojo.User;

public class FunctionDemo1 {
	
	public static void main(String[] args) {
		
		Function<User,String>  applyFunction = p -> p.getName();
		Function<User,User> composeFunction = p -> {p.setName(p.getName()+"-->compose"); return p;};
		Function<String,String> thenFunction = p -> p+"after";
		User user = new User(1, "jack", 30, User.SEX.FEMALE.getSexStr());
		System.out.println(applyFunction.compose(composeFunction).andThen(thenFunction).apply(user));
		/*System.out.println(applyFunction.compose(composeFunction).apply(user));o
		System.out.println(applyFunction.andThen(thenFunction).apply(user));*/
		
		
		
	}

}
