package com.atipera.github.model;

import com.atipera.github.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonProperty("items")
    @JsonView(Views.Public.class)
    private List<RepositoryDto> repositories = new ArrayList<>();

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RepositoryDto {
        @JsonProperty("name")
        @JsonView(Views.Public.class)
        private String repositoryName;
        @JsonView(Views.Public.class)
        private OwnerDto owner;
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class OwnerDto {
            @JsonProperty("login")
            @JsonView(Views.Public.class)
            private String ownerLogin;
        }


        private Boolean fork;
        @JsonView(Views.Public.class)
        private List<BranchDto> branches = new ArrayList<>();

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BranchDto {
            @JsonView(Views.Public.class)
            private String name;
            @JsonView(Views.Public.class)
            private CommitDto commit;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class CommitDto {
                @JsonView(Views.Public.class)
                private String sha;
            }
        }
    }
}
