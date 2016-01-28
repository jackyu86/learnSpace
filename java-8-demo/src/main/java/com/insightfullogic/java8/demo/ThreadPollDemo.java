package com.insightfullogic.java8.demo;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPollDemo {

	public static void main(String[] args) {
		//固定大小的线程池
		ExecutorService threadPool2 = Executors.newFixedThreadPool(10);
		
		 //
		ExecutorCompletionService<Integer> executorCompletionService = new ExecutorCompletionService<Integer>(threadPool2);
		 
		for (int i = 0; i < 10; i++) {
		 //Callable 使用submit
		executorCompletionService.submit(new Callable<Integer>() {
		 
		@Override
		 
		public Integer call() throws Exception {
		 
		int sum = 0;
		 
		for (int j = 0; j < 10; j++) {
		 
		sum += new Random().nextInt(1000);
		 
		}
		 
		System.out.println("num:" + sum);
		 
		return sum;
		 
		}
		 
		});
		 
		}
		 
		int sum = 0;
		 
		for (int i = 0; i < 10; i++) {
		 
		try {
		 
		int num = executorCompletionService.take().get();
		 
		sum += num;
		 
		} catch (InterruptedException e) {
		 
		e.printStackTrace();
		 
		} catch (Exception e) {
		 
		e.printStackTrace();
		 
		}
		 
		}
		 
		System.out.println("sum:" + sum);

	}
	
}
