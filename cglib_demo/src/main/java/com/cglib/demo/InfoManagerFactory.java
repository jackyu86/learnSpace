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

import net.sf.cglib.proxy.Enhancer;

/**   
 * This class is used for ...   
 * @author jack  
 * @version   
 *       1.0, 2016年1月6日 下午2:10:53   
 */
public class InfoManagerFactory {
	
	   private static InfoManager manger = new InfoManager();
	    /**
	     * 创建原始的InfoManager
	     * 
	     * @return
	     */
	    public static InfoManager getInstance() {
	        return manger;
	    }
	    
	    public static InfoManager getAuthInstance(AuthProxy authProxy){
	    	Enhancer enhancer = new Enhancer();
	    	
	    	enhancer.setSuperclass(InfoManager.class);
	    	
	    	enhancer.setCallback(authProxy);
	    	
	    	return null;
	    }

}
