package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class App
{
    private Connection con = null;

    // Connect to database
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " +                                  Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

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

    // Get employee by ID
    public Employee getEmployee(int ID)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_no, d.dept_name, " +
                            "m.emp_no AS manager_no, m.first_name AS manager_first, m.last_name AS manager_last " +
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

                // Department
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");

                // Manager
                Employee mgr = null;
                String managerFirst = rset.getString("manager_first");
                String managerLast = rset.getString("manager_last");
                if (managerFirst != null && managerLast != null)
                {
                    mgr = new Employee();
                    mgr.emp_no = rset.getInt("manager_no");
                    mgr.first_name = managerFirst;
                    mgr.last_name = managerLast;
                }
                dept.manager = mgr;
                emp.manager = mgr;
                emp.dept = dept;

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

    // Get employee by first and last name
    public Employee getEmployee(String firstName, String lastName)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_no, d.dept_name, " +
                            "m.emp_no AS manager_no, m.first_name AS manager_first, m.last_name AS manager_last " +
                            "FROM employees e " +
                            "LEFT JOIN titles t ON e.emp_no = t.emp_no " +
                            "LEFT JOIN salaries s ON e.emp_no = s.emp_no " +
                            "LEFT JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "LEFT JOIN departments d ON de.dept_no = d.dept_no " +
                            "LEFT JOIN dept_manager dm ON de.dept_no = dm.dept_no " +
                            "LEFT JOIN employees m ON dm.emp_no = m.emp_no " +
                            "WHERE e.first_name = '" + firstName + "' AND e.last_name = '" + lastName + "' " +
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

                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");

                Employee mgr = null;
                String managerFirst = rset.getString("manager_first");
                String managerLast = rset.getString("manager_last");
                if (managerFirst != null && managerLast != null)
                {
                    mgr = new Employee();
                    mgr.emp_no = rset.getInt("manager_no");
                    mgr.first_name = managerFirst;
                    mgr.last_name = managerLast;
                }
                dept.manager = mgr;
                emp.manager = mgr;
                emp.dept = dept;

                return emp;
            }
            else
            {
                System.out.println("Employee not found: " + firstName + " " + lastName);
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

    // Get department by name
    public Department getDepartment(String deptName)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT d.dept_no, d.dept_name, m.emp_no AS manager_no, m.first_name AS manager_first, m.last_name AS manager_last " +
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

                Employee mgr = null;
                String managerFirst = rset.getString("manager_first");
                String managerLast = rset.getString("manager_last");
                if (managerFirst != null && managerLast != null)
                {
                    mgr = new Employee();
                    mgr.emp_no = rset.getInt("manager_no");
                    mgr.first_name = managerFirst;
                    mgr.last_name = managerLast;
                }
                dept.manager = mgr;
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

    // Get salaries by role
    public ArrayList<Employee> getSalariesByRole(String title) {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_name, dm.emp_no AS manager_no, m.first_name AS manager_first, m.last_name AS manager_last " +
                            "FROM employees e " +
                            "JOIN titles t ON e.emp_no = t.emp_no " +
                            "JOIN salaries s ON e.emp_no = s.emp_no " +
                            "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "JOIN departments d ON de.dept_no = d.dept_no " +
                            "JOIN dept_manager dm ON d.dept_no = dm.dept_no " +
                            "LEFT JOIN employees m ON dm.emp_no = m.emp_no " +
                            "WHERE s.to_date = '9999-01-01' AND t.to_date = '9999-01-01' AND t.title = '" + title + "' " +
                            "ORDER BY e.emp_no ASC;";

            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");

                Employee mgr = null;
                String managerFirst = rset.getString("manager_first");
                String managerLast = rset.getString("manager_last");
                if (managerFirst != null && managerLast != null) {
                    mgr = new Employee();
                    mgr.emp_no = rset.getInt("manager_no");
                    mgr.first_name = managerFirst;
                    mgr.last_name = managerLast;
                }

                emp.manager = mgr;

                Department dept = new Department();
                dept.dept_name = rset.getString("dept_name");
                dept.manager = mgr;
                emp.dept = dept;

                employees.add(emp);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by role");
        }
        return employees;
    }


    // Get salaries by department
    public ArrayList<Employee> getSalariesByDepartment(Department dept)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                            "FROM employees e, salaries s, dept_emp de, departments d " +
                            "WHERE e.emp_no = s.emp_no AND e.emp_no = de.emp_no " +
                            "AND de.dept_no = d.dept_no AND s.to_date = '9999-01-01' " +
                            "AND d.dept_no = '" + dept.dept_no + "' " +
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
                emp.dept_name = dept.dept_name;
                emp.manager = dept.manager;
                emp.dept = dept;
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

    // Print salaries
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
            System.out.println(String.format("%-10s %-15s %-20s %-8s",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary));
        }
    }
    /**
     * Display employee information to the console.
     * @param emp Employee object
     */
    public void displayEmployee(Employee emp)
    {
        if (emp == null) {
            System.out.println("No employee data to display");
            return;
        }

        System.out.printf(
                "Emp No: %d\nName: %s %s\nTitle: %s\nSalary: %d\nDepartment: %s\nManager: %s\n",
                emp.emp_no,
                emp.first_name,
                emp.last_name,
                emp.title,
                emp.salary,
                (emp.dept != null ? emp.dept.dept_name : emp.dept_name),
                (emp.manager != null ? emp.manager.first_name + " " + emp.manager.last_name : "N/A")
        );
    }

    public void addEmployee(Employee emp)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strUpdate =
                    "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
                            "VALUES (" + emp.emp_no + ", '" + emp.first_name + "', '" + emp.last_name + "', " +
                            "'9999-01-01', 'M', '9999-01-01')";
            stmt.execute(strUpdate);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to add employee");
        }
    }
    public void outputEmployees(ArrayList<Employee> employees, String filename) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees to output");
            return;
        }

        StringBuilder sb = new StringBuilder();

        // Markdown table header
        sb.append("| Emp No | First Name | Last Name | Title | Salary | Department | Manager |\n");
        sb.append("| --- | --- | --- | --- | --- | --- | --- |\n");

        // Loop over employees and append rows
        for (Employee emp : employees) {
            if (emp == null) continue;

            String managerName = (emp.manager != null) ? emp.manager.first_name + " " + emp.manager.last_name : "N/A";

            sb.append("| ")
                    .append(emp.emp_no).append(" | ")
                    .append(emp.first_name).append(" | ")
                    .append(emp.last_name).append(" | ")
                    .append(emp.title).append(" | ")
                    .append(emp.salary).append(" | ")
                    .append(emp.dept_name).append(" | ")
                    .append(managerName)
                    .append(" |\n");
        }

        // Create reports directory if it doesn't exist
        try {
            File dir = new File("./reports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir, filename)));
            writer.write(sb.toString());
            writer.close();
            System.out.println("Report generated: ./reports/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write report");
        }
    }
    public static void main(String[] args) {
        // Create new Application and connect to database
        App app = new App();

        if (args.length < 1) {
            app.connect("localhost:33060", 30000);
        } else {
            app.connect(args[0], Integer.parseInt(args[1]));
        }

        // Example: Get salaries by role 'Manager' and output to markdown
        ArrayList<Employee> employees = app.getSalariesByRole("Manager");
        app.outputEmployees(employees, "ManagerSalaries.md");

        // Optionally, you can still print to console
        app.printSalaries(employees);

        // Disconnect from database
        app.disconnect();
    }

}
