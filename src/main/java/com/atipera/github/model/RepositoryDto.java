package com.atipera.github.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDto {
    String repositoryName;
    String ownerLogin;
    Boolean fork;

    @Override
    public String toString() {
        return "RepositoryDto{" +
                "repositoryName='" + repositoryName + '\'' +
                ", ownerLogin='" + ownerLogin + '\'' +
                ", fork=" + fork +
                ", branches=" + branches +
                '}';
    }

    List<BranchDto> branches = new ArrayList<>();
}
