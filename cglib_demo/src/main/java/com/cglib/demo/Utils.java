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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**   
 * This class is used for ...   
 * @author jack  
 * @version   
 *       1.0, 2016年1月7日 下午9:14:31   
 */
public class Utils {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void main(String[] args) {
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
		from.setList(list);
		from.setDate(new Date());
		
		Target target = new Target();
/*		boolean a =from.getClass().isAssignableFrom(target.getClass());
		System.out.println(a+"---");*/

		java.lang.reflect.Field[] fields = target.getClass().getDeclaredFields();
		
		
		try {
			System.out.println(checkData(new Date(), fields[3]));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static Object checkData(Object value, Field field)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException {
		//Object value = v.getValue();
		// System.out.println(value.getClass().getTypeName());
		// Object obj = clazz.newInstance();
		field.setAccessible(true);
		// 跳过静态属性
		/*
		 * String mod = Modifier.toString(field.getModifiers()); if
		 * (mod.indexOf("static") != -1)
		 */

		if(value!=null){
		// 得到属性的类名
		String className = field.getType().getSimpleName().intern();
		// 得到属性值
		if (className.equalsIgnoreCase("String")) {
			if (value instanceof Date) {
				return sdf.format(value).toString(); 
			}else
			return value.toString();
		} else if (className.equalsIgnoreCase("list")) {
			// 需手动配置
		} else if (className.equalsIgnoreCase("map")) {
			// 需手动配置
		} else if (className.equalsIgnoreCase("set")) {
			// 需手动配置
		} else if (className.equalsIgnoreCase("boolean")) {
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
		} else if (className.equalsIgnoreCase("int")) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Integer.parseInt(value.toString());
			}
			// 损失精度的不处理
		} else if (className.equalsIgnoreCase("float")) {
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
		} else if (className.equalsIgnoreCase("double")) {
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

		} else if (className.equalsIgnoreCase("long")) {
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

		} else if (className.equalsIgnoreCase("Date")) {
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
	
	public static Object formatData(Object value, Type fieldtype)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException {
		//Object value = v.getValue();
		// System.out.println(value.getClass().getTypeName());
		// Object obj = clazz.newInstance();
		//field.setAccessible(true);
		// 跳过静态属性
		/*
		 * String mod = Modifier.toString(field.getModifiers()); if
		 * (mod.indexOf("static") != -1)
		 */
		if(value!=null){
		// 得到属性的类名
		String className =  fieldtype.getClass().getSimpleName().intern();
		// 得到属性值
		if (className.equalsIgnoreCase("String")) {
			if (value instanceof Date) {
				return sdf.format(value).toString(); 
			}else
			return value.toString();
		} else if (className.equalsIgnoreCase("list")) {
			// 需手动配置
		} else if (className.equalsIgnoreCase("map")) {
			// 需手动配置
		} else if (className.equalsIgnoreCase("set")) {
			// 需手动配置
		} else if (className.equalsIgnoreCase("boolean")) {
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
		} else if (className.equalsIgnoreCase("int")) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Integer.parseInt(value.toString());
			}
			// 损失精度的不处理
		} else if (className.equalsIgnoreCase("float")) {
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
		} else if (className.equalsIgnoreCase("double")) {
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

		} else if (className.equalsIgnoreCase("long")) {
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

		} else if (className.equalsIgnoreCase("Date")) {
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
