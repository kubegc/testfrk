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

	public static final String echoUser_PATH = "/user/echoUser";

    
    private String post_echoUser_valid_all_DATA ="{\"name\":\"FFCEIABEGC106IE81G1\",\"age\":99,\"desc\":{\"note\":\"161362418\"}}";

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

    
    private String post_echoUser_invalid_name_DATA ="{\"name\":\"215HIEAHG2C65C93DFA04\",\"age\":101}";

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

    
    private String post_echoUser_invalid_age_DATA ="{\"name\":\"215HIEAHG2C65C93DFA04\",\"age\":101}";

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

    
    private String post_echoUser_invalid_desc_DATA ="{\"name\":\"215HIEAHG2C65C93DFA04\",\"age\":101}";

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

	public static final String getUser_PATH = "/user/getUser";

 
    @Test
    public void get_getUser_valid_allTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/user/getUser?name=4BE833GHE229934DGI4&age=99")
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
                .get("/user/getUser?name=494B0909K8DAHJB962A1F&age=101")
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
                .get("/user/getUser?name=494B0909K8DAHJB962A1F&age=101")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

	public static final String echoUser2_PATH = "/user/echoUser2";

    
    private String post_echoUser2_valid_all_DATA ="{\"name\":\"89CDF959G388GA2B7EC\",\"age\":99,\"note\":\"485657554\"}";

    @Test
    public void post_echoUser2_valid_allTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser2_PATH)
                .content(post_echoUser2_valid_all_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andReturn();
    }

    
    private String post_echoUser2_invalid_name_DATA ="{\"name\":\"CI48K731FKEG9842HE386\",\"age\":101,\"note\":\"A57577A5880\"}";

    @Test
    public void post_echoUser2_invalid_nameTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser2_PATH)
                .content(post_echoUser2_invalid_name_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

    
    private String post_echoUser2_invalid_age_DATA ="{\"name\":\"CI48K731FKEG9842HE386\",\"age\":101,\"note\":\"A57577A5880\"}";

    @Test
    public void post_echoUser2_invalid_ageTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser2_PATH)
                .content(post_echoUser2_invalid_age_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }

    
    private String post_echoUser2_invalid_note_DATA ="{\"name\":\"CI48K731FKEG9842HE386\",\"age\":101,\"note\":\"A57577A5880\"}";

    @Test
    public void post_echoUser2_invalid_noteTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(echoUser2_PATH)
                .content(post_echoUser2_invalid_note_DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andReturn();
    }
}