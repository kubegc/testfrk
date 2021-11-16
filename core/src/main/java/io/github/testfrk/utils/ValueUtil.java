/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.util.Random;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * @author wuheng@iscas.ac.cn
 * @since  0.4
 * 
 * it is used for check Java object
 */

public class ValueUtil {


	/***********************************************************************
	 * 
	 *                 String
	 *
	 ***********************************************************************/
	static char[] codes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z' };

	static Random random = new Random();
	
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
	
	/***********************************************************************
	 * 
	 *                 Integer
	 *
	 ***********************************************************************/
	
	public static int getMaxTrueValue(Max exp) {
		return (int) (exp.value() - 1);
	}
	
	public static int getMaxFalseValue(Max exp) {
		return (int) (exp.value() + 1);
	}
	
	public static int getMinTrueValue(Min exp) {
		return (int) (exp.value() + 1);
	}

	public static int getMinFalseValue(Min exp) {
		return (int) (exp.value() - 1);
	}
}
