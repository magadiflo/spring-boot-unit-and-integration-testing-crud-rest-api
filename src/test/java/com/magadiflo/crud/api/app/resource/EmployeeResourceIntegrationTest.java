package com.magadiflo.crud.api.app.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magadiflo.crud.api.app.entities.Employee;
import com.magadiflo.crud.api.app.repository.IEmployeeRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

/**
 * PRUEBAS DE INTEGRACIÓN
 * **********************
 */

/**
 * @SpringBootTest :
 * *****************
 * Spring Boot proporciona esta anotación para las pruebas de integración.
 * Esta anotación crea un contexto de aplicación y carga el contexto
 * completo de la aplicación.
 * <br>
 * @SpringBootTest arranca el contexto completo de la aplicación, lo que significa
 * que podemos usar el @Autowired para poder usar inyección de dependencia.
 * <br>
 * Inicia el servidor embebido, crea un entorno web y, a continuación,
 * permite a los métodos @Test realizar pruebas de integración.
 * <br>
 * De forma predeterminada, @SpringBootTest no inicia un servidor. Necesitamos agregar
 * el atributo webEnvironment para refinar aún más cómo se ejecutan sus pruebas.
 * Tiene varias opciones:
 * <br>
 * - MOCK (predeterminado): carga un contexto de aplicación web y proporciona un entorno web simulado.
 * - RANDOM_PORT: carga un WebServerApplicationContext y proporciona un entorno web real. El servidor
 *   embebido se inicia y escucha en un puerto aleatorio. Este es el que se debe utilizar para la
 *   prueba de integración.
 * - DEFINED_PORT: carga un WebServerApplicationContext y proporciona un entorno web real.
 * - NONE: carga un ApplicationContext usando SpringApplication, pero no proporciona ningún entorno web.
 */

/**
 * @AutoConfigureMockMvc :
 * ***********************
 * Nos va a permitir añadir un MockMvc a nuestro ApplicationContext, de esta manera podremos realizar
 * peticiones HTTP contra nuestro controlador.
 * <br>
 * NOTA: En el curso de Andres Guzmán (para las pruebas de integración) usamos tanto el RestTemplate como el WebClient
 * para poder hacer las peticiones HTTP a nuestras Apis del controlador, mientras que aquí se está usando
 * MockMvc.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmployeeResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.employeeRepository.deleteAll();
    }

    @Test
    void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        // given - precondición o configuración
        List<Employee> listOfEmployees = Arrays.asList(
                Employee.builder().firstName("Martín").lastName("Díaz").email("martin@gmail.com").build(),
                Employee.builder().firstName("Gabriel").lastName("Flores").email("gabriel@gmail.com").build(),
                Employee.builder().firstName("Alejandra").lastName("Casanova").email("alejandra@gmail.com").build(),
                Employee.builder().firstName("Rosita").lastName("Pardo").email("rosita@gmail.com").build(),
                Employee.builder().firstName("Eli").lastName("Tello").email("eli@gmail.com").build()
        );
        this.employeeRepository.saveAll(listOfEmployees);

        // when - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees"));

        // then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(listOfEmployees.size())));

    }

    // Escenario positivo
    @Test
    void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        // given - precondición o configuración
        Employee employee = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("magadiflo@gmail.com")
                .build();
        this.employeeRepository.save(employee);

        // when - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", employee.getId()));

        // then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(employee.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is(employee.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(employee.getEmail())));
    }

    // Escenario negativo
    @Test
    void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {
        // given - precondición o configuración
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("magadiflo@gmail.com")
                .build();
        this.employeeRepository.save(employee);
        this.employeeRepository.deleteById(employee.getId());
        employee.setId(null);
        this.employeeRepository.save(employee);

        // when - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", employeeId));

        // then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        // given - precondición o configuración
        Employee employee = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("magadiflo@gmail.com")
                .build();

        // when - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(employee)));

        // then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(employee.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is(employee.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(employee.getEmail())));
    }

    @Test
    void givenEmployeeWithNewData_whenUpdateEmployee_thenReturnEmployeeWithNewDadaObject() throws Exception {
        // given - precondición o configuración
        Employee employeeBD = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("magadiflo@gmail.com")
                .build();
        this.employeeRepository.save(employeeBD);

        Employee employeeWithNewData = Employee.builder()
                .firstName("Gaspi")
                .lastName("Florecilla")
                .email("gaspi_florecilla@gmail.com")
                .build();

        // when - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/employees/{id}", employeeBD.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(employeeWithNewData)));


        // then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(employeeWithNewData.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is(employeeWithNewData.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(employeeWithNewData.getEmail())));
    }

    @Test
    void deleteEmployee() throws Exception {
        // given - precondición o configuración
        Employee employee = Employee.builder()
                .firstName("Martín")
                .lastName("Díaz")
                .email("magadiflo@gmail.com")
                .build();
        this.employeeRepository.save(employee);

        // when - acción o el comportamiento que vamos a probar
        ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", employee.getId()));

        // then - verificar la salida
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value("Employee deleted successfully!"));
    }
}