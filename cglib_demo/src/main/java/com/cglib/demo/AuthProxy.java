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

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**   
 * This class is used for ...   
 * @author jack  
 * @version   
 *       1.0, 2016年1月6日 下午2:35:40   
 */
public class AuthProxy implements MethodInterceptor {
	
	//名称
	private String name;
	

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}


	public AuthProxy(String name) {
		super();
		this.name = name;
	}

	


	/* (non-Javadoc)
	 * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], net.sf.cglib.proxy.MethodProxy)
	 */
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		if("jack".equals(this.name)){
			return arg3.invokeSuper(arg0, arg2);
		}else{
			System.out.println("you have no permits!!!!!");
		return null;
		}
	}
	
	

}
