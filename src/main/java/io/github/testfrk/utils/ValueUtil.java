/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kubesys.httpfrk.utils.JavaUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
@SuppressWarnings("deprecation")
public class ValueUtil {

	
	protected static String[]   cstas = new String[]{
								"org.hibernate.validator.constraints", 
								"javax.validation.constraints"};
	
	protected static Properties props = new Properties();
	
	static {
		File file = new File("config/defvalue.conf");
		if (file.exists()) {
			try {
				props.load(new FileInputStream(file));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayNode getPrimitiveValues(String cls, Parameter p) {
		return getPrimitiveValues(cls, p.getType().getName(), p.getName(), p.getAnnotations());
	}
	
	public ArrayNode getPrimitiveValues(String cls, Field f, String tag) throws Exception {
		return getPrimitiveValues(cls, f.getType().getName(), f.getName(), usedAnnotations(f.getAnnotations(), tag));
	}
	
	public ArrayNode getPrimitiveValues(String cls, String type, String name, Annotation[] as) {
		ArrayNode list = new ObjectMapper().createArrayNode();
		if (type.equals(String.class.getName())) {
			Map<String, Annotation> va = valuesAnnotations(as);
			if(va.size() == 0) {
				addValue(list, cls + ".true." + name);
				addValue(list, cls + ".false." + name);
			} else {
				Length len = (Length) va.get("org.hibernate.validator.constraints.Length");
				if (len != null) {
					list.add(getStringValue(len.max() - 1));
					list.add(getStringValue(len.max() + 1));
					list.add(getStringValue(len.min() - 1));
					return list;
				}
				
				Pattern pa = (Pattern) va.get("javax.validation.constraints.Pattern");
				if (pa != null) {
					int max = len(pa.regexp(), ",", "}");
					int min = len(pa.regexp(), "{", ",");
					list.add(getStringValue(max - 1));
					list.add(getStringValue(max + 1));
					list.add(getStringValue(min - 1));
					return list;
				}
				
				Email em = (Email) va.get("org.hibernate.validator.constraints.Email");
				if (em != null) {
					list.add("test@123.com");
					list.add("test@123");
					return list;
				}
				
				NotBlank nb = (NotBlank) va.get("org.hibernate.validator.constraints.NotBlank");
				if (nb != null) {
					list.add("T^&S)DS");
					list.addNull();
					return list;
				}
				
				Null nu = (Null) va.get("javax.validation.constraints.Null");
				if (nu != null) {
					list.addNull();
					list.add("asdd");
					return list;
				}
				
				addValue(list, cls + ".true." + name);
				addValue(list, cls + ".false." + name);
			}
		} else if (type.equals("java.lang.Integer")) {
			Map<String, Annotation> va = valuesAnnotations(as);
			if(va.size() == 0) {
				addValue(list, cls + ".true." + name);
				addValue(list, cls + ".false." + name);
			} else {
				Min minDesc = (Min) va.get("javax.validation.constraints.Min");
				Min maxDesc = (Min) va.get("javax.validation.constraints.Max");
				int min = (int) ((minDesc == null) ? Integer.MIN_VALUE : minDesc.value());
				int max = (int) ((maxDesc == null) ? Integer.MAX_VALUE : maxDesc.value());
				list.add(max - 1);
				list.add(max == Integer.MAX_VALUE ? max : max + 1);
				list.add(min == Integer.MIN_VALUE ? min : min - 1);
			}
		}
		return list;
	}
	
	public static int len(String str, String prefix, String postfix) {
		int stx = str.lastIndexOf(prefix);
		int edx = str.lastIndexOf(postfix);
		return Integer.parseInt(str.substring(stx + 1, edx).trim());
	}
	
	public static void addValue(ArrayNode list, String key) {
		String val = props.getProperty(key);
		if (val != null) {
			list.add(val);
		} else {
			System.out.println("config " + key + " in conf/defvalue.conf");
		}
	}
	
	public ObjectNode getObjectValues(String clsName, Class<?>[] tags) throws Exception {
		
		String tag = tags.length == 0 ? null : tags[0].getName();
		
		ObjectNode node = new ObjectMapper().createObjectNode();
		Class<?> cls = Class.forName(clsName);
		for (Field f : cls.getDeclaredFields()) {
			if (JavaUtil.isPrimitive(f.getGenericType().getTypeName())
					|| JavaUtil.isStringList(f.getGenericType().getTypeName())) {
				if (validParameter(f.getAnnotations(), tag)) {
					node.set(f.getName(), getPrimitiveValues(cls.getSimpleName(), f, tag));
				}
			} 
			// 这些情况暂时不处理
			else if (JavaUtil.isList(f.getGenericType().getTypeName()) 
					|| JavaUtil.isSet(f.getGenericType().getTypeName())
					|| JavaUtil.isMap(f.getGenericType().getTypeName())) {
				System.out.println("ignore " + f.getGenericType().getTypeName() + " " + f.getName());
//				throw new RuntimeException("Unsupport parameters types: list, Set and Map");
			} 
			// 如果是Java对象，必须包含RequestBody标签
			else {
				node.set(f.getName(), getObjectValues(getClassName(
						f.getGenericType().getTypeName()), tags));
			}
		}
		return node;
	}
	
	public static Annotation[] usedAnnotations(Annotation[] as, String tag) throws Exception {
		
		List<Annotation> list = new ArrayList<>();
		for (Annotation a: valuesAnnotations(as).values()) {
			Method m = a.annotationType().getDeclaredMethod("groups");
			Class<?>[] vs =  (Class<?>[]) m.invoke(a);
			
			// from config case
			if (as.length == 0 && tag == null) {
				list.add(a);
			}
			
			for (Class<?> v : vs) {
				if (v.getTypeName().equals(tag)) {
					list.add(a);
				}
			}
		}
		return list.toArray(new Annotation[] {});
		
	}
	
	public static boolean validParameter(Annotation[] as, String tag) throws Exception {
	
		for (Annotation a : valuesAnnotations(as).values()) {
			Method m = a.annotationType().getDeclaredMethod("groups");
			Class<?>[] vs =  (Class<?>[]) m.invoke(a);
			
			// from config case
			if (vs.length == 0 && tag == null) {
				return true;
			}
			
			for (Class<?> v : vs) {
				if (v.getTypeName().equals(tag)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getClassName(String typeName) {
		int stx = typeName.indexOf("<");
		if (stx == -1) {
			return typeName;
		}
		int etx = typeName.indexOf(">");
		return typeName.substring(stx + 1, etx);
	}
	
	
	public static Map<String, Annotation> valuesAnnotations(Annotation[] as) {
		Map<String, Annotation> list = new HashMap<>();
		if (as == null) {
			return list;
		}
		
		for (Annotation a: as) {
			for (String c : cstas) {
				if (a.annotationType().getTypeName().contains(c)) {
					list.put(a.annotationType().getTypeName(), a);
				}
			}
		}
		return list;
	}
	
	public static String getStringValue(int len) {
		if (len <= 0) {
			return null;
		}
		
		char[] chArr = new char[len];
		for (int i = 0; i < len; i++) {
			chArr[i] = codes[random.nextInt(len)%codes.length];
		}
		return new String(chArr);
	}
	
	public static void print(Annotation[] as) {
		for (Annotation a : as) {
			System.out.println(a);
		}
	}
	
	public static void print(Collection<Annotation> as) {
		for (Annotation a : as) {
			System.out.println(a);
		}
	}
	
	/*******************************************************
	 * 
	 * 
	 * 
	 ********************************************************/
	
	static char[] codes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z' };

	static Random random = new Random();

	static boolean contain(Class<?>[] clzg, String tag) {
		if (tag == null) {
			return true;
		}
		
		if (clzg == null && tag != null) {
			return false;
		}
		
		for (Class<?> c : clzg) {
			if (c.getName().equals(tag)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Object getTrueValue(String classname, Field f, Class<?>[] values) {
		
		return getValue(classname, f, values, "true");
	}
	
	public static Object getFalseValue(String classname, Field f, Class<?>[] values) {

		return getValue(classname, f, values, "false");
	}

	private static Object getValue(String classname, Field f, Class<?>[] values, String type) {
		String tag = (values != null) ? values[0].getName() : null;
		String key = classname + "." + type + "." + f.getName();
		
		Max max = f.getAnnotation(Max.class);
		if (max != null) {
			return type.equals("true") ? getMaxTrueValue(classname, f.getName(), max)
					: getMaxFalseValue(classname, f.getName(), max);
		}
		
		Min min = f.getAnnotation(Min.class);
		if (min != null) {
			return type.equals("true") ? getMinTrueValue(classname, f.getName(), min)
					: getMinFalseValue(classname, f.getName(), min);
		}
		
		Null _null = f.getAnnotation(Null.class);
		if (_null != null && contain(_null.groups(), tag)) {
			return null;
		}
		
		NotNull _notNull = f.getAnnotation(NotNull.class);
		if (_notNull != null && contain(_notNull.groups(), tag)) {
			return props.get(key);
		}
		
		
		Pattern exp = f.getAnnotation(Pattern.class);
		if (exp != null) {
//			return type.equals("true") ? getStringTrueValue(classname, f.getName(), exp)
//					: getStringFalseValue(classname, f.getName(), exp);
			return props.get(key);
		}

		return null;
	}


	public static String getStringTrueValue(String classname, String name, Pattern exp) {
		
		String v = exp.regexp();
		int idx = v.lastIndexOf(",");
		int len = Integer.parseInt(v.substring(idx + 1, v.length() - 1)) - 1;
		char[] chArr = new char[len];
		for (int i = 0; i < len; i++) {
			chArr[i] = codes[random.nextInt(len)];
		}
		return new String(chArr);

	}

	public static String getStringFalseValue(String classname, String name, Pattern exp) {
		String v = exp.regexp();
		int idx = v.lastIndexOf(",");
		int len = Integer.parseInt(v.substring(idx + 1, v.length() - 1)) + 1;
		char[] chArr = new char[len];
		for (int i = 0; i < len; i++) {
			chArr[i] = codes[random.nextInt(len)];
		}
		return new String(chArr);
	}

	public static int getMaxTrueValue(String classname, String name, Max exp) {
		return (int) (exp.value() - 1);
	}

	public static int getMaxFalseValue(String classname, String name, Max exp) {
		return (int) (exp.value() + 1);
	}
	
	public static int getMinTrueValue(String classname, String name, Min exp) {
		return (int) (exp.value() + 1);
	}

	public static int getMinFalseValue(String classname, String name, Min exp) {
		return (int) (exp.value() - 1);
	}
}
