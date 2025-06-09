package com.reliaquest.api.model;

//  Wrapper class for a single employee
public class EmployeeResponse {
    private Employee data;
    private String status;

    public Employee getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setData(Employee data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
