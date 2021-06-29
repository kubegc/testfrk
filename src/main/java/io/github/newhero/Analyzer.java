/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.newhero.utils.ClassUtil;
import io.github.newhero.utils.JavaUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Analyzer {

    protected Map<String, List<String>> classToUrl = new HashMap<>();

    protected Map<String, String> urlToMethod = new HashMap<>();

    protected Map<String, ObjectNode> urlToJson = new HashMap<>();

    protected final String pkgName;
    
	public Analyzer(String pkgName) {
		super();
		this.pkgName = pkgName;
	}

	public void start() throws Exception {
		doStart(RequestMapping.class);
	}
	
	public void start(Class<? extends Annotation> classAnnotation) throws Exception {
		doStart(classAnnotation);
	}
	
	@SuppressWarnings("unchecked")
	void doStart(Class<? extends Annotation> classAnnotation) throws Exception {
		// find all Controller
        for (Class<?> clz : ClassUtil.scan(pkgName, classAnnotation)) {
            
        	RequestMapping crm = clz.getAnnotation(RequestMapping.class);
            String url = crm.value()[0];

            // find all http services
            for (Method m : clz.getDeclaredMethods()) {
                RequestMapping mrm = m.getAnnotation(RequestMapping.class);
                if (mrm == null) {
                    continue;
                }

                addUrlToClass(clz.getSimpleName(), url + mrm.value()[0]);
                urlToMethod.put(url + mrm.value()[0], m.getName()) ;

                // find all parameters without HttpServletResponse and HttpServletRequest
                if (m.getParameterCount() != 0) {
                    ObjectNode paramNode = new ObjectMapper().createObjectNode();
                    for (Parameter p : m.getParameters()) {
                        if (p.getParameterizedType().getTypeName().startsWith("javax.servlet.http")) {
                            continue;
                        }
                        String typeName = p.getParameterizedType().getTypeName();
                        if (JavaUtil.isPrimitive(typeName)) {
                            paramNode.put(p.getName(), typeName);
                        } else {
                            if (!typeName.contains("<")) {
                                for (Field f : Class.forName(typeName).getDeclaredFields()) {
                                    paramNode.put(f.getName(), f.getGenericType().getTypeName());
                                }
                             } else {

                                int idx = typeName.indexOf("<");
                                String genericClass = typeName.substring(0, idx);
                                String realClass = typeName.substring(idx + 1, typeName.length() - 1);
                                for (Field f : Class.forName(genericClass).getDeclaredFields()) {
                                    if (f.getGenericType().getTypeName().equals("T")) {
                                        ObjectNode subNode = new ObjectMapper().createObjectNode();
                                        for (Field sf : Class.forName(realClass).getDeclaredFields()) {
                                            subNode.put(sf.getName(), sf.getGenericType().getTypeName());
                                        }
                                        paramNode.set(f.getName(), subNode);
                                    } else {
                                        paramNode.put(f.getName(), f.getGenericType().getTypeName());
                                    }
                                }
                            }
                        }
                        urlToJson.put(url + mrm.value()[0], paramNode);
                    }

                } else {
                    urlToJson.put(url + mrm.value()[0], null);
                }
            }
        }
	}

    void addUrlToClass(String clz, String url) {
        List<String> list = classToUrl.get(clz) == null ?
                new ArrayList<>() : classToUrl.get(clz);
        list.add(url);
        classToUrl.put(clz, list);
    }
}
