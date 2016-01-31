package com.insightfullogic.java8.demo;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

public class J8TimeAPIDemo1 {
	public static void main(String[] args) {
		
		//Clock
		Clock clock = Clock.systemDefaultZone();
		long millis = clock.millis();
		System.out.println(millis);//当前毫秒时间
		Instant instant = clock.instant();
		Date fromMillis = new Date(millis);
		System.out.println(fromMillis);
		Date date = Date.from(instant);
		System.out.println(date);
		
	}

}
