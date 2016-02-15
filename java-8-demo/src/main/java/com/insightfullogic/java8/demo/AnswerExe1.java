package com.insightfullogic.java8.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.insightfullogic.java8.examples.chapter5.StringCombiner;

public class AnswerExe1 {
	public static List<String> names = Arrays.asList("jack","john","pual","jack","john","john","pual","john","pual","jack");
	
	public static void main(String[] args) {
		//计算出现次数
		Map<String, Long> map = names.stream().collect(Collectors.groupingBy(n -> n, HashMap::new, Collectors.counting()));
	String aaa =	names.stream().reduce(new StringCombiner(",", "[", "]"), StringCombiner::add, StringCombiner::merge).toString();
	System.out.println(aaa);
		System.out.println(map);
	}

}
