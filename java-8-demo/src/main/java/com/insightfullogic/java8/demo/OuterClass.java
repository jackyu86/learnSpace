package com.insightfullogic.java8.demo;

public class OuterClass {
	
	public static void test(){
		String aa ="jack yu";
		new InnerClass() {
			
			@Override
			public void test() {
				int x = aa.hashCode();
				System.out.println(x);
			}
		};
		
	}
	
	
	 public static void test(final String s){
	     //或final String s = "axman";



	  class OuterClass$1 extends InnerClass{

	 

	   private final String s;
	   public OuterClass$1(String s){
	      this.s = s;   
	   }
	   public void test(){
	      int x = s.hashCode();

	      System.out.println(x);

	   }
	  };

	  InnerClass c = new OuterClass$1(s);
	  //其它代码.
	  c.test();
	 }
	 public static void main(String[] args) {
		test("aaaa");
	}

}
