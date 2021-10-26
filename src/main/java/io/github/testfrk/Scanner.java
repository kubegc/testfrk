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
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet
 */
public class Scanner implements ResourceLoaderAware {

	/**
	 * 
	 */
	private final List<TypeFilter> filters = new LinkedList<TypeFilter>();

	private ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.patternResolver);

	/***********************************************************
	 * 
	 * Core
	 *
	 ************************************************************/
	@SuppressWarnings("unchecked")
	public static Set<Class<?>> scan(String[] basePackages, Class<? extends Annotation>... annotations) {
		Scanner cs = new Scanner();

		if (annotations != null) {
			for (Class<? extends Annotation> anno : annotations) {
				cs.addIncludeFilter(new AnnotationTypeFilter(anno));
			}
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (String s : basePackages) {
			classes.addAll(cs.doScan(s));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	public static Set<Class<?>> scan(String basePackages, Class<? extends Annotation>... annotations) {
		return Scanner.scan(StringUtils.tokenizeToStringArray(basePackages, ",; \t\n"), annotations);
	}

	private Set<Class<?>> doScan(String basePackage) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ org.springframework.util.ClassUtils.convertClassNameToResourcePath(
							SystemPropertyUtils.resolvePlaceholders(basePackage))
					+ "/**/*.class";
			Resource[] resources = this.patternResolver.getResources(packageSearchPath);

			for (int i = 0; i < resources.length; i++) {
				Resource resource = resources[i];
				if (resource.isReadable()) {
					MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
					if (filters.size() == 0 || matches(metadataReader)) {
						try {
							classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
						} catch (ClassNotFoundException e) {
						}
					}
				}
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return classes;
	}
	
	/***********************************************************
	 * 
	 * Utils
	 *
	 ************************************************************/
	private void addIncludeFilter(TypeFilter includeFilter) {
		this.filters.add(includeFilter);
	}

	private boolean matches(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : this.filters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return true;
			}
		}
		return false;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.patternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

}
