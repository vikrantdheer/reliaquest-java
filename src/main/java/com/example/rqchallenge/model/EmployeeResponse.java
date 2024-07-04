package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EmployeeResponse implements Serializable {
    private static final long serialVersionUID = -8427814363994874257L;
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private List<Employee> data;

    @JsonProperty("message")
    private String message;

}