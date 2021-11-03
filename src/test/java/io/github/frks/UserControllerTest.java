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

	public static final String echoHello2_PATH = "/user/echoUser";

    
    private String echoUser_valid_all_DATA ="{\"name\":\"8ADH0HC4B6BB49E43G8\",\"age\":99,\"desc\":\"408552674\"}";

    @Test
    public void echoUser_valid_allTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoHello2_PATH)
                .content(echoUser_valid_all_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andReturn();
    }

    
    private String echoUser_invalid_name_DATA ="{\"name\":\"H6HI62BC2HG37II62CEGF\",\"age\":99,\"desc\":\"408552674\"}";

    @Test
    public void echoUser_invalid_nameTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoHello2_PATH)
                .content(echoUser_invalid_name_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

    
    private String echoUser_invalid_age_DATA ="{\"name\":\"8ADH0HC4B6BB49E43G8\",\"age\":101,\"desc\":\"408552674\"}";

    @Test
    public void echoUser_invalid_ageTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoHello2_PATH)
                .content(echoUser_invalid_age_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

    
    private String echoUser_invalid_desc_DATA ="{\"name\":\"8ADH0HC4B6BB49E43G8\",\"age\":99,\"desc\":\"A888A810519\"}";

    @Test
    public void echoUser_invalid_descTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoHello2_PATH)
                .content(echoUser_invalid_desc_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }
}