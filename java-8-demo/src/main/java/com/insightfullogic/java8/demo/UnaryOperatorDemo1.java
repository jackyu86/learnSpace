package com.insightfullogic.java8.demo;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import com.insightfullogic.java8.demo.pojo.User;

/**
 * 一元运算符
 * @author jack-yu
 *
 */
public class UnaryOperatorDemo1 {
	
	private static List<User> userList =Arrays.asList( 
			new User(1, "张三", 12,User.SEX.MALE.getSexStr()),
			new User(2, "李四", 14,User.SEX.FEMALE.getSexStr()),
			new User(3, "王五", 16,User.SEX.MALE.getSexStr()),
			new User(4, "赵六", 18,User.SEX.MALE.getSexStr()),
			new User(5, "三八", 20,User.SEX.FEMALE.getSexStr()),
			new User(5, "三酒", 22,null)
			);
	
public static void main(String[] args) {
		UnaryOperator<User> userM = (user) -> { user.setName(user.getName()+"-->operator");return user;};
		System.out.println(userM.apply(new User(6, "jackyu", 30, "male")));
}
}
