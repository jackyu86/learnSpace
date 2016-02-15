package com.insightfullogic.java8.demo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.insightfullogic.java8.demo.pojo.Like;
import com.insightfullogic.java8.demo.pojo.User;

public class StreamDemo1 {
	
	private static List<User> userList =Arrays.asList( 
			new User(1, "张三", 12,User.SEX.MALE.getSexStr()),
			new User(2, "李四", 14,User.SEX.FEMALE.getSexStr()),
			new User(3, "王五", 16,User.SEX.MALE.getSexStr()),
			new User(4, "赵六", 18,User.SEX.MALE.getSexStr()),
			new User(5, "三八", 20,User.SEX.FEMALE.getSexStr()),
			new User(6, "三酒", 22,null)
			);
	
	private static List<Like> userList2 =Arrays.asList( 
			new Like("你妹", 1),
			new Like("你00妹", 2),
			new Like("你000妹", 3),
			new Like("你0000妹", 4),
			new Like("你00000妹", 5),
			new Like("你000000妹", 6),
			new Like("你0000000妹", 3),
			new Like("你00000000妹", 4),
			new Like("他000000000妹", 1)
			);

	
	public static void main(String[] args) {
		//stream concat   userList逐个插入userList2全部
	Stream<Object> aaa = userList.stream()
			.flatMap(usr -> Stream.concat(Stream.of(usr),userList2.stream().filter(l ->l!=null && l.getDu() ==  usr.getId()) ) );
	aaa.forEach(a -> System.out.println(a));
		

		
		//stream  generate
	Stream<Double> stream = 	Stream.generate( () -> Math.random());
	stream.limit(10).forEach(d -> System.out.println(d));

	//stream iterate
	Stream<Integer> stream2 = Stream.iterate(1, a -> a.valueOf(1)+1);
	//stream2.forEach(a -> System.out.println(a));
	//stream2.peek(a -> ++a).forEach(a -> System.out.println(a));;
	
	//stream 组合示例
	List<Integer> intLists= Lists.newArrayList(1,1,null,2,3,5,76,4,5,6,8,9,0,4,null,5,6,7,8,9,10);
	int sum = 	intLists.stream().filter(a -> a!=null).distinct().mapToInt(b -> b*2).peek(System.out::println).skip(5).limit(100).sum();
	System.out.println(sum);
		//reduce
		int count = Stream.of(1,2,3).reduce(0,(acc,element) -> acc + element);
		
		//拆分 reduce 类似
		BinaryOperator<Integer> ks = (acc,ele) ->  acc+ele;
		int count2 = ks.apply(ks.apply(ks.apply(0, 1), 2), 3);
		
		System.out.printf("reduce result count    %s   , binaryOperator result count2  %s  ", count,count2);
		System.out.println();
		//min
		User usr = userList.stream().min(Comparator.comparing(User::getAge)).get();
		//max
		User usr2 = userList.stream().max(Comparator.comparing(user -> user.getAge())).get();
		System.out.printf("age min  ->   %s   ; age max  ->  %s  ",usr,usr2);
		System.out.println();
		//三元运算符选择函数
		boolean isMale =true;
		//按性别筛选 1
		Function<List<User>,List<User>> malefunction1  =(users) -> users.stream()
		  .filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())&&u.getSex().equals(User.SEX.MALE.getSexStr()))
		  .collect(Collectors.toList());
		Function<List<User>,List<User>> femalefunction1  =(users) -> users.stream()
				  .filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())&&u.getSex().equals(User.SEX.FEMALE.getSexStr()))
				  .collect(Collectors.toList());
		List<User> userlist1 =  isMale? malefunction1.apply(userList):femalefunction1.apply(userList);
		System.out.println(userlist1);
		
		//按性别筛选 2
		BiFunction<String, List<User>, List<User>> sexFilterFunction = (sexs,users) -> users.stream()
																																							  .filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())&&u.getSex().equals(sexs))
																																							  .collect(Collectors.toList());
		System.out.println(sexFilterFunction.apply("male", userList));
		
		//按性别分组
		userList.stream().filter(u -> u.getSex()!=null &&!"".equals(u.getSex().trim())).collect(Collectors.groupingBy(User::getSex, Collectors.summingInt(p -> 1)));
		
		//对象属性筛选 组装集合
		List<String> lists= userList.stream().map(User::getName).collect(Collectors.toList());
			System.out.println(lists);
			//key 重复会报错 
		Map<String,Integer> ageMap = userList.stream().map(User::getAge).collect(Collectors.toMap(k -> k+"---", v -> v));
		System.out.println(ageMap);
		//组装set   空值报错
		Set<String> setStr =  userList.stream().filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())).map(User::getSex).collect(Collectors.toCollection(TreeSet::new));
		Set<String> setStr2 = userList.stream().filter(u -> u.getSex()!=null && !"".equals(u.getSex().trim())).map(user -> user.getSex()).collect(Collectors.toCollection(LinkedHashSet::new));
		System.out.println(setStr);
		System.out.println(setStr2);
		
		//joining 自定义组装
		String userNames = userList.stream().map(User::getName).collect(Collectors.joining(","));
		System.out.println(userNames);
		
		//合计
		int ageSum = userList.stream().collect(Collectors.summingInt(User::getAge));
		System.out.println(ageSum);

		
		//groupingBy 按照性别分组 形式 female [user1,user*] male[user2,user*]
		Map<String, List<User> >um  =	userList.stream().filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())).collect(Collectors.groupingBy(User::getSex));
		Map<String, List<User> >um2  =	userList.stream().filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())).collect(Collectors.groupingBy(user -> user.getSex()));
		System.out.println(um);
		
		//分组和集合操作嵌套使用
		Object o = Collectors.summingInt(a -> 1);
		
	System.out.println(o.toString() +"summingInt");
	Map<String,Integer>  msi  =  userList.stream().filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim())).collect(Collectors.groupingBy(User::getSex, Collectors.summingInt(p -> 1)));
	System.out.println(msi);
	
	
	
	Map<String, List<String>> map2 = userList.stream().filter(u -> u.getSex()!=null&&!"".equals(u.getSex().trim()))
			.collect( Collectors.groupingBy(User::getSex,  Collectors.mapping(User::getName,  Collectors.toList()))); 
	System.out.println(map2);
	
	}
	
}
