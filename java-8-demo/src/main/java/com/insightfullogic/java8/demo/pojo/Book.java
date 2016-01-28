package com.insightfullogic.java8.demo.pojo;

import java.util.Date;

public class Book {
	private String author;
	private String tag;
	private String name;
	private Date releaseDate;
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Book(String author, String tag, String name, Date releaseDate) {
		super();
		this.author = author;
		this.tag = tag;
		this.name = name;
		this.releaseDate = releaseDate;
	}
	@Override
	public String toString() {
		return "Article [author=" + author + ", tag=" + tag + ", name=" + name
				+ ", releaseDate=" + releaseDate + "]";
	}
	
	
	 
}
