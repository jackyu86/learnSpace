/*
 * Copyright 2015 cloudshops.cn All right reserved. This software is the
 * confidential and proprietary information of cloudshops.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cloudshops.cn.
 */

/**
 * 
 */
package com.cglib.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import net.sf.cglib.core.DebuggingClassWriter;

/**
 * This class is used for ...
 * 
 * @author jack
 * @version 1.0, 2016年1月6日 下午2:11:14
 */
public class Client {
	public static void main(String[] args) {
		/*System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,
				"/home/jack-yu/tmp/1");*/
		//for(;;){
			long t1 = System.currentTimeMillis();
			NewCopier copier = NewCopier.create(Source.class, Target.class, true);
			Source from = new Source();
			from.setStr("abc");
			from.setNo(123);
			from.setNol(123l);
			List<String> list = new ArrayList<String>();
			list.add("a");
			list.add("b");
			list.add("c");
			list.add("d");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			list.add("e");
			from.setList(list);
			from.setDate(new Date());
			
			Target to = new Target();
			Converter converter = new BigIntConverter();
			copier.copy(from, to, converter); // 使用converter类
			long t2 = System.currentTimeMillis();
			
			System.out.println(" beans  copier  time takes "+(t2-t1));
			
			System.out.println(to+"____________________________________________________________________________________________________");
		//}
		
		
		/*
		//for(;;){
			long t1 = System.currentTimeMillis();
			BeanCopier copier2 = BeanCopier.create(Source.class, Target.class, false);
			Source from1 = new Source();
			from1.setStr("abc");
			from1.setNo(123);
			from1.setNol(123l);
			List<String> list2 = new ArrayList<String>();
			list2.add("a");
			list2.add("b");
			list2.add("c");
			list2.add("d");
			list2.add("e");
			from1.setList(list2);
			from1.setDate(new Date());
			long t2 = System.currentTimeMillis();
			Target to2= new Target();
			
			copier2.copy(from1, to2, null); // 使用converter类
			System.out.println(" beans  copier  time takes "+(t2-t1));
			System.out.println(to2+"____________________________________________________________________________________________________");*/
	//	}
		
		/*
		 * Client c = new Client(); c.anyonecanManager();
		 */
	}

	/**
	 * 模拟：没有任何权限要求，任何人都可以操作
	 */
	public void anyonecanManager() {
		System.out.println("any one can do manager");
		InfoManager manager = InfoManagerFactory.getInstance();
		doCRUD(manager);
		separatorLine();
	}

	/**
	 * 对Info做增加／更新／删除／查询操作
	 * 
	 * @param manager
	 */
	private void doCRUD(InfoManager manager) {
		manager.create();
		manager.update();
		manager.delete();
		manager.query();
	}

	/**
	 * 加一个分隔行，用于区分
	 */
	private void separatorLine() {
		System.out.println("################################");
	}

}

class BigIntConverter implements net.sf.cglib.core.Converter {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.cglib.core.Converter#convert(java.lang.Object,
	 * java.lang.Class, java.lang.Object)
	 */
	public Object convert(Object value, Class target, Object context) {
	//	System.out.println(value);
		try {
			return formatData(value, target);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private  Object formatData(Object value, Class<?> field)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException {

		if(value!=null){
			String className="String";
		// 得到属性的类名
				className=field.getName();
		// 得到属性值
		if (field.isAssignableFrom(String.class)) {
			if (value instanceof Date) {
				return sdf.format(value).toString(); 
			}else
			return value.toString();
		}//默认返回ArrayList 
		else if (field.isAssignableFrom(List.class)) {
			if (value instanceof Collection) {
				List list = new ArrayList();
				list.addAll((Collection)value);
				return list; 
			}
			
		} else if (field.isAssignableFrom(Map.class)) {
			// 需手动配置
		} else if (field.isAssignableFrom(Set.class)) {
			// 需手动配置
		} else if (field.isAssignableFrom(boolean.class)||field.isAssignableFrom(Boolean.class)) {
			if (value instanceof Integer) {
				if ((Integer) value == 0) {
					return false;
				} else if ((Integer) value == 1) {
					return true;
				}
			} else if (value instanceof String
					&& StringUtils.isNotEmpty(value.toString())) {
				if (value.toString().equals("false")) {
					return false;
				} else if (value.toString().equals("true")) {
					return true;
				}
			}
		} else if (field.isAssignableFrom(int.class)||field.isAssignableFrom(Integer.class)) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Integer.parseInt(value.toString());
			}
			// 损失精度的不处理
		} else if (field.isAssignableFrom(float.class)||field.isAssignableFrom(Float.class)) {
			if (value instanceof Integer) {
				return Float.parseFloat(String.valueOf(value));
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Float.parseFloat(value.toString());
			} else if (value instanceof Float) {
				return value;
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Double) {
				return Float.parseFloat(String.valueOf(value));
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Long) {
				return Float.parseFloat(String.valueOf(value));
			}
		} else if (field.isAssignableFrom(double.class)||field.isAssignableFrom(Double.class)) {
			if (value instanceof Integer) {
				return Double.parseDouble(String.valueOf(value));
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Double.parseDouble(String.valueOf(value));
			} else if (value instanceof Float) {
				return Double.parseDouble(String.valueOf(value));
			} else if (value instanceof Double) {
				return value;
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Long) {
				return Double.parseDouble(String.valueOf(value));
			}

		} else if (field.isAssignableFrom(long.class)||field.isAssignableFrom(Long.class)) {
			if (value instanceof Integer) {
				return Long.parseLong(String.valueOf(value));
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Long.parseLong(String.valueOf(value));
			} else if (value instanceof Float) {
				return (Long) value;
			} else if (value instanceof Double) {
				return Long.parseLong(String.valueOf(value));
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Long) {
				return value;
			}

		} else if (field.isAssignableFrom(Date.class)) {
			if (value instanceof Date) {
				return value;
			}else if(value instanceof String){
				return sdf.format(value).toString();	 
			  }
			 
		}
		return null;
		}else{
			return null;
		}
	}

}
