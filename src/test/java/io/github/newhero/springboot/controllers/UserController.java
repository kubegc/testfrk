/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero.springboot.controllers;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@RequestMapping(value= "/user")
public class UserController {

	@RequestMapping(value= "/echoUser", 
			method = RequestMethod.POST)
	public Object echoHello2(@RequestBody 
			@Pattern(regexp = "[0-9a-zA-Z]{6,20}") String name, 
			@Min(0) @Max(100) int age,
			String desc) {
		return "Hello, " + name + "," + age;
	}
	
}
