/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero.utils;

import io.github.newhero.utils.FileUtil;

/**
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 * 
 */

public class FileUtilTest {

	public static String FILE = "/**\r\n"
			+ " * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences\r\n"
			+ " */\r\n"
			+ "package com.github.webfrk;\r\n"
			+ "\r\n"
			+ "import java.io.File;\r\n"
			+ "import java.io.FileWriter;\r\n"
			+ "\r\n"
			+ "/**\r\n"
			+ " * @author wuheng@iscas.ac.cn\r\n"
			+ " * @since 2021.6.29\r\n"
			+ " * \r\n"
			+ " */\r\n"
			+ "\r\n"
			+ "public class TestDemo {\r\n"
			+ "\r\n"
			+ "	public static void write(String pkg, String name, String content) throws Exception {\r\n"
			+ "		String path = \"src/test/java/\" + pkg.replaceAll(\"\\\\.\", \"/\");\r\n"
			+ "		File dir = new File(path);\r\n"
			+ "		if (!dir.exists()) {\r\n"
			+ "			dir.mkdirs();\r\n"
			+ "		}\r\n"
			+ "		FileWriter java = new FileWriter(new File(dir, name + \".java\"));\r\n"
			+ "		java.write(content);\r\n"
			+ "		java.flush();\r\n"
			+ "		java.close();\r\n"
			+ "	}\r\n"
			+ "}\r\n"
			+ "";
	
	public static void main(String[] args) throws Exception {
		FileUtil.write("com.github.webfrk", "TestDemo", FILE);
	}
	
}
