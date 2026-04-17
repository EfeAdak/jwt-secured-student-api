package com.efe.apidemo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isUnauthorized());
    }

    void shouldReturn401_whenUserAccessEndpoint() throws Exception {
        String userToken = obtainToken("efe", "123456");

        mockMvc.perform(post("/students")
                .header("Authorization", "Bearer" + userToken)
                .contentType("application/json")
                .content("""
                        {
                            "name": "Ali",
                            "email": "ali@example.com"
                        }
                        """))
                .andExpect(status().isForbidden());
    }

     private String obtainToken(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("""
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                    """.formatted(username, password)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*

        response example -> {"token":"abc123xyz"}
        1- split(":") then its ->   ["{\"token\"", "\"abc123xyz\"}"]
        2- [1] then its ->  "\"abc123xyz\"}"
        3- replaceAll("[\"}]", "") then its ->  abc123xyz

        */
        return response.split(":")[1].replaceAll("[\"}]", "");

     }


}