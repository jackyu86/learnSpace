package com.insightfullogic.java8.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.math3.primes.Primes;

public class SuShu {

	private static List<Integer> list = new ArrayList<Integer>();
	
	static 
	{	
		int i = 0;
		for(;;){
			list.add(i);
			i++;
			if(i>10000){
				break;
			}
		}
	}
	//重复添加
	
	static 
	{	
		int i = 0;
		for(;;){
			list.add(i);
			i++;
			if(i>100){
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		
		//查询list 中的素数  转list 输出
		list.stream().map(m -> new Integer(m)).filter(f -> Primes.isPrime(f)).distinct()/*.collect(Collectors.toList())*/.forEach(s -> {System.out.println(s);});;
		
		list.stream()
		.map(m -> new Integer(m))
		.filter(f -> Primes.isPrime(f))
		.distinct()
		//转化成map  默认hashmap
		.collect(Collectors.toMap(k ->  k+"key", v -> v));
		
		
		
		list.stream()
		.map(m -> new Integer(m))
		.filter(f -> Primes.isPrime(f))
		.distinct()
		.collect(Collectors.groupingBy(p -> p, Collectors.summingInt(p -> 1)));
		
		boolean equas2  =   list.stream().anyMatch(f ->  {return f ==2;});
		
		System.out.println(equas2);
		
		
		
		//parallel Stream 并行流  cup不行没发现多快
		
		/*long t1 = System.currentTimeMillis();
		list.parallelStream().forEach(j -> {System.out.println(j);});
		long t2 = System.currentTimeMillis();
		
		System.out.println(" parallel stream  time takes "+(t2-t1));*/
		
		
		//serial Stream 串行流
		
		/*long t3 = System.currentTimeMillis();
		list.stream().forEach(j -> {System.out.println(j);});
		long t4 = System.currentTimeMillis();
		
		System.out.println(" parallel stream  time takes "+(t4-t3));*/
		
		
		/*
		long t5 = System.currentTimeMillis();
		for(Integer i : list){
			System.out.println(i);
		}
		long t6 = System.currentTimeMillis();
		
		System.out.println(" parallel stream  time takes "+(t6-t5));*/
		
		
		
		
		
		
	}
}
