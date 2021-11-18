/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.springboot.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@Component
@RequestMapping(value= "/user")
public class UserController {

	@RequestMapping(value= "/echoUser", 
			method = RequestMethod.POST)
	public JsonNode echoUser(@RequestBody @Validated User<Desc> user) {
		ObjectNode res = new ObjectMapper().createObjectNode();
		res.put("success", true);
		res.put("data", user.toString());
		return res;
	}
	
	@RequestMapping(value= "/echoUser2", 
			method = RequestMethod.POST)
	public JsonNode echoUser2(@RequestBody @Validated AllUser user) {
		ObjectNode res = new ObjectMapper().createObjectNode();
		res.put("success", true);
		res.put("data", user.toString());
		return res;
	}
	
	@RequestMapping(value= "/getUser", 
			method = RequestMethod.GET)
	public JsonNode getUser(
			@RequestParam @Validated @Pattern(regexp = "[0-9a-zA-Z]{6,20}") String name,
			@RequestParam @Validated @Min(0) @Max(100) Integer age) {
		
		ObjectNode res = new ObjectMapper().createObjectNode();
		res.put("success", true);
		res.put("data", name + "-" + age);
		return res;
	}
	
	public static class AllUser {
		
		@Pattern(regexp = "[0-9a-zA-Z]{6,20}")
		protected String name;
		
		@Min(0) @Max(100)
		protected Integer age;
		
		@Length(max = 10)
		protected String note;
		
		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
		
		public String toString() {
			return "hello," + name + "," + age;
		}
		
	}
	
	public static class User<T> {
		
		@Pattern(regexp = "[0-9a-zA-Z]{6,20}")
		protected String name;
		
		@Min(0) @Max(100)
		protected Integer age;
		
		protected T desc;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
		
		public String toString() {
			return "hello," + name + "," + age;
		}
		
	}
	
	public static class Desc {
		@Length(max = 10, min = 2)
		protected String note;
		
		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}
	}
	
	@ExceptionHandler
	@ResponseBody
	public String invalidResponse(HttpServletRequest request, Exception e) throws Exception {
		ObjectNode res = new ObjectMapper().createObjectNode();
		res.put("success", false);
		res.put("reason", e.toString());
		return res.toPrettyString();
	}
}
