/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.v1;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.testfrk.utils.FileUtil;
import io.github.testfrk.utils.JavaUtil;
import io.github.testfrk.utils.ValueUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Generator {

	
	public static final Set<String> nullController = new HashSet<>();
	
	protected final Analyzer analyzer;

	protected final Class<?> bootstrap;

	protected final String pkgName;

	public static final String BODY = ".content(#NAME#_DATA)";

	public static final String PATH = "\tpublic final static String #METHON#_PATH = \"#URL#\";\n\n";
	
	public Generator(Analyzer analyzer, Class<?> bootstrap, String pkgName) throws Exception {
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
			try {
				Map<String, List<String>> nameToUrlGroup = analyzer.getNameToUrlGroup();
				for (String url : nameToUrlGroup.get(clz.getName())) {
					System.out.println(url);
	
					try {
						List<String> keys = getKeys(analyzer.getUrlToJsonData().get(url));
						String tplWithTrue = getTplWithTrue(url, keys);
						sb.append(PATH.replace("#METHON#", analyzer.getUrlToMethod().get(url).getName())
								.replace("#URL#", url));
		
						
						for (int i = 0; i < keys.size(); i++) {
							String tpl = getTplWithFalse(url, keys, i);
							if ("".equals(tpl)) {
								continue;
							}
							sb.append(tpl).append("\n\n");
						}
						
						sb.append(tplWithTrue).append("\n\n");
					} catch (Exception ex) {
						System.err.println("ignore url.");
					}
				}
	
				sb.append("\n}\n\n");
	
				System.out.println(sb.toString());
				FileUtil.write(pkgName, clz.getSimpleName() + "Test", sb.toString());
			} catch (Exception ex) {
				System.err.println(fullname + " is not used");
				nullController.add(fullname);
			}
		}
	}

	private String getTplWithTrue(String url, List<String> keys) throws Exception {
		String type = analyzer.getUrlToReqType().get(url) == null ? "GET" : analyzer.getUrlToReqType().get(url);

		String tpl = FileUtil.read("templates/" + type.toLowerCase() + "tmp");

		ObjectNode data = analyzer.getUrlToJsonData().get(url).deepCopy();

		fillData(url, keys, -1, data);

		tpl = (data == null) ? tpl.replace(BODY, "")
				: tpl.replace("#DATA#", data.toPrettyString().replaceAll("\"", "\\\\\"").replaceAll("[\\t\\n\\r]", ""));

		String name = analyzer.getUrlToMethod().get(url).getName() + "_VALID_";
		return tpl.replaceAll("#NAME#", name).replace("#VALUE#", "true").replace("#METHON#",
				analyzer.getUrlToMethod().get(url).getName());
	}

	private String getTplWithFalse(String url, List<String> keys, int i) throws Exception {
		String type = analyzer.getUrlToReqType().get(url) == null ? "GET" : analyzer.getUrlToReqType().get(url);

		String tpl = FileUtil.read("templates/" + type.toLowerCase() + "tmp");

		ObjectNode data = analyzer.getUrlToJsonData().get(url).deepCopy();

		fillData(url, keys, i, data);

		tpl = (data == null) ? tpl.replace(BODY, "")
				: tpl.replace("#DATA#", data.toPrettyString().replaceAll("\"", "\\\\\"").replaceAll("[\\t\\n\\r]", ""));

		String name = analyzer.getUrlToMethod().get(url).getName() + "_INVALID_"
				+ keys.get(i) + "_";
		if (data.has(keys.get(i))) {
			return tpl.replaceAll("#NAME#", name).replace("#VALUE#", "false").replace("#METHON#",
				analyzer.getUrlToMethod().get(url).getName());
		} else {
			return "";
		}
	}

	private boolean fillData(String url, List<String> keys, int i, ObjectNode data) throws Exception {

		if (data == null) {
			return false;
		}

		boolean hasData = false;
		Method m = analyzer.getUrlToMethod().get(url);
		for (Parameter p : m.getParameters()) {
			Validated[] v = p.getDeclaredAnnotationsByType(Validated.class);
			
			try {
				System.out.println(v[0].value()[0]);
			} catch (Exception ex) {
				v = null;
			}
			
			String asType = getFullType(p);
			if (!JavaUtil.isPrimitive(asType)) {
				for (Field f : Class.forName(getType(p)).getDeclaredFields()) {
					Object value = getValue(keys, p.getType().getSimpleName(), f, i, v);
					String typename = f.getType().getName();
					if (typename.equals(Object.class.getName())) {
						set(data, f.getName(), null);
					} else if (typename.equals(String.class.getName())) {
						put(data, f.getName(), (String) value);
					} else if (typename.equals(Integer.class.getName()) 
							|| typename.equals("int")) {
						put(data, f.getName(), (Integer) value);
					} else if (typename.equals(Boolean.class.getName()) 
							|| typename.equals("boolean")) {
						put(data, f.getName(), (Boolean) value);
					} else {
						data.remove(f.getName());
						hasData = false;
					}
					
				}
			}
		}
		return hasData;
	}

	private String getFullType(Parameter p) {
		return p.getParameterizedType().getTypeName();
	}
	
	private String getType(Parameter p) {
		String asType  = p.getType().getName();
		int indexOf = asType.indexOf("<");
		return (indexOf != -1) ? asType.substring(0, indexOf) :  asType;
	}

	Object getValue(List<String> keys, String classname, Field f, int i, Validated[] v) {
		return (i == -1) ? ValueUtil.getTrueValue(classname, f, (v == null) ? null : v[0].value()) :
		(keys.get(i).equals(f.getName()) ? ValueUtil.getFalseValue(classname, f, (v == null) ? null : v[0].value()) 
				: ValueUtil.getTrueValue(classname, f, (v == null) ? null : v[0].value()));
	}
	
	void put(ObjectNode data, String key, String val) {
		if (val != null) {
			data.put(key, val);
		} else {
			data.remove(key);
		}
	}
	
	void put(ObjectNode data, String key, Integer val) {
		if (val != null) {
			data.put(key, val);
		} else {
			data.remove(key);
		}
	}
	
	void put(ObjectNode data, String key, Boolean val) {
		if (val != null) {
			data.put(key, val);
		} else {
			data.remove(key);
		}
	}
	
	void set(ObjectNode data, String key, Object val) throws Exception {
		if (val == null) {
			data.set(key, new ObjectMapper().createObjectNode());
		} else {
			data.set(key, new ObjectMapper().readTree(
					new ObjectMapper().writeValueAsString(val)));
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
