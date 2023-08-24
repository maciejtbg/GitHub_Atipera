package com.atipera.github.service;


import com.atipera.github.model.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final String BASE_URL = "https://api.github.com/";
    private final RestTemplate restTemplate = new RestTemplate(); //klasa Spring Boot to przekazywania i obsługi zapytań REST

    private static final String MY_TOKEN = "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"; //mój token działa przez 5 dni

    private final ObjectMapper objectMapper = new ObjectMapper(); //Object Mapper do mapowania

    public ResponseEntity<?> getResponseForUser(String userName) {
        //dzięki nagłówkowi osiąga się większą możliwą ilość zapytań na minutę
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + MY_TOKEN);
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(
                    BASE_URL + "search/repositories?q=user:{user}",
                    HttpMethod.GET,
                    httpEntity, //dodaje przygotowany nagłówek
                    String.class, //oczekuje String jako odpowiedź
                    userName
            );
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

    public UserDto getUser(String userName) {
        ResponseEntity<?> responseEntity = getResponseForUser(userName);
        //jeśli user nie istnieje, GitHub zwraca kod 422 (GitHub: 422 Unprocessable Entity)
        if (responseEntity.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            //user nie istnieje
            return null;
        } else {
            String responseString = (String) responseEntity.getBody();
            UserDto userDto;
            try {
                userDto = objectMapper.readValue(responseString, UserDto.class); //rzutuje odrazu na obiekt User ale bez branches
                for (UserDto.RepositoryDto repositoryDto : userDto.getRepositories()) { //iteruje po wszystkich repo i dodaje branches
                    repositoryDto.setBranches(getBranchDtoList(repositoryDto.getOwner().getOwnerLogin(), repositoryDto.getRepositoryName()));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return userDto;
        }
    }

    public List<UserDto.RepositoryDto.BranchDto> getBranchDtoList(String owner, String repo) throws JsonProcessingException {
        String responseString = restTemplate.getForObject(BASE_URL + "repos/{owner}/{repo}/branches", String.class, owner, repo);
        return objectMapper.readValue(responseString, new TypeReference<>() {
        });
    }
}
