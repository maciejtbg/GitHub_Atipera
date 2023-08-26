package com.atipera.github.service;


import com.atipera.github.model.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class UserService {

    private static final String BASE_URL = "https://api.github.com/";
    private final RestTemplate restTemplate = new RestTemplate(); //klasa Spring Boot to przekazywania i obsługi zapytań REST
    private final ObjectMapper objectMapper = new ObjectMapper(); //Object Mapper do mapowania na obiekt klasy UserDto

    public ResponseEntity<?> getResponseForUser(String userName, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json"); //nagłówek z github
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        if (token != null) { // jeśli nie ma tokena to tej linii nagłówka nie należy podawać 401
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        try {
            String apiUrl = BASE_URL + "search/repositories?q=user:" + userName;

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

    public UserDto getUser(String userName, String token) {
        ResponseEntity<?> responseEntity = getResponseForUser(userName, token);
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
