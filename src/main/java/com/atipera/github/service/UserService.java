package com.atipera.github.service;

import com.atipera.github.model.BranchDto;
import com.atipera.github.model.RepositoryDto;
import com.atipera.github.model.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
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
            UserDto user = new UserDto();
            try {
                JsonNode root = objectMapper.readTree(responseString);
                JsonNode itemsNode = root.get("items");
                List<RepositoryDto> repositories = new LinkedList<>();
                for (JsonNode itemNode : itemsNode) { //iteruje po wszystkich repozytoriach
                    boolean repoFork = itemNode.path("fork").asBoolean();

                    if (!repoFork) { //warunek sprawdzający, czy repozytorium nie jest forkiem
                        RepositoryDto repository = new RepositoryDto();
                        String repoName = itemNode.path("name").asText();
                        repository.setRepositoryName(repoName);

                        String owner = itemNode.path("owner").path("login").asText();
                        repository.setOwnerLogin(owner);

                        repository.setBranches(getBranchDtoList(owner, repoName));
                        repositories.add(repository);
                    }
                }
                user.setRepositories(repositories);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return user;
        }
    }

    public List<BranchDto> getBranchDtoList(String owner, String repo) {

        List<BranchDto> list = new ArrayList<>();
        String response = restTemplate.getForObject(BASE_URL + "repos/{owner}/{repo}/branches", String.class, owner, repo);
        JsonNode root;
        try {
            root = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode itemsNode = root;
        for (JsonNode itemNode : itemsNode) {
            BranchDto branchDto = new BranchDto();
            String branchName = itemNode.path("name").asText();
            branchDto.setName(branchName);

            String sha1 = itemNode.path("commit").path("sha").asText();
            branchDto.setSha1(sha1);
            list.add(branchDto);
        }
        return list;
    }
}
