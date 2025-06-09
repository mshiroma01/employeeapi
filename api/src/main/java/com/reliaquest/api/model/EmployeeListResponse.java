package com.reliaquest.api.model;

import java.util.List;

//  Wrapper class for a list of employees
public class EmployeeListResponse {
    private List<Employee> data;
    private String status;

    public List<Employee> getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setData(List<Employee> data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
