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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
import io.github.testfrk.utils.ValueUtil;

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
					list.add(ValueUtil.getStringValue(len.max() - 1));
					list.add(ValueUtil.getStringValue(len.max() + 1));
					list.add(ValueUtil.getStringValue(len.min() - 1));
					return list;
				}
				
				Pattern pa = (Pattern) va.get("javax.validation.constraints.Pattern");
				if (pa != null) {
					int max = len(pa.regexp(), ",", "}");
					int min = len(pa.regexp(), "{", ",");
					list.add(ValueUtil.getStringValue(max - 1));
					list.add(ValueUtil.getStringValue(max + 1));
					list.add(ValueUtil.getStringValue(min - 1));
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
	
	
	/*******************************************************
	 * 
	 * 
	 * 
	 ********************************************************/
	
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
	



}
