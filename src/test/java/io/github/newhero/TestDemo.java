/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

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
public class TestDemo {

    public static String CLASS_TEMP = "package com.newhero.zw.prep.app.test;\n" +
            "\n" +
            "import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;\n" +
            "import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;\n" +
            "\n" +
            "import org.junit.Test;\n" +
            "import org.junit.runner.RunWith;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;\n" +
            "import org.springframework.boot.test.context.SpringBootTest;\n" +
            "import org.springframework.context.annotation.ComponentScan;\n" +
            "import org.springframework.http.MediaType;\n" +
            "import org.springframework.test.context.junit4.SpringRunner;\n" +
            "import org.springframework.test.web.servlet.MockMvc;\n" +
            "import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;\n" +
            "import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;\n" +
            "\n" +
            "\n" +
            "/**\n" +
            " * @author wuheng@iscas.ac.cn\n" +
            " * @since  2019.11.16\n" +
            " */\n" +
            "@RunWith(SpringRunner.class)\n" +
            "@AutoConfigureMockMvc\n" +
            "@SpringBootTest(classes = com.newhero.zw.prep.PrepServerApplication.class)\n" +
            "@ComponentScan(basePackages= {\"com.newhero.zw.prep\"})\n" +
            "public class #CLASSNAME#  {";

    public static String METHOD_TEMP = "\n" +
            "    public final static String PATH_#NAME# = \"#URL#\";\n" +
            "\n" +
            "    @Autowired\n" +
            "    private MockMvc mvc;\n" +
            "    private String data =\"#DATA#\";\n" +
            "\n" +
            "    @Test\n" +
            "    public void test_#NAME#() throws Exception {\n" +
            "        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders\n" +
            "                .post(PATH_#NAME#)\n" +
            "                .content(data)\n" +
            "                .contentType(MediaType.APPLICATION_JSON_UTF8)\n" +
            "                .accept(MediaType.APPLICATION_JSON_UTF8);\n" +
            "        mvc.perform(builder)\n" +
            "                .andExpect(status().isOk());\n" +
            "    }\n";

    static Map<String, List<String>> classToUrl = new HashMap<>();

    static Map<String, String> urlToMethod = new HashMap<>();

    static Map<String, ObjectNode> urlToJson = new HashMap<>();

    public static  void  main(String[] args) throws ClassNotFoundException {
        analyse();
        for (String name : classToUrl.keySet()) {
            System.out.print(CLASS_TEMP.replace("#CLASSNAME#", name + "Test"));
            for (String url : classToUrl.get(name)) {
                String content = urlToJson.get(url) == null ?
                        METHOD_TEMP.replace("#URL#", url)
                                .replace("#NAME#", urlToMethod.get(url))
                                .replace("#NAME#", urlToMethod.get(url))
                                .replace("#DATA#", ""):
                        METHOD_TEMP.replace("#URL#", url)
                                .replace("#NAME#", urlToMethod.get(url))
                                .replace("#NAME#", urlToMethod.get(url))
                                .replace("#DATA#", urlToJson.get(url).toPrettyString());
                System.out.println(content);
            }
            System.out.println("\n}\n\n");
        }
    }

    private static void analyse() throws ClassNotFoundException {
        String[] pkgs = new String[1];
        pkgs[0] = "com.newhero.zw.prep.app";
        // find all Controller
        for (Class<?> clz : ClassUtil.scan(pkgs, RequestMapping.class)) {
            RequestMapping[] mmp = clz.getAnnotationsByType(RequestMapping.class);
            String url = mmp[0].value()[0];

            // find all http services
            for (Method m : clz.getDeclaredMethods()) {
                RequestMapping mp = m.getAnnotation(RequestMapping.class);
                if (mp == null) {
                    continue;
                }

                addUrlToClass(clz.getSimpleName(), url + mp.value()[0]);
                urlToMethod.put(url + mp.value()[0], m.getName()) ;

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
                        urlToJson.put(url + mp.value()[0], paramNode);
                    }

                } else {
                    urlToJson.put(url + mp.value()[0], null);
                }

            }
        }
    }


    static void addUrlToClass(String clz, String url) {
        List<String> list = classToUrl.get(clz) == null ?
                new ArrayList<>() : classToUrl.get(clz);
        list.add(url);
        classToUrl.put(clz, list);
    }
}
