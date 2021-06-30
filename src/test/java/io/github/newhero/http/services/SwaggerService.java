package io.github.newhero.http.services;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@RequestMapping(value= "/test")
public class SwaggerService {
	
	@RequestMapping(value= "/echoUser", 
			method = RequestMethod.POST)
	public Object echoHello2(@RequestBody User user) {
		return "Hello, " + user.getName();
	}
	
	public static class User {
		
		@Size(min = 1, max = 20)
		protected String name;
		
		@Min(0)
	    @Max(100)
		protected int age;

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
		
	}
}