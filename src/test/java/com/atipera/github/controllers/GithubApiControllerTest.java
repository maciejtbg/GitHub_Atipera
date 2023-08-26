package com.atipera.github.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GithubApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGet406() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/xml"))
                .andDo(print())
                .andExpect(status().is(406))
                .andExpect(jsonPath("$.message", Matchers.is("Not acceptable XML header")));
    }

    @Test
    void shouldGet404() throws Exception {
        mockMvc.perform(get("/api/users/nosuchuseranymore").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message", Matchers.is("User not found")));
    }

    @Test
    void shouldGet200() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(status().is(200));
    }
    @Test
    void shouldGetItems() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(jsonPath("$.items", notNullValue()));
    }
    @Test
    void shouldGetOwner() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(jsonPath("$.items[0].owner.login",notNullValue()));
    }
    @Test
    void shouldGetRepoName() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(jsonPath("$.items[0].name").exists());
    }
    @Test
    void shouldGetBranchName() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(jsonPath("$.items[1].branches[0].name").value("master"));
    }
    @Test
    void shouldGetShaNumber() throws Exception {
        mockMvc.perform(get("/api/users/maciejtbg").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(jsonPath("$.items[2].branches[0].commit.sha").exists());
    }
}