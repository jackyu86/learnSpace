package com.insightfullogic.java8.demo;

import java.util.HashMap;
import java.util.Map;

public class MapNewAPI {
	static Map<Integer,String> map = new HashMap<Integer, String>();
	static{
		int i = 1;
		for(;;){
			i++;
			map.putIfAbsent(i, i+"val");
			if(i>10){
				map.putIfAbsent(null, null);
				map.putIfAbsent(null, null);
				map.putIfAbsent(null, null);
				map.putIfAbsent(null, null);
				map.putIfAbsent(null, null);
				break;
			}
		}
	}
	public static void main(String[] args) {
		//存在修改
		String aaa = map.computeIfPresent(3, (num,value) -> value+num);
		System.out.println(aaa);
		String bbb = map.get(3);
		System.out.println(bbb);
		//不存在添加
		String ccc = map.computeIfAbsent(23, key -> "abc"+key);
		System.out.println(ccc);
		System.out.println(map.containsKey(4));
		
		//只有完全匹配时才真正删除
		boolean removed = map.remove(3, "3val3");
		System.out.println(removed);
		
		map.getOrDefault(44, "not found");
		
		//map 合并
		//合并操作先看map中是否没有特定的key/value存在，如果是，则把key/value存入map，否则merging函数就会被调用，对现有的数值进行修改。
		map.merge(5, "5val", (value,newValue) -> value.concat(newValue));
		map.merge(5, "5newval", (value,newValue) -> value.concat(newValue));
		
		System.out.println(map);
	}

}
