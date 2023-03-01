package com.magadiflo.crud.api.app.service.impl;

import com.magadiflo.crud.api.app.entities.Employee;
import com.magadiflo.crud.api.app.repository.IEmployeeRepository;
import com.magadiflo.crud.api.app.service.IEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Employee> getAllEmployees() {
        return this.employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return this.employeeRepository.findById(id);
    }

    @Transactional
    @Override
    public Employee saveEmployee(Employee employee) {
        Optional<Employee> employeeOptional = this.employeeRepository.findByEmail(employee.getEmail());
        if (employeeOptional.isPresent()) {
            throw new NoSuchElementException(String.format("Employee already exist with given email %s", employee.getEmail()));
        }
        return this.employeeRepository.save(employee);
    }

    @Transactional
    @Override
    public Employee updateEmployee(Employee updateEmployee) {
        Optional<Employee> employeeBD = this.employeeRepository.findById(updateEmployee.getId());
        Optional<Employee> employeeEmailBD = this.employeeRepository.findByEmail(updateEmployee.getEmail());

        if (updateEmployee.getEmail() != employeeBD.get().getEmail() && employeeEmailBD.isPresent()) {
            throw new NoSuchElementException(String.format("Update error, employee already exist with given email %s", updateEmployee.getEmail()));
        }

        return this.employeeRepository.save(updateEmployee);
    }

    @Transactional
    @Override
    public void deleteEmployee(Long id) {
        this.employeeRepository.deleteById(id);
    }
}
