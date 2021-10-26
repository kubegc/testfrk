/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Random;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class ValueUtil {

	
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
