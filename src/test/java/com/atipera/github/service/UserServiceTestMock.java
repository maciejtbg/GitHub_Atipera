package com.atipera.github.service;

import com.atipera.github.model.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserService.class)
@Import(UserServiceTestMock.TestConfig.class)
class UserServiceTestMock {

    @Autowired
    private UserService userService;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;


    @Test
    void getBranchDtoListShouldReturnRepositoryDetails() throws JsonProcessingException {
        String owner = "maciejtbg";
        String repository = "Homework";

        String json = """
                [
                    {
                        "name": "master",
                        "commit": {
                            "sha": "8bae91a3fd0dfac741a56906ba03c6ffcb04bc0b",
                            "url": "https://api.github.com/repos/maciejtbg/Homework/commits/8bae91a3fd0dfac741a56906ba03c6ffcb04bc0b"
                        },
                        "protected": false
                    }
                ]
                     """;

        this.mockRestServiceServer
                .expect(requestTo("https://api.github.com/repos/"+owner+"/"+repository+"/branches"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<UserDto.RepositoryDto.BranchDto> result = userService.getBranchDtoList(owner, repository);

        assertNotNull(result);
    }

    static class TestConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

}