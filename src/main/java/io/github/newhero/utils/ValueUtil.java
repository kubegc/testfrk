/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero.utils;

import java.lang.reflect.Parameter;
import java.util.Random;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class ValueUtil {

	static char[] codes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z' };

	static Random random = new Random();

	public static Object getTrueValue(Parameter p) {
		
		Pattern exp = p.getAnnotation(Pattern.class);
		if (exp != null) {
			return getStringTrueValue(exp);
		}

		Max max = p.getAnnotation(Max.class);
		if (max != null) {
			return getIntTrueValue(max);
		}

		return null;
	}

	public static Object getFalseValue(Parameter p) {

		Pattern exp = p.getAnnotation(Pattern.class);
		if (exp != null) {
			return getStringFalseValue(exp);
		}

		Max max = p.getAnnotation(Max.class);
		if (max != null) {
			return getIntFalseValue(max);
		}

		return null;
	}

	public static String getStringTrueValue(Pattern exp) {
		String v = exp.regexp();
		int idx = v.lastIndexOf(",");
		int len = Integer.parseInt(v.substring(idx + 1, v.length() - 1)) - 1;
		char[] chArr = new char[len];
		for (int i = 0; i < len; i++) {
			chArr[i] = codes[random.nextInt(len)];
		}
		return new String(chArr);

	}

	public static String getStringFalseValue(Pattern exp) {
		String v = exp.regexp();
		int idx = v.lastIndexOf(",");
		int len = Integer.parseInt(v.substring(idx + 1, v.length() - 1)) + 1;
		char[] chArr = new char[len];
		for (int i = 0; i < len; i++) {
			chArr[i] = codes[random.nextInt(len)];
		}
		return new String(chArr);
	}

	public static int getIntTrueValue(Max exp) {
		return (int) (exp.value() - 1);
	}

	public static int getIntFalseValue(Max exp) {
		return (int) (exp.value() + 1);
	}
}
