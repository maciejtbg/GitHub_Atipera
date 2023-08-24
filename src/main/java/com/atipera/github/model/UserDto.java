package com.atipera.github.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Override
    public String toString() {
        return "UserDto{" +
                "repositories=" + repositories +
                '}';
    }

    List<RepositoryDto> repositories = new ArrayList<>();

}
