package com.insightfullogic.java8.demo;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * 时间日期API
 * */
public class J8TimeAPIDemo1 {
	public static void main(String[] args) {

		// Clock
		Clock clock = Clock.systemDefaultZone();
		long millis = clock.millis();
		System.out.println(millis);// 当前毫秒时间
		Instant instant = clock.instant();
		Date fromMillis = new Date(millis);
		System.out.println(fromMillis);
		Date date = Date.from(instant);
		System.out.println(date);

		// Timezones
		// 获得可用时区
		Set<String> avaZones = ZoneId.getAvailableZoneIds();
		avaZones.forEach(a -> System.out.println(a));

		ZoneId zone1 = ZoneId.of("Europe/Berlin");
		ZoneId zone2 = ZoneId.of("Brazil/East");
		System.out.println(zone1.getRules());
		System.out.println(zone2.getRules());

		LocalTime localTime1 = LocalTime.now(zone1);
		System.out.println(localTime1);
		LocalTime localTime2 = LocalTime.now(zone2);
		System.out.println(localTime2);
		// 比较本地时间A是够在本地时间B中
		System.out.println(localTime1.isBefore(localTime2));

		long betweenHours = ChronoUnit.HOURS.between(localTime1, localTime2);
		System.out.println(betweenHours);
		long betweenMinutes  = ChronoUnit.MINUTES.between(localTime1, localTime2);
		System.out.println("between minutes" + betweenMinutes);
		
		LocalTime localTime3 = LocalTime.of(12, 20, 55);
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.GERMAN);

		LocalTime localTime4 = LocalTime.parse("11:11",dateTimeFormatter);
		
		System.out.println(localTime4);
		
		//LocalDate
		
		LocalDate today = LocalDate.now();
		LocalDate localDateOf = LocalDate.of(2016, 01, 30);
		System.out.printf("now : %s , localDateOf : %s ",today,localDateOf);
		
		System.out.println();
		//明天的日期
		LocalDate tomorrow  = today.plus(1,ChronoUnit.DAYS);
		LocalDate nextWeek = today.plus(1,ChronoUnit.WEEKS);
		System.out.printf("tomorrow :  %s  , nextWeek : %s",tomorrow,nextWeek);
		
		LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
		DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
		System.out.println(dayOfWeek);    // FRIDAY<span style="font-family: Georgia, 'Times New Roman', 'Bitstream Charter', Times, serif; font-size: 13px; line-height: 19px;">Parsing a LocalDate from a string is just as simple as parsing a LocalTime:</span>
		
		DateTimeFormatter germanFormatter =
			    DateTimeFormatter
			        .ofLocalizedDate(FormatStyle.MEDIUM)
			        .withLocale(Locale.GERMAN);
			 
			LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
			System.out.println(xmas);   // 2014-12-24
			
			
			LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);
			 
			DayOfWeek dayOfWeek2 = sylvester.getDayOfWeek();
			System.out.println(dayOfWeek2);      // WEDNESDAY
			 
			Month month = sylvester.getMonth();
			System.out.println(month);          // DECEMBER
			 
			long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
			System.out.println(minuteOfDay);    // 1439
			
			Instant instanw = sylvester.atZone(ZoneId.systemDefault()).toInstant();
			
					Date legacyDate = Date.from(instanw);
					System.out.println(legacyDate);// Wed Dec 31 23:59:59 CET 2014
		
		
					DateTimeFormatter formatter =
						    DateTimeFormatter
						        .ofPattern("MMM dd, yyyy - HH:mm");
					
						LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
						String string = formatter.format(parsed);
						System.out.println(string);     // Nov 03, 2014 - 07:13
		
		
		
		
		

	}

}
