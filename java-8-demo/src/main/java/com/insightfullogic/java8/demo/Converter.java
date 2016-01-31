package com.insightfullogic.java8.demo;

@FunctionalInterface
public interface Converter<F,T> {
	
	T convert(F from);
	
}
