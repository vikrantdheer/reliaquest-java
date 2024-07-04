package com.example.rqchallenge;

import com.example.rqchallenge.controller.EmployeeController;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @MockBean
    EmployeeService employeeService;
    @Autowired
    MockMvc mockMvc;
    private List<Employee> employeeList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeList = new ArrayList<>();
        Employee employee1 = new Employee("1", "Vikrant Dheer", 5000, 35);
        Employee employee2 = new Employee("2", "John Paul", 6000, 45);
        employeeList.add(employee1);
        employeeList.add(employee2);
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Vikrant Dheer"))
                .andExpect(jsonPath("$[1].name").value("John Paul"));
    }

    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        when(employeeService.getEmployeesByNameSearch(anyString())).thenReturn(employeeList);

        mockMvc.perform(get("/api/v1/search").param("searchString", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Vikrant Dheer"))
                .andExpect(jsonPath("$[1].name").value("John Paul"));
    }

    @Test
    public void testGetEmployeeById() throws Exception {
        Employee employee = new Employee("1", "John Doe", 5000, 50);
        when(employeeService.getEmployeeById(anyString())).thenReturn(employee);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(6000);

        mockMvc.perform(get("/api/v1/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("6000"));
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> employeeNames = Arrays.asList("John Doe", "Jane Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(employeeNames);

        mockMvc.perform(get("/api/v1/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("John Doe"))
                .andExpect(jsonPath("$[1]").value("Jane Doe"));
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Employee employee = new Employee("3", "Sam Smith", 7000, 70);
        when(employeeService.createEmployee(any(Map.class))).thenReturn(employee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sam Smith\", \"salary\": \"7000\", \"age\": \"70\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.name").value("Sam Smith"))
                .andExpect(jsonPath("$.salary").value("7000"))
                .andExpect(jsonPath("$.age").value("70"));
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeBy(anyString())).thenReturn("John Doe");

        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee: John Doe with id: 1 deleted successfully"));
    }
}
