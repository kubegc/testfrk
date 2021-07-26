/**
 * Copyright (2021, ) NewHero COM.LTM
 */
package io.github.newhero.tests;


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
 * @since  2019.11.16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = io.github.newhero.springboot.TestServer.class)
@AutoConfigureMockMvc
@ComponentScan(basePackages= {"io.github.newhero.springboot"})
public class UserControllerTest  {

    @Autowired
    private MockMvc mvc;
	public final static String ECHOHELLO2_PATH = "/user/echoUser";

    
    private String ECHOHELLO2_INVALID_NAME__DATA ="{  \"name\" : \"J7BJ1631IDA58F596DFJ5\",  \"age\" : 99}";

    @Test
    public void ECHOHELLO2_INVALID_NAME_Test() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(ECHOHELLO2_PATH)
                .content(ECHOHELLO2_INVALID_NAME__DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false));
    }


    
    private String ECHOHELLO2_INVALID_AGE__DATA ="{  \"name\" : \"2G0GI837C66A7FGA3A9\",  \"age\" : 101}";

    @Test
    public void ECHOHELLO2_INVALID_AGE_Test() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(ECHOHELLO2_PATH)
                .content(ECHOHELLO2_INVALID_AGE__DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false));
    }


    
    private String ECHOHELLO2_VALID__DATA ="{  \"name\" : \"G077DFA1331AC8E5D82\",  \"age\" : 99}";

    @Test
    public void ECHOHELLO2_VALID_Test() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(ECHOHELLO2_PATH)
                .content(ECHOHELLO2_VALID__DATA)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true));
    }



}

