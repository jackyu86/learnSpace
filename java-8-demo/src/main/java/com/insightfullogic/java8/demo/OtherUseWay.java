package com.insightfullogic.java8.demo;

import java.util.Random;
import java.util.concurrent.Callable;

public class OtherUseWay {
	static int sum =0;
	
	public static void main(String[] args) throws Exception {
		//嵌套lambda 使用
		 Callable<Runnable> cl =  () -> () -> {sum += new Random().nextInt();};
		 cl.call().run();
		 System.out.println(sum+"_");
		 boolean hasxx =false;
		 
		 //三元运算符返回函数
		 //true返回42 false 返回24
		 Callable<Integer> ci = hasxx ? (() -> 42) : (() ->24);
		 System.out.println(ci.call());
		 
		 
		 
	}

}
