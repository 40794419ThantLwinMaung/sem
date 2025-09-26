package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class App
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 30;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                Thread.sleep(3000); // shorter for testing
                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example"
                );
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
                System.out.println("Disconnected from database");
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Get employee information from database.
     */
    public Employee getEmployee(int ID)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_name, " +
                            "m.first_name AS manager_first, m.last_name AS manager_last " +
                            "FROM employees e " +
                            "LEFT JOIN titles t ON e.emp_no = t.emp_no " +
                            "LEFT JOIN salaries s ON e.emp_no = s.emp_no " +
                            "LEFT JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "LEFT JOIN departments d ON de.dept_no = d.dept_no " +
                            "LEFT JOIN dept_manager dm ON de.dept_no = dm.dept_no " +
                            "LEFT JOIN employees m ON dm.emp_no = m.emp_no " +
                            "WHERE e.emp_no = " + ID + " " +
                            "ORDER BY s.to_date DESC LIMIT 1;";

            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");

                String managerFirst = rset.getString("manager_first");
                String managerLast = rset.getString("manager_last");
                if (managerFirst != null && managerLast != null)
                {
                    Employee mgr = new Employee();
                    mgr.first_name = managerFirst;
                    mgr.last_name = managerLast;
                    emp.manager = mgr;
                }
                else
                {
                    emp.manager = null;
                }
                return emp;
            }
            else
            {
                System.out.println("Employee not found");
                return null;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Display employee information to the console.
     */
    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary: " + emp.salary + "\n"
                            + emp.dept_name
            );
            if (emp.manager != null)
                System.out.println("Manager: " + emp.manager.first_name + " " + emp.manager.last_name);
            else
                System.out.println("Manager: N/A");
        }
        else
        {
            System.out.println("No employee data to display");
        }
    }

    /**
     * Get salary information by role.
     */
    public void getSalariesByRole(String title)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                            "FROM employees e, salaries s, titles t " +
                            "WHERE e.emp_no = s.emp_no " +
                            "AND e.emp_no = t.emp_no " +
                            "AND s.to_date = '9999-01-01' " +
                            "AND t.to_date = '9999-01-01' " +
                            "AND t.title = '" + title + "' " +
                            "ORDER BY e.emp_no ASC;";

            ResultSet rset = stmt.executeQuery(strSelect);

            System.out.println("Salaries for role: " + title);
            while (rset.next())
            {
                System.out.printf("%-8d%-15s%-15s%-8d\n",
                        rset.getInt("emp_no"),
                        rset.getString("first_name"),
                        rset.getString("last_name"),
                        rset.getInt("salary"));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by role");
        }
    }

    /**
     * Get a department by its name.
     */
    public Department getDepartment(String deptName)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT d.dept_no, d.dept_name, " +
                            "m.emp_no AS manager_no, m.first_name AS manager_first, m.last_name AS manager_last " +
                            "FROM departments d " +
                            "LEFT JOIN dept_manager dm ON d.dept_no = dm.dept_no " +
                            "LEFT JOIN employees m ON dm.emp_no = m.emp_no " +
                            "WHERE d.dept_name = '" + deptName + "';";

            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next())
            {
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");

                String managerFirst = rset.getString("manager_first");
                String managerLast = rset.getString("manager_last");
                if (managerFirst != null && managerLast != null)
                {
                    Employee mgr = new Employee();
                    mgr.emp_no = rset.getInt("manager_no");
                    mgr.first_name = managerFirst;
                    mgr.last_name = managerLast;
                    dept.manager = mgr;
                }
                else
                {
                    dept.manager = null;
                }
                return dept;
            }
            else
            {
                System.out.println("Department not found: " + deptName);
                return null;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department");
            return null;
        }
    }

    /**
     * Get all employee salaries for a given department.
     */
    public ArrayList<Employee> getSalariesByDepartment(Department dept)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                            "FROM employees e, salaries s, dept_emp de " +
                            "WHERE e.emp_no = s.emp_no " +
                            "AND e.emp_no = de.emp_no " +
                            "AND s.to_date = '9999-01-01' " +
                            "AND de.dept_no = '" + dept.dept_no + "' " +
                            "ORDER BY e.emp_no ASC;";

            ResultSet rset = stmt.executeQuery(strSelect);

            ArrayList<Employee> employees = new ArrayList<>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                emp.dept = dept;
                emp.manager = dept.manager;
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by department");
            return null;
        }
    }

    /**
     * Prints a list of employees with their salaries.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        if (employees == null || employees.isEmpty())
        {
            System.out.println("No salary data available.");
            return;
        }

        System.out.println(String.format("%-10s %-15s %-20s %-8s",
                "Emp No", "First Name", "Last Name", "Salary"));

        for (Employee emp : employees)
        {
            String emp_string = String.format("%-10s %-15s %-20s %-8s",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    public static void main(String[] args)
    {
        App a = new App();
        a.connect();

        // Example: Get one employee by ID
        Employee emp = a.getEmployee(255530);
        a.displayEmployee(emp);

        // Example: Get salaries for a role
        a.getSalariesByRole("Engineer");

        // Example: Get department and salaries
        Department dept = a.getDepartment("Sales");
        if (dept != null)
        {
            System.out.println("Department: " + dept.dept_name + " (" + dept.dept_no + ")");
            if (dept.manager != null)
                System.out.println("Manager: " + dept.manager.first_name + " " + dept.manager.last_name);
            else
                System.out.println("Manager: N/A");

            ArrayList<Employee> employees = a.getSalariesByDepartment(dept);
            a.printSalaries(employees);
        }

        a.disconnect();
    }
}
