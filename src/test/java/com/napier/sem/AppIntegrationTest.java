package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AppIntegrationTest
{
    static App app;

    @BeforeAll
    static void init()
    {
        app = new App();
        app.connect("localhost:33060", 30000);
    }

    @Test
    void testGetEmployeeById()
    {
        Employee emp = app.getEmployee(255530);
        assertNotNull(emp);
        assertEquals(255530, emp.emp_no);
        assertEquals("Ronghao", emp.first_name);
        assertEquals("Garigliano", emp.last_name);
    }

    @Test
    void testGetEmployeeByName()
    {
        Employee emp = app.getEmployee("Ronghao", "Garigliano");
        assertNotNull(emp);
        assertEquals(255530, emp.emp_no);
    }

    @Test
    void testAddEmployee()
    {
        Employee emp = new Employee();
        emp.emp_no = 500000;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        app.addEmployee(emp);

        Employee added = app.getEmployee(500000);
        assertNotNull(added);
        assertEquals(500000, added.emp_no);
        assertEquals("Kevin", added.first_name);
        assertEquals("Chalmers", added.last_name);
    }

    @Test
    void testGetDepartment()
    {
        Department dept = app.getDepartment("Sales");
        assertNotNull(dept);
        assertEquals("Sales", dept.dept_name);
        assertNotNull(dept.manager);
    }

    @Test
    void testGetSalariesByRole()
    {
        ArrayList<Employee> employees = app.getSalariesByRole("Engineer");
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        assertEquals("Engineer", employees.get(0).title);
    }

    @Test
    void testGetSalariesByDepartment()
    {
        Department dept = app.getDepartment("Sales");
        assertNotNull(dept);

        ArrayList<Employee> employees = app.getSalariesByDepartment(dept);
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        assertEquals("Sales", employees.get(0).dept_name);
    }

    @Test
    void testPrintSalaries()
    {
        ArrayList<Employee> employees = app.getSalariesByRole("Engineer");
        app.printSalaries(employees);   // should print table
        app.printSalaries(null);        // should print "No salary data available."
    }

    @Test
    void testDisplayEmployee()
    {
        Employee emp = app.getEmployee(255530);
        app.displayEmployee(emp);   // should print valid employee
        app.displayEmployee(null);  // should print "No employee data to display"
    }

    @Test
    void testDisconnect()
    {
        app.disconnect();
        // Just ensures disconnect runs without exception
    }
}
