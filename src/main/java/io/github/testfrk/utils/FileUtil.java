/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.3
 * 
 * It is used for read and write TestCase.
 */

public class FileUtil {

	/**
	 * @param pkg         package name
	 * @param name        testcase name
	 * @param content     testcase content
	 * @throws Exception  unable to write
	 */
	public static void write(String pkg, String name, String content) throws Exception {
		// make dir
		File dir = new File("src/test/java/" + pkg.replaceAll("\\.", "/"));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// write content
		FileWriter writer = new FileWriter(
				new File(dir, name + ".java"));
		writer.write(content);
		writer.close();
	}
	
	/**
	 * @param file        file path
	 * @return            file content
	 * @throws Exception  unable to read
	 */
	public static String read(String file) throws Exception {
		// init file content
		StringBuilder sb = new StringBuilder();
		// append content
		BufferedReader br = new BufferedReader(
				new FileReader(new File(file)));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		// return
		return sb.toString();
	}
}
