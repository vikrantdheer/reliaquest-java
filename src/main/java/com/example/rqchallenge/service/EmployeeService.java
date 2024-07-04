package com.example.rqchallenge.service;

import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.EmployeeServiceException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeResponse;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.rqchallenge.utils.Constants.BASE_URL;
import static com.example.rqchallenge.utils.Constants.CREATE;
import static com.example.rqchallenge.utils.Constants.EMPLOYEES;
import static com.example.rqchallenge.utils.Constants.SLASH;

@Service
@Slf4j
@Retry(name = "employeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Retry(name = "getAllEmployees", fallbackMethod = "fallbackForGetAllEmployees")
    public List<Employee> getAllEmployees() {
        try {
            String url = BASE_URL + EMPLOYEES;
            ResponseEntity<EmployeeResponse> response = restTemplate
                    .getForEntity(url, EmployeeResponse.class);
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Too many requests to the server", e);
            throw new EmployeeServiceException("Too many requests to the server", e);
        } catch (Exception e) {
            log.error("Error occurred while fetching all employees", e);
            throw new EmployeeServiceException("Error occurred while fetching all employees", e);
        }
    }

    public List<Employee> fallbackForGetAllEmployees(Exception e) {
        log.error("FALLBACK: Error occurred while fetching all employees as dummy service is down. " +
                "Please try again later.", e.getMessage());
        return new ArrayList<>();
    }

    @Override
    @Retry(name = "getEmployeesByNameSearch", fallbackMethod = "fallbackForGetEmployeesByNameSearch")
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.info("Attempting to search employees by name: {}", searchString);
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(e -> e.getName().contains(searchString))
                .collect(Collectors.toList());
    }

    public List<Employee> fallbackForGetEmployeesByNameSearch(String searchString, Exception e) {
        log.error("FALLBACK: Error occurred while searching employees by name", e);
        return new ArrayList<>();
    }

    @Override
    @Retry(name = "getEmployeesById", fallbackMethod = "fallbackForGetEmployeesById")
    public Employee getEmployeeById(String id) {
        try {
            String url = BASE_URL + EMPLOYEES + SLASH + id;
            ResponseEntity<Employee> response = restTemplate.getForEntity(url, Employee.class);

            if (response.getBody() == null) {
                throw new EmployeeNotFoundException("Employee with id: " + id + " not found");
            }

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Error Occured! Page Not found", e);
            throw new EmployeeServiceException("Error Occured! Page Not found", e);
        } catch (Exception e) {
            log.error("Error occurred while fetching employee by id", e);
            throw new EmployeeServiceException("Error occurred while fetching employee by id", e);
        }
    }

    public Employee fallbackForGetEmployeesById(String id, Exception e) {
        log.error("FALLBACK: Error occurred while fetching employee by id", e);
        return new Employee();
    }

    @Override
    @Retry(name = "getHighestSalaryOfEmployees", fallbackMethod = "fallbackForGetHighestSalaryOfEmployees")
    public Integer getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .max(Comparator.comparingInt(Employee::getSalary))
                .map(Employee::getSalary).orElse(0);
    }

    public Integer fallbackForGetHighestSalaryOfEmployees(Exception e) {
        log.error("FALLBACK: Error occurred while fetching highest salary of employees", e);
        return 0;
    }

    @Override
    @Retry(name = "getTopTenHighestEarningEmployeeNames", fallbackMethod =
            "fallbackForGetTopTenHighestEarningEmployeeNames")
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public List<String> fallbackForGetTopTenHighestEarningEmployeeNames(Exception e) {
        log.error("FALLBACK: Error occurred while fetching top ten highest earning employee names", e);
        return new ArrayList<>();
    }

    @Override
    @Retry(name = "createEmployee", fallbackMethod = "fallbackForCreateEmployee")
    public Employee createEmployee(Map<String, Object> employeeInput) {
        try {
            String url = BASE_URL + CREATE;
            ResponseEntity<Employee> response = restTemplate
                    .postForEntity(url, employeeInput, Employee.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error occurred while creating employee", e);
            throw new RuntimeException("Error occurred while creating employee", e);
        }
    }

    public Employee fallbackForCreateEmployee(Map<String, Object> employeeInput, Exception e) {
        log.error("FALLBACK: Error occurred while creating employee", e);
        return null;
    }

    @Override
    @Retry(name = "deleteEmployeeById", fallbackMethod = "fallbackForDeleteEmployeeById")
    public String deleteEmployeeBy(String id) {
        try {
            List<Employee> allEmployees = getAllEmployees();
            Employee employee = allEmployees.stream()
                    .filter(e -> e.getId().equals(id))
                    .findAny()
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id: " + id + " not found"));

            String url = BASE_URL + "/delete/" + id;
            restTemplate.delete(url);

            return employee.getName();
        } catch (EmployeeNotFoundException e) {
            log.error("Employee not found with id: " + id, e);
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while deleting employee", e);
            throw new RuntimeException("Error occurred while deleting employee", e);
        }
    }

    public String fallbackForDeleteEmployeeById(String id, Exception e) {
        log.error("FALLBACK: Error occurred while deleting employee by id", e);
        return "Error occurred while deleting employee";
    }
}
