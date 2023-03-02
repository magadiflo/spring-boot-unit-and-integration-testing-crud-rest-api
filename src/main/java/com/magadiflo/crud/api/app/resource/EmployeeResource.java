package com.magadiflo.crud.api.app.resource;

import com.magadiflo.crud.api.app.entities.Employee;
import com.magadiflo.crud.api.app.service.IEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/employees")
public class EmployeeResource {

    private final IEmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return this.employeeService.getAllEmployees();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return this.employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.employeeService.saveEmployee(employee));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return this.employeeService.getEmployeeById(id)
                .map(employeeDB -> {
                    employeeDB.setFirstName(employee.getFirstName());
                    employeeDB.setLastName(employee.getLastName());
                    employeeDB.setEmail(employee.getEmail());

                    Employee updatedEmployee = this.employeeService.updateEmployee(employeeDB);
                    return new ResponseEntity<Employee>(updatedEmployee, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        this.employeeService.deleteEmployee(id);
        return new ResponseEntity<String>("Employee deleted successfully!", HttpStatus.OK);
    }

}
