package io.github.testfrk.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.github.kubesys.httpfrk.HttpServer;

import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @since 2019.11.16
 * 
 *        <p>
 *        The {@code ApplicationServer} class is used for starting web
 *        applications. Please configure
 * 
 *        src/main/resources/application.yml src/main/resources/log4j.properties
 * 
 */
@ComponentScan(basePackages = {
		"io.github.newhero.springboot.controllers" })
@SpringBootApplication
@EnableAutoConfiguration
@Configuration
public class TestServer  {

	public static void main(String[] args) {
		SpringApplication.run(TestServer.class, args);
	}

}