package com.insightfullogic.java8.demo;

import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.insightfullogic.java8.demo.pojo.User;

public class StreamDemo1 {
	
	private static List<User> userList =Arrays.asList( 
			new User(1, "张三", 12,User.SEX.MALE.getSexStr()),
			new User(2, "李四", 14,User.SEX.FEMALE.getSexStr()),
			new User(3, "王五", 16,User.SEX.MALE.getSexStr()),
			new User(4, "赵六", 18,User.SEX.MALE.getSexStr()),
			new User(5, "三八", 20,User.SEX.FEMALE.getSexStr())
			);

	
	public static void main(String[] args) {
		//对象属性筛选 组装集合
		List<String> lists= userList.stream().map(User::getName).collect(Collectors.toList());
			System.out.println(lists);
			//key 重复会报错
		Map<String,Integer> ageMap = userList.stream().map(User::getAge).collect(Collectors.toMap(k -> k+"---", v -> v));
		System.out.println(ageMap);
		//组装set   
		Set<String> setStr =  userList.stream().map(User::getSex).collect(Collectors.toCollection(TreeSet::new));
		System.out.println(setStr);
		
		//joining 自定义组装
		String userNames = userList.stream().map(User::getName).collect(Collectors.joining(","));
		System.out.println(userNames);
		
		//合计
		int ageSum = userList.stream().collect(Collectors.summingInt(User::getAge));
		System.out.println(ageSum);

		
		//groupingBy 按照性别分组 形式 female [user1,user*] male[user2,user*]
		Map<String, List<User> >um  =	userList.stream().collect(Collectors.groupingBy(User::getSex));
		System.out.println(um);
		
		//分组和集合操作嵌套使用
		Object o = Collectors.summingInt(a -> 1);
		
	System.out.println(o.toString() +"summingInt");
	Map<String,Integer>  msi  =  userList.stream().collect(Collectors.groupingBy(User::getSex, Collectors.summingInt(p -> 1)));
	System.out.println(msi);
	
	
	
	Map<String, List<String>> map2 = userList.stream()
			.collect( 
					Collectors.groupingBy(
                            User::getSex, 
                            Collectors.mapping(User::getName, 
                                    Collectors.toList()))); 
	System.out.println(map2);
	
	}
	
}
