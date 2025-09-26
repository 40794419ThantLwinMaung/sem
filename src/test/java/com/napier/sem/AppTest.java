package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest
{
    static App app;

    @BeforeAll
    static void init()
    {
        app = new App();
    }

    // Existing printSalaries tests
    @Test
    void printSalariesTestNull()
    {
        app.printSalaries(null);
    }

    @Test
    void printSalariesTestEmpty()
    {
        ArrayList<Employee> employees = new ArrayList<>();
        app.printSalaries(employees);
    }

//    @Test
//    void printSalariesTestContainsNull()
//    {
//        ArrayList<Employee> employees = new ArrayList<>();
//        employees.add(null);
//        app.printSalaries(employees);
//    }

    @Test
    void printSalaries()
    {
        ArrayList<Employee> employees = new ArrayList<>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.title = "Engineer";
        emp.salary = 55000;
        employees.add(emp);
        app.printSalaries(employees);
    }

    // New displayEmployee tests
    @Test
    void displayEmployeeTestNull()
    {
        app.displayEmployee(null);
    }

    @Test
    void displayEmployeeTestWithEmployee()
    {
        Employee emp = new Employee();
        emp.emp_no = 2;
        emp.first_name = "Alice";
        emp.last_name = "Smith";
        emp.title = "Manager";
        emp.salary = 75000;
        emp.dept_name = "Sales";
        emp.manager = null;

        app.displayEmployee(emp);
    }

    @Test
    void displayEmployeeTestWithManager()
    {
        Employee manager = new Employee();
        manager.emp_no = 3;
        manager.first_name = "Bob";
        manager.last_name = "Johnson";

        Employee emp = new Employee();
        emp.emp_no = 4;
        emp.first_name = "Charlie";
        emp.last_name = "Brown";
        emp.title = "Developer";
        emp.salary = 60000;
        emp.dept_name = "IT";
        emp.manager = manager;

        app.displayEmployee(emp);
    }
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }
}
