package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;

@SpringBootApplication
@RestController
public class App
{
    private static Connection con = null;

    // Connect to database
    public static void connect(String location, int delay) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                Thread.sleep(delay);
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    public static void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Disconnected from database");
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Get employee by ID (REST endpoint).
     */
    @RequestMapping("employee")
    public Employee getEmployee(@RequestParam(value = "id") String ID) {
        try {
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

            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");
                return emp;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Get employee by name (REST endpoint).
     */
    @RequestMapping("employee_name")
    public Employee getEmployeeByName(@RequestParam(value = "first") String firstName,
                                      @RequestParam(value = "last") String lastName) {
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_no, d.dept_name " +
                            "FROM employees e " +
                            "LEFT JOIN titles t ON e.emp_no = t.emp_no " +
                            "LEFT JOIN salaries s ON e.emp_no = s.emp_no " +
                            "LEFT JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "LEFT JOIN departments d ON de.dept_no = d.dept_no " +
                            "WHERE e.first_name = '" + firstName + "' AND e.last_name = '" + lastName + "' " +
                            "ORDER BY s.to_date DESC LIMIT 1;";

            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");
                return emp;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Get department by name (REST endpoint).
     */
    @RequestMapping("department")
    public Department getDepartment(@RequestParam(value = "dept") String deptName) {
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT d.dept_no, d.dept_name " +
                            "FROM departments d " +
                            "WHERE d.dept_name = '" + deptName + "';";

            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next()) {
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                return dept;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Get salaries by role/title (REST endpoint).
     */
    @RequestMapping("salaries_title")
    public ArrayList<Employee> getSalariesByTitle(@RequestParam(value = "title") String title) {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, t.title, s.salary, d.dept_name " +
                            "FROM employees e " +
                            "JOIN titles t ON e.emp_no = t.emp_no " +
                            "JOIN salaries s ON e.emp_no = s.emp_no " +
                            "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "JOIN departments d ON de.dept_no = d.dept_no " +
                            "WHERE s.to_date = '9999-01-01' AND t.to_date = '9999-01-01' " +
                            "AND t.title = '" + title + "' " +
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
                employees.add(emp);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return employees;
    }

    /**
     * Get salaries by department (REST endpoint).
     */
    @RequestMapping("salaries_department")
    public ArrayList<Employee> getSalariesByDepartment(@RequestParam(value = "dept") String deptNo) {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                            "FROM employees e, salaries s, dept_emp de, departments d " +
                            "WHERE e.emp_no = s.emp_no AND e.emp_no = de.emp_no " +
                            "AND de.dept_no = d.dept_no AND s.to_date = '9999-01-01' " +
                            "AND d.dept_no = '" + deptNo + "' " +
                            "ORDER BY e.emp_no ASC;";

            ResultSet rset = stmt.executeQuery(strSelect);
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return employees;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            connect("localhost:33060", 30000);
        } else {
            connect(args[0], Integer.parseInt(args[1]));
        }
        SpringApplication.run(App.class, args);
    }
}
