package com.insightfullogic.java8.demo;

import java.lang.annotation.Repeatable;


//ava 8中的注解是可重复的

@interface Hints{
	Hint[] value();
}
@Repeatable(Hints.class)
@interface Hint{
	String value();
}

//变体1：使用注解容器（老方法）
@Hints({@Hint("hint1"), @Hint("hint2")})
class Person2 {}


//变体2：使用可重复注解（新方法）
@Hint("hint1")
@Hint("hint2")
class Person3 {}


public class Java8AnnotationsDemo1 {
	

	
	
	public static void main(String[] args) {
		
		Hint hint = Person2.class.getAnnotation(Hint.class);
		System.out.println(hint);                   // null
		 
		Hints hints1 = Person2.class.getAnnotation(Hints.class);
		System.out.println(hints1.value().length);  // 2
		 
		Hint[] hints2 = Person3.class.getAnnotationsByType(Hint.class);
		System.out.println(hints2.length);          // 2	}
	
	
	
	}
}
