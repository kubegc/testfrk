/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.newhero.utils.FileUtil;
import io.github.newhero.utils.JavaUtil;
import io.github.newhero.utils.ValueUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Generator {

	protected final Analyzer analyzer;

	protected final Class<?> bootstrap;

	protected final String pkgName;

	public static final String BODY = ".content(#NAME#_DATA)";

	public static final String PATH = "\tpublic final static String #METHON#_PATH = \"#URL#\";\n\n";

	public Generator(Analyzer analyzer, Class<?> bootstrap, String pkgName) {
		super();
		this.analyzer = analyzer;
		this.bootstrap = bootstrap;
		this.pkgName = pkgName;
	}

	public void start() throws Exception {

		for (String fullname : analyzer.getNameToClass().keySet()) {
			Class<?> clz = analyzer.getNameToClass().get(fullname);
			StringBuilder sb = new StringBuilder();
			sb.append(FileUtil.read("templates/classtmp").replace("#TESTCASE_PACKAGE#", pkgName)
					.replace("#BOOTSTRAP#", bootstrap.getName()).replace("#SOURCE_PACKAGE#", analyzer.pkgName)
					.replace("#CLASSNAME#", clz.getSimpleName() + "Test"));

			for (String url : analyzer.getNameToUrlGroup().get(clz.getName())) {

				sb.append(PATH.replace("#METHON#", analyzer.getUrlToMethod().get(url).getName().toUpperCase())
						.replace("#URL#", url));

				List<String> keys = getKeys(analyzer.getUrlToJsonData().get(url));
				for (int i = 0; i < keys.size(); i++) {
					String tpl = getTplWithFalse(url, keys, i);
					sb.append(tpl).append("\n\n");
				}

				sb.append(getTplWithTrue(url, keys)).append("\n\n");
			}

			sb.append("\n}\n\n");

			System.out.println(sb.toString());
			FileUtil.write(pkgName, clz.getSimpleName() + "Test", sb.toString());
		}
	}

	private String getTplWithTrue(String url, List<String> keys) throws Exception {
		String type = analyzer.getUrlToReqType().get(url) == null ? "GET" : analyzer.getUrlToReqType().get(url);

		String tpl = FileUtil.read("templates/" + type.toLowerCase() + "tmp");

		ObjectNode data = analyzer.getUrlToJsonData().get(url).deepCopy();

		fillData(url, keys, -1, data);

		tpl = (data == null) ? tpl.replace(BODY, "")
				: tpl.replace("#DATA#", data.toPrettyString().replaceAll("\"", "\\\\\"").replaceAll("[\\t\\n\\r]", ""));

		String name = analyzer.getUrlToMethod().get(url).getName().toUpperCase() + "_VALID_";
		return tpl.replaceAll("#NAME#", name).replace("#VALUE#", "true").replace("#METHON#",
				analyzer.getUrlToMethod().get(url).getName().toUpperCase());
	}

	private String getTplWithFalse(String url, List<String> keys, int i) throws Exception {
		String type = analyzer.getUrlToReqType().get(url) == null ? "GET" : analyzer.getUrlToReqType().get(url);

		String tpl = FileUtil.read("templates/" + type.toLowerCase() + "tmp");

		ObjectNode data = analyzer.getUrlToJsonData().get(url).deepCopy();

		fillData(url, keys, i, data);

		tpl = (data == null) ? tpl.replace(BODY, "")
				: tpl.replace("#DATA#", data.toPrettyString().replaceAll("\"", "\\\\\"").replaceAll("[\\t\\n\\r]", ""));

		String name = analyzer.getUrlToMethod().get(url).getName().toUpperCase() + "_INVALID_"
				+ keys.get(i).toUpperCase() + "_";
		return tpl.replaceAll("#NAME#", name).replace("#VALUE#", "false").replace("#METHON#",
				analyzer.getUrlToMethod().get(url).getName().toUpperCase());
	}

	private void fillData(String url, List<String> keys, int i, ObjectNode data) throws Exception {

		if (data == null) {
			return;
		}

		Method m = analyzer.getUrlToMethod().get(url);
		for (Parameter p : m.getParameters()) {
			Object val = (i == -1) ? ValueUtil.getTrueValue(p) :
					(keys.get(i).equals(p.getName()) ? ValueUtil.getFalseValue(p) 
							: ValueUtil.getTrueValue(p));
			
			if (val == null) {
				data.remove(p.getName());
				continue;
			}
			
			String asType  = data.get(p.getName()).asText();
			if (asType.equals(String.class.getName())) {
				data.put(p.getName(), (String) val);
			} else if (asType.equals(Integer.class.getName()) || asType.equals("int")) {
				data.put(p.getName(), (Integer) val);
			} else if (asType.equals(Boolean.class.getName()) || asType.equals("boolean")) {
				data.put(p.getName(), (Boolean) val);
			} else if (!JavaUtil.isPrimitive(asType)) {
				data.set(asType, (JsonNode) val);
			} else {
				throw new Exception("Unsupport Java type");
			}
		}
	}

	List<String> getKeys(ObjectNode data) {
		List<String> list = new ArrayList<>();
		if (data != null) {
			Iterator<String> iter = data.fieldNames();
			while (iter.hasNext()) {
				list.add(iter.next());
			}
		}
		return list;
	}
}