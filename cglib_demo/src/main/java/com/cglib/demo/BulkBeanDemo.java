package com.cglib.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.cglib.beans.BulkBean;
import net.sf.cglib.core.DebuggingClassWriter;

public class BulkBeanDemo {

	public static void main(String[] args) {
		
		 System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "/home/jack-yu/tmp/2");  
		 String getters[] = new String[] { "getDate", "getNo","getNol","getStr","getList" };
	        String setters[] = new String[] { "setDate", "setNo", "setNol","setStr","setList" };
	        Class types[] = new Class[] { Date.class, Integer.class, Long.class,String.class,List.class };
	        BulkBean bb = BulkBean
	                .create(Source.class, getters, setters, types);
	        Source tb = new Source();
	        tb.setDate(new Date());
	        tb.setNo(1);
	        tb.setNol(123l);
	        tb.setStr("123");
	        List<String> list= new ArrayList<String>();
	        list.add("1");
	        tb.setList(list);
	        //bb.setPropertyValues(tb, new Object[]{new Date(),122});
	        Object objs[] = bb.getPropertyValues(tb);
	        for (int i = 0; i < objs.length; i++) {
	            System.out.println(objs[i]);
	        }
	        
	    }  
	
	
	}
	
