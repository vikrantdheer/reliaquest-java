package com.example.rqchallenge.employees;

import com.example.rqchallenge.model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public interface IEmployeeController {

    @GetMapping("/employees")
    ResponseEntity<List<Employee>> getAllEmployees() throws IOException;

    @GetMapping("/search")
    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@RequestParam String searchString);

    @GetMapping("/employees/{id}")
    ResponseEntity<Employee> getEmployeeById(@PathVariable String id);

    @GetMapping("/highestSalary")
    ResponseEntity<?> getHighestSalaryOfEmployees();

    @GetMapping("/topTenHighestEarningEmployeeNames")
    ResponseEntity<?> getTopTenHighestEarningEmployeeNames();

    @PostMapping("/employees")
    ResponseEntity<Employee> createEmployee(@RequestBody Map<String, Object> employeeInput);

    @DeleteMapping("/employees/{id}")
    ResponseEntity<String> deleteEmployeeById(@PathVariable String id);

}
