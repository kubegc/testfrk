/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.values;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kubesys.httpfrk.utils.JavaUtil;

import io.github.testfrk.utils.AnnoUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
@SuppressWarnings("deprecation")
public class DefaultValueGenerator extends AbstractValueGenerator {

	protected static String[] jsr303 = new String[]{
								"org.hibernate.validator.constraints", 
								"javax.validation.constraints"};
	
	public ArrayNode getPrimitiveValues(String cls, Parameter p) throws Exception {
		return getPrimitiveValues(cls, p.getType().getName(), p.getName(), p.getAnnotations());
	}
	
	public ArrayNode getPrimitiveValues(String cls, Field f, String tag) throws Exception {
		return getPrimitiveValues(cls, f.getType().getName(), f.getName(), AnnoUtil.usedAnnotations(f.getAnnotations(), "groups", jsr303, tag));
	}
	
	public ArrayNode getPrimitiveValues(String cls, String type, String name, Annotation[] as) {
		ArrayNode list = new ObjectMapper().createArrayNode();
		if (type.equals(String.class.getName())) {
			Map<String, Annotation> va = AnnoUtil.valuesAnnotations(as, jsr303);
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
		} else if (type.equals("java.lang.Integer") || type.equals("int")) {
			Map<String, Annotation> va = AnnoUtil.valuesAnnotations(as, jsr303);
			if(va.size() == 0) {
				addValue(list, cls + ".true." + name);
				addValue(list, cls + ".false." + name);
			} else {
				Min minDesc = (Min) va.get("javax.validation.constraints.Min");
				Max maxDesc = (Max) va.get("javax.validation.constraints.Max");
				int min = (int) ((minDesc == null) ? Integer.MIN_VALUE : minDesc.value());
				int max = (int) ((maxDesc == null) ? Integer.MAX_VALUE : maxDesc.value());
				list.add(max - 1);
				list.add(max == Integer.MAX_VALUE ? max : max + 1);
				list.add(min == Integer.MIN_VALUE ? min : min - 1);
			}
		}
		return list;
	}
	
	@Override
	public String checkAndGetKey(String url, Method m, int i) throws Exception {
		Parameter p = m.getParameters()[i];
		
		// 每个参数都必须带Validated
		Validated v = p.getAnnotation(Validated.class);
		AnnoUtil.assertNotNull(v, Validated.class, url, p);
		
		// 每个参数都必须带RequestParam
		RequestParam rp = p.getAnnotation(RequestParam.class);
		AnnoUtil.assertNotNull(rp, RequestParam.class, url, p);
		
		return (rp.value() == null || rp.value().length() == 0) 
									? p.getName() : rp.value(); 
	}

	@Override
	public Class<?>[] checkAndGetValue(String url, Method m, int i) throws Exception {
		
		Parameter p = m.getParameters()[i];
		
		// 每个参数都必须带Validated
		Validated v = p.getAnnotation(Validated.class);
		AnnoUtil.assertNotNull(v, Validated.class, url, p);
		
		// 每个参数都必须带RequestBody
		RequestBody rb = p.getAnnotation(RequestBody.class);
		AnnoUtil.assertNotNull(rb, RequestBody.class, url, p);
		
		return v.value();
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
				if (AnnoUtil.validParameter(f.getAnnotations(), "groups", jsr303, tag)) {
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
	
	public static String getClassName(String typeName) {
		int stx = typeName.indexOf("<");
		if (stx == -1) {
			return typeName;
		}
		int etx = typeName.indexOf(">");
		return typeName.substring(stx + 1, etx);
	}
	
	
	public static String getStringValue(int len) {
		if (len <= 0) {
			return null;
		}
		
		try {
			char[] chArr = new char[len];
			for (int i = 0; i < len; i++) {
				chArr[i] = codes[random.nextInt(len)%codes.length];
			}
			return new String(chArr);
		} catch (java.lang.OutOfMemoryError ex) {
			return null;
		}
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
