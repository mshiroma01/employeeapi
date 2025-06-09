package com.reliaquest.api.model;

public class Employee {

    public String id;
    public String employee_name;
    public Integer employee_salary;
    public Integer employee_age;
    public String employee_title;
    public String employee_email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public Integer getEmployee_salary() {
        return employee_salary;
    }

    public void setEmployee_salary(Integer employee_salary) {
        this.employee_salary = employee_salary;
    }

    public Integer getEmployee_age() {
        return employee_age;
    }

    public void setEmployee_age(Integer employee_age) {
        this.employee_age = employee_age;
    }

    public String getEmployee_title() {
        return employee_title;
    }

    public void setEmployee_Title(String employee_title) {
        this.employee_title = employee_title;
    }

    public String getEmployee_Email() {
        return employee_email;
    }

    public void setEmployee_Email(String employee_email) {
        this.employee_email = employee_email;
    }
}
