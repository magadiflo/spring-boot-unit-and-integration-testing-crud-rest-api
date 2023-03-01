package com.magadiflo.crud.api.app.service;

import com.magadiflo.crud.api.app.entities.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id);

    Employee saveEmployee(Employee employee);

    Employee updateEmployee(Employee updateEmployee);

    void deleteEmployee(Long id);
}
