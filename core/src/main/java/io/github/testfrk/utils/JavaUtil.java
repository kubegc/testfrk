/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wuheng@iscas.ac.cn
 * @since  0.1
 * 
 * it is used for check Java object
 */

public class JavaUtil {


	/*********************************************************************
	 *
	 * 
	 * Java type checker
	 * 
	 * 
	 *********************************************************************/

	/**
	 * primitive type in Java
	 */
	protected final static Set<String> primitive = new HashSet<String>();

	static {
		primitive.add(String.class.getName());
		primitive.add(Boolean.class.getName());
		primitive.add(Integer.class.getName());
		primitive.add(Long.class.getName());
		primitive.add(Double.class.getName());
		primitive.add(Float.class.getName());
		primitive.add(Byte.class.getName());
		primitive.add("boolean");
		primitive.add("int");
		primitive.add("long");
		primitive.add("double");
		primitive.add("float");
		primitive.add("byte");
	}

	/**
	 * @param typename       typename
	 * @return return true if the classname is primitive, otherwise return false
	 */
	public static boolean isPrimitive(String typename) {
		return isNull(typename) ? false : primitive.contains(typename);
	}

	/**
	 * @param clazz           class
	 * @return return true if the typename is primitive, otherwise return false
	 */
	public static boolean isPrimitive(Class<?> clazz) {
		return isNull(clazz) ? false : isPrimitive(clazz.getName());
	}

	/**
	 * @param typename        typename
	 * @return return         true if the typename is starts with java.util.Map, otherwise
	 *         return false
	 */
	public static boolean isMap(String typename) {
		return isNull(typename) ? false : typename.startsWith(Map.class.getName());
	}

	/**
	 * 
	 */
	protected final static Map<String, String> map = null;

	/**
	 * @param typename          typename
	 * @return true if the typename is java.util.Map with (String, String) style,
	 *         otherwise return false
	 */
	public static boolean isStringStringMap(String typename) {
		try {
			return isNull(typename) ? false
					: typename.equals(JavaUtil.class.getDeclaredField("map").getGenericType().getTypeName());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param clazz class        class
	 * @return return true if the typename is starts with java.util.Map, otherwise
	 *         return false
	 */
	public static boolean isMap(Class<?> clazz) {
		return isNull(clazz) ? false : isMap(clazz.getName());
	}

	/**
	 * @param typename typename
	 * @return return true if the typename is Map, but not java.util.Map with
	 *         (String, String) style, otherwise return false
	 */
	public static boolean isStringObjectMap(String typename) {
		return isMap(typename) && !isStringStringMap(typename);
	}

	/**
	 * 
	 */
	protected final static List<String> list = null;

	/**
	 * @param typename         typename
	 * @return return true if the typename is java.util.List with String style,
	 *         otherwise return false
	 */
	public static boolean isStringList(String typename) {
		try {
			return isNull(typename) ? false
					: typename.equals(JavaUtil.class.getDeclaredField("list").getGenericType().getTypeName());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param typename          typename
	 * @return return true if the typename is starts with java.util.List, otherwise
	 *         return false
	 */
	public static boolean isList(String typename) {
		return isNull(typename) ? false : typename.startsWith(List.class.getName());
	}

	/**
	 * @param typename          typename
	 * @return return true if the typename is starts with java.util.List, but not
	 *         java.util.List with String style, otherwise return false
	 */
	public static boolean isObjectList(String typename) {
		return isList(typename) && !isStringList(typename);
	}

	/**
	 * 
	 */
	protected final static Set<String> set = null;

	/**
	 * @param                typename 
	 * @return return true if the typename is starts with java.util.Set, otherwise return false
	 */
	public static boolean isSet(String typename) {
		return isNull(typename) ? false : typename.startsWith(Set.class.getName());
	}
	
	/**
	 * @param typename        typename
	 * @return return true if the typename is starts with java.util.Set with String style, otherwise return false
	 */
	public static boolean isStringSet(String typename) {
		try {
			return isNull(typename) ? false
					: typename.equals(JavaUtil.class.getDeclaredField("set").getGenericType().getTypeName());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param typename        typename
	 * @return return true if the typename is starts with java.util.Set, but not
	 *         java.util.Set with String style, otherwise return false
	 */
	public static boolean isObjectSet(String typename) {
		return isSet(typename) && !isStringSet(typename);
	}

	/**
	 * @param typename              typename
	 * @return 
	 */
	public static String getClassNameForMapStyle(String typename) {
		if (!isMap(typename)) {
			throw new InvalidParameterException("typename shoule be Map");
		}
		int start = typename.indexOf(",");
		int end = typename.indexOf(">");
		return (start == -1) ? typename : typename.substring(start + 1, end).trim(); // <String, Object>
	}

	@Deprecated
	public static String getClassNameForListOrSetStyle(String typename) {
		if (!isList(typename) && !isSet(typename)) {
			throw new InvalidParameterException("typename shoule be Map");
		}
		int start = typename.indexOf("<");
		int end = typename.indexOf(">");
		return (start == -1) ? typename : typename.substring(start + 1, end).trim();
	}

	/*********************************************************************
	 * 
	 *           Get real Java Class                
	 * 
	 *********************************************************************/

	private static final Map<String, String> typeMapping = new HashMap<String, String>();

	static {
		typeMapping.put(String.class.getName(), String.class.getName());
		typeMapping.put(Integer.class.getName(), Integer.class.getName());
		typeMapping.put(Float.class.getName(), Float.class.getName());
		typeMapping.put(Double.class.getName(), Double.class.getName());
		typeMapping.put("String", String.class.getName());
		typeMapping.put("int", Integer.class.getName());
		typeMapping.put("float", Float.class.getName());
		typeMapping.put("double", Double.class.getName());
	}

	/**
	 * @param name          name
	 * @return java class
	 * @throws Exception unable to found class
	 */
	public static Class<?> getJavaType(String name) throws Exception {
		String type = typeMapping.get(name);
		return (type == null) ? Class.forName(name) : Class.forName(type);
	}
	
	/*********************************************************************
	 * 
	 *       Util
	 * 
	 *********************************************************************/

	/**
	 * Check whether a string is null
	 * 
	 * @param str string
	 * @return return true of the string is null, otherwise return false
	 */
	public static boolean isNull(String str) {
		return (str == null || "".equals(str)) ? true : false;
	}

	/**
	 * 
	 * @param obj        object
	 * @return true if object is null
	 */
	public static boolean isNull(Object obj) {
		return (obj == null) ? true : false;
	}
	
	/**
	 * @param typeName    typename
	 * @return true if it dose not contains '<'
	 */
	public static boolean isSimpleObjectType(String typeName) {
		return !typeName.contains("<");
	}
	
	/**
	 * @param typeName   typename
	 * @return true if it contains '<'
	 */
	public static boolean isGenericObjectType(String typeName) {
		return typeName.contains("<");
	}
}
