/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero.utils;

import java.io.File;
import java.io.FileWriter;

/**
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 * 
 */

public class FileUtil {

	public static void write(String pkg, String name, String content) throws Exception {
		String path = "src/test/java/" + pkg.replaceAll("\\.", "/");
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileWriter java = new FileWriter(new File(dir, name + ".java"));
		java.write(content);
		java.flush();
		java.close();
	}
}
