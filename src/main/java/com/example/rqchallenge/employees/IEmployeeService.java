package com.example.rqchallenge.employees;

import com.example.rqchallenge.model.Employee;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(Map<String, Object> employeeInput);

    String deleteEmployeeBy(String id);
}
