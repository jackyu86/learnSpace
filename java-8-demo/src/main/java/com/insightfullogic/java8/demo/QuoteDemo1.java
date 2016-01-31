package com.insightfullogic.java8.demo;





public class QuoteDemo1 {
	
	public static void main(String[] args) {
		Converter<String,Integer> converter = Integer ::valueOf;
		String a="123";
		Integer conInt  = converter.convert(a);
		System.out.println(conInt);
	}

}
