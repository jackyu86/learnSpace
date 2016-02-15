package com.insightfullogic.java8.demo.pojo;

public class Like {

	private String name;
	private int du;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDu() {
		return du;
	}
	public void setDu(int du) {
		this.du = du;
	}
	public Like(String name, int du) {
		super();
		this.name = name;
		this.du = du;
	}
	@Override
	public String toString() {
		return "Like [name=" + name + ", du=" + du + "]";
	}
	public Like() {
		super();
	}
	
	
}
