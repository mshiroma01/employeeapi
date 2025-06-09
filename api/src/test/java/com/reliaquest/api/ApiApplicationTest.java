package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class ApiApplicationTest {

    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    private EmployeeService employeeService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        employeeService = new EmployeeService(restTemplate);
    }

    @Test
    void testGetAllEmployees() {
        String mockJson =
                """
            {
              "data": [
                {
                  "id": "4a3a170b-22cd-4ac2-aad1-9bb5b34a1507",
                  "employee_name": "Tiger Nixon",
                  "employee_salary": 320800,
                  "employee_age": 61,
                  "employee_title": "Vice Chair Executive Principal of Chief Operations Implementation Specialist",
                  "employee_email": "tnixon@company.com"
                }
              ],
              "status": "Successfully processed request."
            }
            """;

        mockServer.expect(requestTo(BASE_URL)).andRespond(withSuccess(mockJson, MediaType.APPLICATION_JSON));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("Tiger Nixon", result.get(0).getEmployee_name());

        mockServer.verify();
    }

    @Test
    void testGetEmployeesByNameSearch() {
        String mockJson =
                """
            {
              "data": [
                {
                  "id": "4a3a170b-22cd-4ac2-aad1-9bb5b34a1507",
                  "employee_name": "Tiger Nixon",
                  "employee_salary": 320800,
                  "employee_age": 61,
                  "employee_title": "Vice Chair Executive Principal of Chief Operations Implementation Specialist",
                  "employee_email": "tnixon@company.com"
                },
                {
                  "id": "5255f1a5-f9f7-4be5-829a-134bde088d17",
                  "employee_name": "Bill Bob",
                  "employee_salary": 89750,
                  "employee_age": 24,
                  "employee_title": "Documentation Engineer",
                  "employee_email": "billBob@company.com"
                }
              ],
              "status": "Successfully processed request."
            }
            """;

        mockServer.expect(requestTo(BASE_URL)).andRespond(withSuccess(mockJson, MediaType.APPLICATION_JSON));

        List<Employee> result = employeeService.searchByName("bob");

        assertEquals(1, result.size());
        assertEquals("Bill Bob", result.get(0).getEmployee_name());

        mockServer.verify();
    }

    @Test
    void testGetEmployeeByIdFound() {
        String id = "5255f1a5-f9f7-4be5-829a-134bde088d17";
        String mockJson =
                """
            {
              "data": {
                "id": "5255f1a5-f9f7-4be5-829a-134bde088d17",
                "employee_name": "Bill Bob",
                "employee_salary": 89750,
                "employee_age": 24,
                "employee_title": "Documentation Engineer",
                "employee_email": "billBob@company.com"
              },
              "status": "Successfully processed request."
            }
            """;

        mockServer.expect(requestTo(BASE_URL + "/" + id)).andRespond(withSuccess(mockJson, MediaType.APPLICATION_JSON));

        Employee result = employeeService.getById(id);

        assertNotNull(result);
        assertEquals("Bill Bob", result.getEmployee_name());

        mockServer.verify();
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        String id = "ndasfhdafgdas";

        mockServer.expect(requestTo(BASE_URL + "/" + id)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        Employee result = employeeService.getById(id);

        assertNull(result);
        mockServer.verify();
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        String mockJson =
                """
            {
              "data": [
                {
                  "id": "4a3a170b-22cd-4ac2-aad1-9bb5b34a1507",
                  "employee_name": "Tiger Nixon",
                  "employee_salary": 320800,
                  "employee_age": 61,
                  "employee_title": "Vice Chair Executive Principal of Chief Operations Implementation Specialist",
                  "employee_email": "tnixon@company.com"
                },
                {
                  "id": "5255f1a5-f9f7-4be5-829a-134bde088d17",
                  "employee_name": "Bill Bob",
                  "employee_salary": 89750,
                  "employee_age": 24,
                  "employee_title": "Documentation Engineer",
                  "employee_email": "billBob@company.com"
                }
              ],
              "status": "Successfully processed request."
            }
            """;

        mockServer.expect(requestTo(BASE_URL)).andRespond(withSuccess(mockJson, MediaType.APPLICATION_JSON));

        Integer result = employeeService.getHighestSalary();

        assertEquals(320800, result);
        mockServer.verify();
    }

    @Test
    void testGetTop10HighestEarningEmployeeNames() {
        String mockJson =
                """
            {
              "data": [
                { "employee_name": "A", "employee_salary": 100 },
                { "employee_name": "B", "employee_salary": 90 },
                { "employee_name": "C", "employee_salary": 80 },
                { "employee_name": "D", "employee_salary": 70 },
                { "employee_name": "E", "employee_salary": 60 },
                { "employee_name": "F", "employee_salary": 50 },
                { "employee_name": "G", "employee_salary": 40 },
                { "employee_name": "H", "employee_salary": 30 },
                { "employee_name": "I", "employee_salary": 20 },
                { "employee_name": "J", "employee_salary": 10 },
                { "employee_name": "K", "employee_salary": 0 }
              ],
              "status": "Successfully processed request."
            }
            """;

        mockServer.expect(requestTo(BASE_URL)).andRespond(withSuccess(mockJson, MediaType.APPLICATION_JSON));

        List<String> top10 = employeeService.getTop10HighestEarners();

        assertEquals(10, top10.size());
        assertEquals(List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"), top10);

        mockServer.verify();
    }

    @Test
    void testCreateEmployee() {
        String mockResponse =
                """
            {
              "data": {
                "id": "d005f39a-beb8-4390-afec-fd54e91d94ee",
                "employee_name": "Jill Jenkins",
                "employee_salary": 139082,
                "employee_age": 48,
                "employee_title": "Financial Advisor",
                "employee_email": "jillj@company.com"
              },
              "status": "Successfully created"
            }
            """;

        EmployeeInput input = new EmployeeInput("Jill Jenkins", 139082, 48, "Financial Advisor");

        mockServer
                .expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        Employee result = employeeService.createEmployee(input);

        assertNotNull(result);
        assertEquals("Jill Jenkins", result.getEmployee_name());
        assertEquals("Financial Advisor", result.getEmployee_title());

        mockServer.verify();
    }

    @Test
    void testDeleteEmployeeById() {
        String id = "d005f39a-beb8-4390-afec-fd54e91d94ee";
        String url = BASE_URL + "/" + id;

        String mockResponse =
                """
            {
              "data": true,
              "status": "Successfully deleted"
            }
            """;

        mockServer
                .expect(requestTo(url))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // Perform delete
        String result = employeeService.deleteEmployee(id);
        assertEquals("Employee deleted successfully.", result);

        mockServer.verify();
    }

    @Test
    void testDeleteEmployeeByIdNotFound() {
        String id = "dfgsayuidfkjhga";
        String url = BASE_URL + "/" + id;

        mockServer
                .expect(requestTo(url))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        String result = employeeService.deleteEmployee(id);
        assertEquals("Employee not found.", result);

        mockServer.verify();
    }
}
