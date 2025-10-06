package web; // 1. CORRECT: Test file package is 'web'

// 2. FIX: Remove redundant 'web.*' imports as the class is already in package 'web'
// import web.App;
// import web.Department;
// import web.Employee;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
// 3. FIX: Add missing required Spring annotation import
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// 4. FIX: Remove incorrect @Test annotation. Use @SpringBootTest for test classes.
@SpringBootTest
public class WebAppIntegrationTest {

    // 5. FIX: Use the local App class from the 'web' package
    static App app;

    @BeforeAll
    static void init() {
        app = new App();
        // Adjust delay as needed (30s if MySQL is dockerized and needs startup time)
        app.connect("localhost:33060", 30000);
    }

    @Test
    void testGetEmployeeById() {
        // 6. FIX: Use simple class name 'Employee' from the 'web' package
        Employee emp = app.getEmployee(String.valueOf(10001)); // Sample ID from employees DB
        assertNotNull(emp);
        assertEquals(10001, emp.emp_no);
        assertEquals("Georgi", emp.first_name);
        assertEquals("Facello", emp.last_name);
    }

    @Test
    void testGetEmployeeByName() {
        // 7. FIX: Use simple class name 'Employee' from the 'web' package
        Employee emp = app.getEmployeeByName("Georgi", "Facello");
        assertNotNull(emp);
        assertEquals(10001, emp.emp_no);
    }

    @Test
    void testGetDepartment() {
        // 8. FIX: Use simple class name 'Department' from the 'web' package
        Department dept = app.getDepartment("Sales");
        assertNotNull(dept);
        assertEquals("Sales", dept.dept_name);
    }

    @Test
    void testGetSalariesByRole() {
        // 9. FIX: Use simple class name 'Employee' in ArrayList
        ArrayList<Employee> employees = app.getSalariesByTitle("Engineer");
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        assertEquals("Engineer", employees.get(0).title);
    }

    @Test
    void testGetSalariesByDepartment() {
        Department dept = app.getDepartment("Sales");
        assertNotNull(dept);

        // 10. FIX: Use simple class name 'Employee' in ArrayList
        ArrayList<Employee> employees = app.getSalariesByDepartment(dept.dept_no);
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        assertTrue(employees.get(0).salary > 0);
    }

    @Test
    void testDisconnect() {
        app.disconnect();
        // Just ensures disconnect runs without exception
    }
}