# Spring Boot GitHub API Application

This is a Spring Boot application that interacts with the GitHub API to retrieve information about a user's repositories and their branches.

## Technologies Used

- Java 17
- Spring Boot
- Jackson (JSON processing library)
- RestTemplate (HTTP client for RESTful services)
- JUnit and MockMvc (for testing)

## Project Structure

The project has the following main packages:

- `com.atipera.github.controllers`: Contains the controller classes that handle HTTP requests.
- `com.atipera.github.model`: Contains the DTO classes used for mapping JSON responses.
- `com.atipera.github.service`: Contains the service classes responsible for interacting with the GitHub API.

## Endpoints

- `GET /api/users/{username}`: Returns information about GitHub user repositories (name and owner) that are not forks in JSON format. For each repository, the names of the last commit and the SHA number are listed.
- `GET /api/users/{username}?token={token}`: A query with the 'token' parameter performs the same task as a query without a token, but it may prove useful when it turns out that the number of queries exceeds the limit granted to queries without authentication by GitHub servers. Tokenless queries should suffice for a small number of queries per minute or for a user query with a small number of repositories or commits. You can obtain a GitHub token by registering on the GitHub portal and generating your own token. It is a long string of characters usually starting with "ghp_". 

## Running the Application

1. Clone the repository.
2. Open the project in your preferred Java IDE.
3. Run the application.

## Testing

The application includes unit tests for its components. You can find the tests in the `src/test` directory. The `GithubApiControllerTest` class contains several test methods that validate the behavior of the `GithubApiController` class.

### Test Descriptions

- `shouldGet406`: This test ensures that the endpoint returns a `406 Not Acceptable` response when the Accept header is set to `application/xml`. It checks whether the response contains the expected error message.
- `shouldGet404`: This test checks that the endpoint returns a `404 Not Found` response when querying a user that doesn't exist. The test verifies whether the response contains the expected error message.
- `shouldGet200`: This test validates that the endpoint returns a `200 OK` response for a valid user. The main purpose is to ensure that the endpoint is functioning correctly.
- `shouldGetItems`: This test confirms that the response JSON has a non-null items field. It checks whether the returned JSON structure contains the expected field.
- `shouldGetOwner`: This test checks that the owner's login field for the first repository in the response is not null. It ensures that the owner field exists and contains data.
- `shouldGetRepoName`: This test verifies that the name field of the first repository in the response exists. It ensures that the repository name is present in the response.
- `shouldGetBranchName`: This test asserts that the name of the first branch in the second repository matches the value "master". It checks the correctness of branch name extraction.
- `shouldGetShaNumber`: This test ensures that the response contains a valid SHA number for the first branch in the third repository. It validates the presence of a commit SHA.
- `shouldReturnResponse200`: The test checks if the `getResponseForUser` method returns a response with status code `200` when called with valid parameters.
- `shouldReturnResponse401WithToken`: This test simulates the case where the getResponseForUser method is called with a token that is not valid, resulting in a `401` status code response. It then tests that the response received is in the expected JSON format. 

Each test method sends a specific request to the endpoint, and then uses MockMvc to perform assertions on the response status, JSON structure, and content. The `andDo(print())` method helps in printing the response content for debugging purposes.
Feel free to customize and expand these tests to cover more scenarios and edge cases, ensuring that your application behaves as expected under different conditions. 
Remember that for all tests to work properly, you need to connect to the GitHub API `https://api.github.com/repos/` and `https://api.github.com/search`.