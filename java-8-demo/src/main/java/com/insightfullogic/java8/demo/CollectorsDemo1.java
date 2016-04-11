package com.insightfullogic.java8.demo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.insightfullogic.java8.demo.pojo.Like;
import com.insightfullogic.java8.demo.pojo.User;

public class CollectorsDemo1 {
	
	private static List<User> userList =Arrays.asList( 
			new User(1, "张三", 12,User.SEX.MALE.getSexStr()),
			new User(2, "李四", 14,User.SEX.FEMALE.getSexStr()),
			new User(3, "王五", 16,User.SEX.MALE.getSexStr()),
			new User(4, "赵六", 18,User.SEX.MALE.getSexStr()),
			new User(5, "三八", 20,User.SEX.FEMALE.getSexStr()),
			new User(6, "三酒", 22,null)
			);
	
	private static List<Like> likes = Arrays.asList(
			new Like("sao",2)
			);
	
	public static void main(String[] args) {
		String aaaa ="aaaaaaaaaaaaaaaaaaaaaaaa";
		
		userList.parallelStream().filter(u -> u.getName()!=null&&!"".equals(u.getName())).flatMap(uu -> Stream.concat(Stream.of(uu), Stream.of(likes))).forEach(a -> System.out.println("flatMap collect collections"+a));
		String usrNameJoins=userList.stream().map(u -> u.getName()).collect(Collectors.joining(",", "[", "]"));
		System.out.println(usrNameJoins);
		String aa =	userList.stream().filter(u -> u.getName()!=null).map(u -> u.getName()).reduce(aaaa,(aaa,ele) -> aaa+ele);
		System.out.println(aa);
		StringBuilder sb =userList.stream().filter(u -> u.getName()!=null).map(u -> u.getName()).reduce(new StringBuilder(), (builer,usrName) -> {
																										if(builer.length()>0){
																											builer.append(",");
																										}
																										builer.append(usrName);
																										return builer;
		}, (left,right) -> left.append(right));
		sb.insert(0, "[");
		sb.append("]");
		System.out.println(sb.toString());
		
		
		Optional<User> u =userList.stream().collect(Collectors.maxBy(Comparator.comparing(User :: getAge)));
		System.out.println(u);
		
		//分块
		Map<Boolean, List<User>> Object = userList.stream().collect(Collectors.partitioningBy(usr -> usr.getAge()>16));
		List<User> usr1 = Object.get(true);
		System.out.println(usr1);
		Map<String, List<User>> o2 =	userList.stream().filter(uu -> uu.getSex()!=null&&!"".equals(uu.getSex())).collect(Collectors.groupingBy(usr11 -> usr11.getSex()));
		List<User> u2 = o2.get("male");
		System.out.println(u2);
	}
	

}
