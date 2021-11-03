/**
 * Copyright (2021, ) NewHero COM.LTM
 */
package io.github.frks;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * @author wuheng@iscas.ac.cn
 * @since  2021.10.8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = io.github.testfrk.springboot.TestServer.class)
@AutoConfigureMockMvc
@ComponentScan(basePackages= {"io.github.testfrk.springboot"})
public class UserControllerTest  {

    @Autowired
    private MockMvc mvc;

	public static final String getUser_PATH = "/user/getUser";

 
    @Test
    public void get_getUser_valid_allTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/user/getUser?name=E909H4HD56F7E0AC644&age=99")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andReturn();
    }

 
    @Test
    public void get_getUser_invalid_nameTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/user/getUser?name=3C9I470KGAGEFAB2H13E7&age=99")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

 
    @Test
    public void get_getUser_invalid_ageTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/user/getUser?name=E909H4HD56F7E0AC644&age=101")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

	public static final String echoUser_PATH = "/user/echoUser";

    
    private String post_echoUser_valid_all_DATA ="{\"name\":\"DBIC5714G1G711C00I5\",\"age\":99,\"desc\":\"573731464\"}";

    @Test
    public void post_echoUser_valid_allTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser_PATH)
                .content(post_echoUser_valid_all_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andReturn();
    }

    
    private String post_echoUser_invalid_name_DATA ="{\"name\":\"A35IHGF9D9JD2FB9KHB5G\",\"age\":99,\"desc\":\"573731464\"}";

    @Test
    public void post_echoUser_invalid_nameTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser_PATH)
                .content(post_echoUser_invalid_name_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

    
    private String post_echoUser_invalid_age_DATA ="{\"name\":\"DBIC5714G1G711C00I5\",\"age\":101,\"desc\":\"573731464\"}";

    @Test
    public void post_echoUser_invalid_ageTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser_PATH)
                .content(post_echoUser_invalid_age_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

    
    private String post_echoUser_invalid_desc_DATA ="{\"name\":\"DBIC5714G1G711C00I5\",\"age\":99,\"desc\":\"56695142644\"}";

    @Test
    public void post_echoUser_invalid_descTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser_PATH)
                .content(post_echoUser_invalid_desc_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }
}