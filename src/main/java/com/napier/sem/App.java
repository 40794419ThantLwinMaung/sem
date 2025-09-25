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
            // Load Database driver
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
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
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
     * @param ID Employee ID to lookup
     * @return Employee object or null if not found
     */
    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // SQL query to fetch all relevant employee info
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

            // Execute SQL statement
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
                emp.manager = (managerFirst != null && managerLast != null) ? managerFirst + " " + managerLast : "N/A";
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
     * @param emp Employee object
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
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
        else
        {
            System.out.println("No employee data to display");
        }
    }

    /**
     * Get salary information by role.
     * @param title The job title to look up salaries for.
     */
    public void getSalariesByRole(String title)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // SQL query to fetch employee salaries by role
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                            "FROM employees e, salaries s, titles t " +
                            "WHERE e.emp_no = s.emp_no " +
                            "AND e.emp_no = t.emp_no " +
                            "AND s.to_date = '9999-01-01' " +
                            "AND t.to_date = '9999-01-01' " +
                            "AND t.title = '" + title + "' " +
                            "ORDER BY e.emp_no ASC;";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            System.out.println("Salaries for role: " + title);
            while (rset.next())
            {
                // Output the emp_no, first_name, last_name, and salary
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
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries()
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries " +
                            "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' " +
                            "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Extract employee salary information
        ArrayList<Employee> employees = a.getAllSalaries();

        // Test the size of the returned data - should be 240124
        if (employees != null)
            System.out.println(employees.size());
        else
            System.out.println("No employees returned.");

        // Disconnect from database
        a.disconnect();
    }
}
