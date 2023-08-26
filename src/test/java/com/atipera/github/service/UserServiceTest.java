package com.atipera.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.github.com/";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        userService = new UserService();
    }

    @Test
    void shouldReturnResponse200() {
        String userName = "test-user";
        String apiUrl = BASE_URL + "search/repositories?q=user:" + userName;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("response body", HttpStatus.OK);
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), eq(httpEntity), eq(String.class)))
                .thenReturn(responseEntity);

        ResponseEntity<?> result = userService.getResponseForUser(userName, null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldReturnResponse401WithToken() throws JsonProcessingException {
        String userName = "test-user";
        String token = "test-token";
        ResponseEntity<?> result = userService.getResponseForUser(userName, token);

        String expectedJsonString = """
                {
                    "message": "Bad credentials",
                    "documentation_url": "https://docs.github.com/rest"
                }
                """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedJsonNode = objectMapper.readTree(expectedJsonString);

        JsonNode actualJsonNode = objectMapper.readTree((String) result.getBody());

        assertEquals(expectedJsonNode, actualJsonNode);
    }

}
