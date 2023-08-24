package com.atipera.github.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    String name;

    @Override
    public String toString() {
        return "BranchDto{" +
                "name='" + name + '\'' +
                ", sha1='" + sha1 + '\'' +
                '}';
    }

    String sha1; //numer ostatniego zatwierdzenia
}
