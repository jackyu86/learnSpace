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

import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;

/**   
 * This class is used for ...   
 * @author jack  
 * @version   
 *       1.0, 2016年1月6日 下午3:13:56   
 */
public class demo1 {

	
	public static void main(String[] args) {
	 Signature aa =
			      TypeUtils.parseConstructor("Throwable");
	 	System.out.println(aa);
	}
}
