package com.atipera.github.controllers;

import com.atipera.github.Views;
import com.atipera.github.model.UserDto;
import com.atipera.github.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
class GithubApiController {

    private final UserService userService;

    @Autowired
    GithubApiController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/api/users/{username}")
    @JsonView(Views.Public.class) // Widok tylko publicznych pól
    public ResponseEntity<?> getUser(@RequestHeader(value = "Accept", required = false) String acceptHeader,
                                              @PathVariable("username") String username,
                                              @RequestParam(value = "token", required = false) String token) {
        if (acceptHeader.equalsIgnoreCase("application/xml")) { //obsługa w przypadku żądania XML
            ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE.value(), "Not acceptable XML header");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .contentType(MediaType.APPLICATION_JSON) // Ustawia format odpowiedzi na JSON
                    .body(errorMessage);
        }
        UserDto userDto = userService.getUser(username, token);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(userDto);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ErrorMessage {
    @JsonView(Views.Public.class)
    private int status;
    @JsonView(Views.Public.class)
    private String message;
}





