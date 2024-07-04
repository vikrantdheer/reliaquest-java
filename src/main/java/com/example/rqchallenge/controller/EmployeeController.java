package com.example.rqchallenge.controller;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class EmployeeController implements IEmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<?> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        if (highestSalary == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service down, please try again later");
        }
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<?> getTopTenHighestEarningEmployeeNames() {
        List<String> employeeNames = employeeService.getTopTenHighestEarningEmployeeNames();
        if (employeeNames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service down, please try again later");
        }
        return ResponseEntity.ok(employeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String employeeName = employeeService.deleteEmployeeBy(id);
        if (employeeName.contains("Error occurred while deleting employee")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeName);
        }
        return ResponseEntity.ok("Employee: " + employeeName + " with id: " + id + " deleted successfully");
    }
}