/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.springboot.controllers;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@RequestMapping(value= "/user")
public class UserController {

	@RequestMapping(value= "/echoUser", 
			method = RequestMethod.POST)
	public Object echoHello2(@RequestParam @Validated @Pattern(regexp = "[0-9a-zA-Z]{6,20}") String name, 
			@RequestParam @Validated @Min(0) @Max(100) int age,
			@RequestParam @Validated @Length(max = 10, min = 2) String desc) {
		return "Hello, " + name + "," + age;
	}
	
}
