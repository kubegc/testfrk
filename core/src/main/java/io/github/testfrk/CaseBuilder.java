/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;


import com.fasterxml.jackson.databind.JsonNode;


/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 */
public abstract class CaseBuilder {

	public abstract JsonNode build(String url, JsonNode dataSet);
}
