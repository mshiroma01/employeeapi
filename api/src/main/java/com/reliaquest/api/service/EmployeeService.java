package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeListResponse;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeService {

    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final RestTemplate restTemplate;

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private <T> T executeWithRetry(Supplier<T> request) {
        int attempts = 0;
        int maxRetries = 3;
        int backoffMs = 1000;

        while (true) {
            try {
                return request.get();
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempts++;
                if (attempts > maxRetries) throw e;
                logger.warn(
                        "Rate limited! Retrying in {}ms (attempt {}/{})", backoffMs * attempts, attempts, maxRetries);
                try {
                    Thread.sleep(backoffMs * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
    }

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        return executeWithRetry(() -> {
            ResponseEntity<EmployeeListResponse> resp =
                    restTemplate.exchange(BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            return Optional.ofNullable(resp.getBody())
                    .map(EmployeeListResponse::getData)
                    .orElse(List.of());
        });
    }

    public List<Employee> searchByName(String searchString) {
        logger.info("Searching by name fragment: {}", searchString);
        return getAllEmployees().stream()
                .filter(e -> e.getEmployee_name() != null
                        && e.getEmployee_name().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getById(String id) {
        if (id.isBlank()) {
            logger.warn("Blank ID passed!");
            return null;
        }
        String url = BASE_URL + "/" + id;
        logger.info("Fetching employee by ID: {}", id);
        try {
            return executeWithRetry(() -> {
                ResponseEntity<EmployeeResponse> resp = restTemplate.getForEntity(url, EmployeeResponse.class);
                return Optional.ofNullable(resp.getBody())
                        .map(EmployeeResponse::getData)
                        .orElse(null);
            });
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Employee {} not found", id);
            return null;
        }
    }

    public Integer getHighestSalary() {
        logger.info("Calculating highest salary");
        return getAllEmployees().stream()
                .map(Employee::getEmployee_salary)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> getTop10HighestEarners() {
        logger.info("Top 10 earners");
        return getAllEmployees().stream()
                .filter(e -> e.getEmployee_salary() != null)
                .sorted((e1, e2) -> e2.getEmployee_salary().compareTo(e1.getEmployee_salary()))
                .limit(10)
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(EmployeeInput request) {
        if (request == null) {
            logger.warn("Null input!");
            return null;
        }
        logger.info("Creating employee: {}", request.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmployeeInput> body = new HttpEntity<>(request, headers);

        return executeWithRetry(() -> {
            ResponseEntity<EmployeeResponse> resp = restTemplate.postForEntity(BASE_URL, body, EmployeeResponse.class);
            return Optional.ofNullable(resp.getBody())
                    .map(EmployeeResponse::getData)
                    .orElse(null);
        });
    }

    public String deleteEmployee(String id) {
        if (id.isBlank()) {
            logger.warn("Blank ID passed!");
            return null;
        }
        String url = BASE_URL + "/" + id;
        logger.info("Deleting employee by ID: {}", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>("{\"name\":\"" + id + "\"}", headers);

        try {
            return executeWithRetry(() -> {
                restTemplate.exchange(url, HttpMethod.DELETE, body, String.class);
                return "Employee deleted successfully.";
            });
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Employee with ID {} was not found!", id);
            return "Employee not found.";
        }
    }
}
