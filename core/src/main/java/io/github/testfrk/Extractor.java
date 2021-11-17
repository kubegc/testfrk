/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.github.testfrk.utils.AnnoUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 * 
 * find all methods with a specified annotation.
 * e.g, RequestMapping
 */
public class Extractor {

	public final static Logger m_logger = Logger.getLogger(Extractor.class.getName());
	
	/**
	 * @param classSet         待分析的类集合，见Scanner.scan
	 * @return 类名和方法名集合, 比如{io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 * @throws Exception  异常
	 */
	public static Map<String, List<MethodAndType>> extract(Set<Class<?>> classSet) throws Exception {
		return extract(classSet, Constants.DEFAULT_ALL);
	}
	
	
	/**
	 * @param classSet          待分析的类集合，见Scanner.scan
	 * @param labels         Annotation用于判断请求类型的方法名，格式见Constants.DEFAULT_POST
	 * @return 类名和方法名集合, 比如{io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 * @throws Exception 异常
	 */
	public static Map<String, List<MethodAndType>> extract(Set<Class<?>> classSet, Label label) throws Exception {
		
		Map<String, List<MethodAndType>> mapper = new HashMap<>();
		
		// no class
		if (classSet == null) {
			throw new NullPointerException("参数classSet不能为空");
		}
		
		// for each class
		for (Class<?> c : classSet) {

			// classname
			String cn = c.getName();
			
			// ignore this class because of be analyzed 
			if (mapper.containsKey(cn)) {
				continue;
			}
			
			// classname-methods mapping
			mapper.put(cn, extractValues(c, label));
		}
		
		return mapper;
	}

	/**
	 * @param c             具体类名
	 * @param label         Annotation用于判断请求类型的方法名，格式见Constants.DEFAULT_POST
	 * @return 类钟包含Annotation的方法名
	 * @throws Exception    异常
	 */
	protected static List<MethodAndType> extractValues(Class<?> c, Label label) throws Exception {
		
		List<MethodAndType> values = new ArrayList<>();
		
		// for each method
		for (Method m : c.getDeclaredMethods()) {
			// just focus on the method has a specified annotation and labels
			try {
				
				Object value = AnnoUtil.getValue(
						m.getAnnotation(label.getRequestAnnotation()), 
						label.getRequestTypeFunction());
				// 用户期望所有的请求类型
				if ("ALL".equals(label.getRequestTypeValue()) ||
						// 这个请求与用户期望的一致
						value == label.getRequestTypeValue()) {
					values.add(new MethodAndType(value.toString(), m));
				} else {
					m_logger.severe("不支持该请求类型" + value + "，对于" + c.getName() + "." + m.getName());
				}
			} catch (Exception ex) {
				// 这里异常说明没有指定的Annotation，如org.springframework.web.bind.annotation.RequestMapping
				// 因此，直接忽略这种情况即可
			}
		}
		return values;
	}
	
	/**
	 * @author wuheng@iscas.ac.cn
	 * @since  0.6
	 *
	 */
	public static class Label {
		
		/**
		 * eg., org.springframework.web.bind.annotation.RequestMapping
		 */
		protected Class<? extends Annotation> requestAnnotation;
		
		/**
		 * eg., method
		 */
		protected String requestTypeFunction;
		
		/**
		 * eg., RequestMethod.POST
		 */
		protected Object requestTypeValue;


		public Label(Class<? extends Annotation> requestAnnotation, String requestTypeFunction,
				Object requestTypeValue) {
			super();
			this.requestAnnotation = requestAnnotation;
			this.requestTypeFunction = requestTypeFunction;
			this.requestTypeValue = requestTypeValue;
		}

		
		public Class<? extends Annotation> getRequestAnnotation() {
			return requestAnnotation;
		}


		public void setRequestAnnotation(Class<? extends Annotation> requestAnnotation) {
			this.requestAnnotation = requestAnnotation;
		}

		public String getRequestTypeFunction() {
			return requestTypeFunction;
		}

		public void setRequestTypeFunction(String requestTypeFunction) {
			this.requestTypeFunction = requestTypeFunction;
		}

		public Object getRequestTypeValue() {
			return requestTypeValue;
		}

		public void setRequestTypeValue(Object requestTypeValue) {
			this.requestTypeValue = requestTypeValue;
		}
	
	}
	
	/**
	 * @author wuheng@iscas.ac.cn
	 * @since  0.6
	 *
	 */
	public static class MethodAndType {
		
		/**
		 * Get/Post/Put/Delete
		 */
		protected final String type;
		
		/**
		 * 方法名
		 */
		protected final Method method;

		public MethodAndType(String type, Method method) {
			super();
			this.type = type;
			this.method = method;
		}

		public String getType() {
			return type;
		}

		public Method getMethod() {
			return method;
		}

		@Override
		public String toString() {
			return type + ":" + method;
		}

	}
}
