/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

import io.github.newhero.utils.FileUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Generator {

	
	protected final Analyzer analyzer;
	
	protected final Class<?> bootstrap;
	
	protected final String testcaseDir;

	public Generator(Analyzer analyzer, Class<?> bootstrap, String testcaseDir) {
		super();
		this.analyzer = analyzer;
		this.bootstrap = bootstrap;
		this.testcaseDir = testcaseDir;
	}

	public void start() throws Exception {
		
		for (String fullname : analyzer.getNameToClass().keySet()) {
			Class<?> clz = analyzer.getNameToClass().get(fullname);
			StringBuilder sb = new StringBuilder();
			sb.append(FileUtil.read("templates/classtmp")
					.replace("#TESTCASE_PACKAGE#", testcaseDir)
					.replace("#BOOTSTRAP#", bootstrap.getName())
					.replace("#SOURCE_PACKAGE#", analyzer.pkgName)
					.replace("#CLASSNAME#", clz.getSimpleName() + "Test"));
			sb.append("\n}\n\n");
		}
	}
}
