/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.github.testfrk.utils.FileUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Generator {

	protected final String pkgName;
	
	protected final Class<?> bootstrap;
	
	protected final Analyzer anlyzer;

	public Generator(String pkgName, Class<?> bootstrap, Analyzer anlyzer) {
		super();
		this.pkgName = pkgName;
		this.bootstrap = bootstrap;
		this.anlyzer = anlyzer;
	}

	public void generate() throws Exception {
		for (String name : RuleBase.nameToUrls.keySet()) {
			Class<?> clz = Class.forName(name);
			StringBuilder sb = new StringBuilder();
			sb.append(FileUtil.read("templates/classtmp").replace("#TESTCASE_PACKAGE#", pkgName)
					.replace("#BOOTSTRAP#", bootstrap.getName()).replace("#SOURCE_PACKAGE#", anlyzer.getPkgName())
					.replace("#CLASSNAME#", clz.getSimpleName() + "Test"));
			
			for (String url : RuleBase.nameToUrls.get(name)) {
				
				ArrayNode dataList = (ArrayNode) this.anlyzer.analyse().get(url);
				
				if (dataList.size() < 2) {
					continue;
				}
				
				sb.append("\n\tpublic static final String " + RuleBase.urlToMethod.get(url).getName() + "_PATH = \"" + url + "\";\n");
				int i = 0;
				for (JsonNode tc : dataList) {
					String servName = tc.fieldNames().next();
					String servData = tc.get(servName).toString().replaceAll("\"", "\\\\\"");
					System.out.println(servName);
					System.out.println(tc.get(servName).toPrettyString());
					sb.append("\n").append(FileUtil.read("templates/posttmp")
									.replaceAll("#NAME#", servName)
									.replaceAll("#METHOD#", RuleBase.urlToMethod.get(url).getName())
									.replace("#DATA#", servData)
									.replace("#VALUE#", (i == 0) ? "true" : "false"));
					++i;
				}
				
			}
			
			
			sb.append("}");
			FileUtil.write(pkgName, clz.getSimpleName() + "Test", sb.toString());
		}
	}
	
}
