package com.insightfullogic.java8.demo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.insightfullogic.java8.demo.pojo.Book;

public class BiFunctionDemo1 {
	private static List<Book> articleList =Arrays.asList( 
			new Book("jack", "java", "jack-权威指南",new Date(1453946110930l)),
			new Book("richard", "java", "java8-函数式编程",new Date(1452956010930l)),
			new Book("peter", "python", "Machine Learning in Action",new Date(1451956110630l)),
			new Book("peter2", "python", "pyhon-权威指南",new Date(1450955110930l)),
			new Book("richard", "java", "编程思想(java)",new Date())
			);
	public static void main(String[] args) {

		//by author
		BiFunction<String, List<Book>, List<Book>> biFunction1 = (name,article) -> article.stream()
																																							 .filter(al -> al.getAuthor().equals(name))
																																							 .collect(Collectors.toList());
		List<Book> als =biFunction1.apply("richard", articleList);
		
		//als.stream().forEach(a -> System.out.println(a));
		
		//by tag
		BiFunction<String,List<Book>,List<Book>> bifunction2 = (tag,article) -> article.stream()
																																						.filter(al -> al.getTag().equals(tag))
																																						.collect(Collectors.toList());
		List<Book> als2 = bifunction2.apply("python", articleList);
		//als2.forEach( al -> System.out.println(al));
		
		
		
		//排序
		Function<List<Book>,List<Book>> function1 =  (books) -> books.parallelStream()
																														.sorted((x,y) -> y.getReleaseDate().compareTo(x.getReleaseDate()))
																														.collect(Collectors.toList());
		List<Book> als3 = function1.apply(articleList);
		//als3.forEach(al -> System.out.println(al));
		
		
		//other
		//
	Function<List<Book>, Optional<Book>> afunction = (books) ->books.parallelStream().findFirst();
	Function<Optional<Book>,String> thenFunctiion = (book) ->{return book.get().getName();};
	/*Optional<Book> book = function1.apply(articleList).parallelStream().findFirst();
	System.out.println(book);*/
	//System.out.println(afunction.compose(function1).apply(articleList));
//	System.out.println(afunction.andThen(thenFunctiion).apply(articleList));
		//获取第一个前排序
	System.out.println(afunction.compose(function1).andThen(thenFunctiion).apply(articleList));
	


	
		
	}

}
