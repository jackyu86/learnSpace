package com.cglib.demo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.Converter;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.Local;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

/**
 * This class is used for BeanCopier扩展 实体必须严格符合javabean标准，否则会出现找到不到方法等异常
 * 1.读取映射(一期:写死转换类型-)
 * 2.程序干预 setBeanConfigMap
 * 
 * @author jack
 * @version 1.0, 2016年1月7日 下午8:17:22
 */
abstract public class NewCopier {
	private static Map<String, String> mappingMap = new LinkedHashMap<String, String>();
	//模拟-设想读取缓存,前段维护映射配置。
	static {
		// key 目标 value 源
		mappingMap.put("str123", "str");
		mappingMap.put("no123", "no");
		mappingMap.put("nol123", "nol");
		mappingMap.put("date123", "date");
		mappingMap.put("list123", "list");
	}
	
	protected static Map<String,String> beanConfigMap =null;
	

	public void setBeanConfigMap(Map<String, String> beanConfigMap) {
		beanConfigMap = beanConfigMap;
	}

	// 生产NewBeanCopier key
	private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey) KeyFactory
			.create(BeanCopierKey.class);

	private static final Type CONVERTER = TypeUtils
			.parseType("net.sf.cglib.core.Converter");
	
	private static final Type BEANCONFIG = TypeUtils.parseType(" java.util.Map");

	private static final Type NEW_COPIER = TypeUtils
			.parseType("com.cglib.demo.NewCopier");

	private static final Signature COPY = new Signature("copy", Type.VOID_TYPE,
			new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_OBJECT,
					CONVERTER });

	private static final Signature CONVERT = TypeUtils
			.parseSignature("Object convert(Object, Class, Object)");

	interface BeanCopierKey {
		public Object newInstance(String source, String target,
				boolean useConverter);
	}

	public static NewCopier create(Class source, Class target,
			boolean useConverter) {
		Generator gen = new Generator();
		gen.setSource(source);
		gen.setTarget(target);
		gen.setUseConverter(useConverter);
		return gen.create();
	}

	abstract public void copy(Object from, Object to, Converter converter);

	public static class Generator extends AbstractClassGenerator {
		private static final Source SOURCE = new Source(
				NewCopier.class.getName());
		private Class source;
		private Class target;
		private boolean useConverter;

		public Generator() {
			super(SOURCE);
		}

		public void setSource(Class source) {
			if (!Modifier.isPublic(source.getModifiers())) {
				setNamePrefix(source.getName());
			}
			this.source = source;
		}

		public void setTarget(Class target) {
			if (!Modifier.isPublic(target.getModifiers())) {
				setNamePrefix(target.getName());
			}

			this.target = target;
		}

		public void setUseConverter(boolean useConverter) {
			this.useConverter = useConverter;
		}

		protected ClassLoader getDefaultClassLoader() {
			return source.getClassLoader();
		}

		protected ProtectionDomain getProtectionDomain() {
			return ReflectUtils.getProtectionDomain(source);
		}

		public NewCopier create() {
			Object key = KEY_FACTORY.newInstance(source.getName(),
					target.getName(), useConverter);
			return (NewCopier) super.create(key);
		}

		public void generateClass(ClassVisitor v) {
			Type sourceType = Type.getType(source);
			Type targetType = Type.getType(target);
			ClassEmitter ce = new ClassEmitter(v);
			//拷贝类开始
			ce.begin_class(Constants.V1_2, Constants.ACC_PUBLIC,
					getClassName(), NEW_COPIER, null, Constants.SOURCE_FILE);
			//无参函数
			EmitUtils.null_constructor(ce);
			
			// 方法开始 copy
			CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);

			// name 字段名
			// readMethodName 读方法名
			// writeMethodName 写方法名
			// 初始化源getter方法
			PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(source);

			PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);

			Map<String, PropertyDescriptor> names = new HashMap<String, PropertyDescriptor>();
			// 读取配置映射

			for (int i = 0; i < getters.length; i++) {
				names.put(getters[i].getName(), getters[i]);
			}
			// 入参目标
			Local targetLocal = e.make_local();
			// 入参源
			Local sourceLocal = e.make_local();
			// 装配对象句柄
			if (useConverter) {
				e.load_arg(1);
				// 效验转换
				e.checkcast(targetType);
				// 句柄转存
				e.store_local(targetLocal);
				e.load_arg(0);
				e.checkcast(sourceType);
				e.store_local(sourceLocal);
			} else {
				e.load_arg(1);
				e.checkcast(targetType);
				e.load_arg(0);
				e.checkcast(sourceType);
			}
			// 迭代set生产set×××
			for (int i = 0; i < setters.length; i++) {
				PropertyDescriptor setter = setters[i];
				// 默认获取名字相同的
				//PropertyDescriptor getter = (PropertyDescriptor) names
					//	.get(setter.getName());  
				//(1)配置查找
				PropertyDescriptor getter =null;
				if(mappingMap!=null){
					getter	=checkAndFindByMap(mappingMap, names, setter);
					//(2)程序干预
					if(getter==null){
						if(beanConfigMap!=null&&!beanConfigMap.isEmpty()){
							getter	=checkAndFindByMap(beanConfigMap, names, setter);
						}
					}
				}
				//(3)同名配置		
				if(getter==null){
					 getter = (PropertyDescriptor) names
								.get(setter.getName());  
				}
				
				if (getter != null) {
					// 获取源的读取方法
					// getDate()Ljava/util/Date;
					// public java.util.Date com.cglib.demo.Source.getDate()
					MethodInfo read = ReflectUtils.getMethodInfo(getter
							.getReadMethod());
					// 获取目标的设置方法
					MethodInfo write = ReflectUtils.getMethodInfo(setter
							.getWriteMethod());
					if (useConverter) {
						Type setterType = write.getSignature()
								.getArgumentTypes()[0];
						// load句柄 localTarget
						e.load_local(targetLocal);
						// load参数 paramConverter
						e.load_arg(2);
						// load句柄 localSource
						e.load_local(sourceLocal);
						// 执行read方法 localSource.getDate()
						e.invoke(read);
						// CGLIB$load_class$java$2Eutil$2EDate 
						e.box(read.getSignature().getReturnType());
						// CGLIB$load_class$java$2Eutil$2EDate
						EmitUtils.load_class(e, setterType);
						// "setDate"
						e.push(write.getSignature().getName());
						// paramConverter.convert
						e.invoke_interface(CONVERTER, CONVERT);
						// 最前面的(Date)
						e.unbox_or_zero(setterType);
						// 执行 write 方法 localTarget.setDate()
						e.invoke(write);
					}
					// 判断目标是否为源的父类
					else if (compatible(getter, setter)) {
						/*
						 * getter.getPropertyType();
						 * System.out.println(getter.getValue
						 * (getter.getName()));
						 */
						e.dup2();
						e.invoke(read);
						e.invoke(write);
					} /*else { // 不同类型
						System.out.println(write.getSignature());
						Type setterType = write.getSignature()
								.getArgumentTypes()[0];
						// Utils.formatData(value, fieldtype)
						System.out.println(setterType);
						// setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
						setter.getPropertyType();
					}*/
				}
			}
			e.return_value();
			e.end_method();
			ce.end_class();
		}

		private static boolean compatible(PropertyDescriptor getter,
				PropertyDescriptor setter) {
			// TODO: allow automatic widening conversions?
			return setter.getPropertyType().isAssignableFrom(
					getter.getPropertyType());
		}

		protected Object firstInstance(Class type) {
			return ReflectUtils.newInstance(type);
		}

		protected Object nextInstance(Object instance) {
			return instance;
		}
		private PropertyDescriptor checkAndFindByMap(Map<String,String> mappingMap,Map<String, PropertyDescriptor>  getterMap,PropertyDescriptor setter){
			//PropertyDescriptor getter = (PropertyDescriptor) names
			//.get(setter.getName());
			
			if(mappingMap!=null&&!mappingMap.isEmpty()&&getterMap!=null&&!getterMap.isEmpty()){
				
				String getterFieldName = mappingMap.get(setter.getName());
				
				if(getterFieldName!=null && !"".equals(getterFieldName)){
					PropertyDescriptor getter = (PropertyDescriptor) getterMap
							.get(getterFieldName);
					return getter;
				}
			}
			
			return  null;
		}
	}
}
