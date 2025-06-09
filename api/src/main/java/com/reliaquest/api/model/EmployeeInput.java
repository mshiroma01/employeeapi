package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeInput {
    @JsonProperty("name")
    private String name;

    @JsonProperty("salary")
    private Integer salary;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("title")
    private String title;

    // no-args constructor for Jackson
    public EmployeeInput() {}

    public EmployeeInput(String name, Integer salary, Integer age, String title) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
