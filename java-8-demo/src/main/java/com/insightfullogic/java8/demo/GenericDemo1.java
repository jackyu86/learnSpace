package com.insightfullogic.java8.demo;

import java.util.List;

import com.insightfullogic.java8.demo.pojo.generic.GenericBeanA;
import com.insightfullogic.java8.demo.pojo.generic.GenericBeanB;
import com.insightfullogic.java8.demo.pojo.generic.GenericBeanC;

														//限定R必须继承自T
public class GenericDemo1<T, R extends T> {

	public <F,S extends T> F testGenericMethodDefine(F f , S s){
		return f;
	}

	public <T,S> T testGenericMethodDefine1(List<T> listt, S s){
		return listt.get(0);
	}
	
	public static void main(String[] args) {
		GenericDemo1<GenericBeanA,GenericBeanB> a = new GenericDemo1<GenericBeanA, GenericBeanB>();
		System.out.println(	a.testGenericMethodDefine(new GenericBeanB(), new GenericBeanC()));
	}
	
	

}
