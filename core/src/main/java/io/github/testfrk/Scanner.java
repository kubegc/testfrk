/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.SystemPropertyUtils;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet.
 * I do not known why. 
 * 
 * Do not modify.
 */
/**
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 *
 */
public class Scanner implements ResourceLoaderAware {

	public  static final Logger m_logger = Logger.getLogger(Scanner.class);
	
	/**
	 * 
	 */
	private static final List<TypeFilter> annotationTypeFilter = new LinkedList<TypeFilter>();

	/**
	 * 
	 */
	private static ResourcePatternResolver patternResolver     = new PathMatchingResourcePatternResolver();

	/**
	 * 
	 */
	private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

	/***********************************************************
	 * 
	 * Core
	 *
	 ************************************************************/
	
	/**
	 * 扫描指定包名basePackage，返回包名下所有名Annotation名为org.springframework.web.bind.annotation.RequestMapping的类
	 * 
	 * @param basePackage      包名
	 * @return 含有Annotation名为org.springframework.web.bind.annotation.RequestMapping的类集合
	 */
	@SuppressWarnings("unchecked")
	public static Set<Class<?>> scan(String basePackage) {
		try {
			return scan(basePackage, (Class<? extends Annotation>) 
					Class.forName(Constants.DEFAULT_REQUESTMAPPING));
		} catch (ClassNotFoundException e) {
			m_logger.warn("项目需要引用spring-web");
		}
		return new HashSet<>();
	}
	
	/**
	 * 扫描指定包名basePackage，返回包名下所有名Annotation名为annos的类
	 * 
	 * @param basePackage        包名
	 * @param annos              注释名
	 * @return 含有Annotation名为annos的类集合
	 */
	@SuppressWarnings("unchecked")
	public static Set<Class<?>> scan(String basePackage, Class<? extends Annotation>... annos) {
		
		if (basePackage == null || annos == null) {
			throw new NullPointerException("包名basePackage和注释annos都不能为空");
		}
		
		for (Class<? extends Annotation> anno : annos) {
			addIncludeFilter(new AnnotationTypeFilter(anno));
		}

		return doScan(basePackage);
	}
	

	/**
	 * 扫描指定包名basePackage，返回包名下所有annotationTypeFilter覆盖的类
	 * 
	 * @param basePackage        包名
	 * @return 返回包名下所有annotationTypeFilter覆盖的类
	 */
	private static Set<Class<?>> doScan(String basePackage) {
		
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		try {
			
			// class文件路径
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ org.springframework.util.ClassUtils.convertClassNameToResourcePath(
							SystemPropertyUtils.resolvePlaceholders(basePackage))
					+ "/**/*.class";
			
			// 所有class文件，抽象为资源resource
			Resource[] resources = patternResolver.getResources(packageSearchPath);

			for (int i = 0; i < resources.length; i++) {
				// 得到资源的元信息
				MetadataReader metadataReader = metadataReaderFactory
								.getMetadataReader(resources[i]);
				
				// 元信息中应该包含指定的注解Annotation
				if (isValid(metadataReader, resources[i])) {
					classes.add(Class.forName(metadataReader
							.getClassMetadata().getClassName()));
				}
			}
			
		} catch (Exception ex) {
			throw new BeanDefinitionStoreException("IO异常，请重试.");
		}
		
		return classes;
	}

	/***********************************************************
	 * 
	 * Utils
	 *
	 ************************************************************/
	/**
	 * @param includeFilter        过滤器
	 */
	private static void addIncludeFilter(TypeFilter includeFilter) {
		annotationTypeFilter.add(includeFilter);
	}

	/**
	 * @param metadataReader     元信息读取器
	 * @param resource           资源
	 * @return   如果资源包含指定的Annotation，即为有效，否则为无效
	 * @throws IOException       IO异常
	 */
	private static boolean isValid(MetadataReader metadataReader, Resource resource) throws IOException {
		if (resource.isReadable() || (annotationTypeFilter.size() == 0 || matches(metadataReader))) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param metadataReader      元信息读取器
	 * @return 如果资源包含指定的Annotation，即为有效，否则为无效
	 * @throws IOException        IO异常
	 */
	private static boolean matches(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : annotationTypeFilter) {
			if (tf.match(metadataReader, metadataReaderFactory)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 我也不清楚为啥，但根据网上教程必须设置
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		patternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

}
