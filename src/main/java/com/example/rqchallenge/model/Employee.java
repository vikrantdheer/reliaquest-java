package com.example.rqchallenge.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee implements Serializable {

    private static final long serialVersionUID = 5223635678214659252L;
    @JsonProperty("id")
    private String id;
    @JsonAlias({"employee_name", "name"})
    private String name;
    @JsonAlias({"employee_salary", "salary"})
    private Integer salary;
    @JsonAlias({"employee_age", "age"})
    private Integer age;

}
