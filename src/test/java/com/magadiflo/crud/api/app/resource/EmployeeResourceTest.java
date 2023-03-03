package com.magadiflo.crud.api.app.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magadiflo.crud.api.app.entities.Employee;
import com.magadiflo.crud.api.app.service.IEmployeeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(EmployeeResource.class)
class EmployeeResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value = "obtener todos los empleados")
    void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        // Given - condición previa o configuración
        List<Employee> listOfEmployees = Arrays.asList(
                Employee.builder().firstName("Martín").lastName("Díaz").email("martin@gmail.com").build(),
                Employee.builder().firstName("Gabriel").lastName("Flores").email("gabriel@gmail.com").build(),
                Employee.builder().firstName("Alejandra").lastName("Casanova").email("alejandra@gmail.com").build(),
                Employee.builder().firstName("Rosita").lastName("Pardo").email("rosita@gmail.com").build(),
                Employee.builder().firstName("Eli").lastName("Tello").email("eli@gmail.com").build()
        );
        Mockito.when(this.employeeService.getAllEmployees()).thenReturn(listOfEmployees);

        // When - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees"));


        // Then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(listOfEmployees.size())));
        Mockito.verify(this.employeeService, Mockito.times(1)).getAllEmployees();
    }

    @Test
    @DisplayName(value = "obtener empleado por id - escenario positivo")
    void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        // Given
        Long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("martin@gmail.com")
                .build();
        Mockito.when(this.employeeService.getEmployeeById(employeeId)).thenReturn(Optional.of(employee));

        // When
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", employeeId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(employee.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is(employee.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(employee.getEmail())));
        Mockito.verify(this.employeeService, Mockito.times(1)).getEmployeeById(employeeId);
    }

    @Test
    @DisplayName(value = "obtener empleado por id - escenario negativo")
    void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {
        // Given
        Long employeeId = 1L;
        Mockito.when(this.employeeService.getEmployeeById(employeeId)).thenReturn(Optional.empty());

        // When
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", employeeId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(this.employeeService, Mockito.times(1)).getEmployeeById(employeeId);
    }

    @Test
    @DisplayName(value = "guardar empleado en la Base de Datos")
    void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        // Given
        Employee employee = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("martin@email.com")
                .build();

        Mockito.doAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setId(1L);
            return emp;
        }).when(this.employeeService).saveEmployee(Mockito.any(Employee.class));

        Assertions.assertNull(employee.getId());

        // When
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(employee)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(employee.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(employee.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(employee.getEmail()));

        Mockito.verify(this.employeeService, Mockito.times(1))
                .saveEmployee(Mockito.any(Employee.class));
    }

    @Test
    @DisplayName(value = "actualizar empleado")
    void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdateEmployeeObject() throws Exception {
        // Given
        Long employeeId = 1L;
        Employee employeeBD = Employee.builder()
                .id(employeeId)
                .firstName("Martín")
                .lastName("Díaz")
                .email("martin@email.com")
                .build();

        Employee updateEmployee = Employee.builder()
                .firstName("Mart")
                .lastName("Díaz")
                .email("martin@email.com")
                .build();

        Mockito.when(this.employeeService.getEmployeeById(employeeId)).thenReturn(Optional.of(employeeBD));
        Mockito.when(this.employeeService.updateEmployee(Mockito.any(Employee.class)))
                .then(invocation -> invocation.getArgument(0));
        // When
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updateEmployee)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employeeBD.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updateEmployee.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(updateEmployee.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updateEmployee.getEmail()));

        Mockito.verify(this.employeeService, Mockito.times(1)).getEmployeeById(employeeId);
        Mockito.verify(this.employeeService, Mockito.times(1)).updateEmployee(Mockito.any(Employee.class));
    }

    @Test
    @DisplayName(value = "eliminar empleado")
    void deleteEmployee() throws Exception {
        // Given
        Long employeeId = 1L;

        Mockito.doNothing().when(this.employeeService).deleteEmployee(employeeId);

        // When
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", employeeId));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andExpect(MockMvcResultMatchers.jsonPath("$").value("Employee deleted successfully!"));
        Mockito.verify(this.employeeService, Mockito.times(1)).deleteEmployee(employeeId);

    }
}