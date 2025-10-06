package web;

/**
 * Represents a department
 */
public class Department
{
    /**
     * Department number (e.g., "d001")
     */
    public String dept_no;

    /**
     * Department name (e.g., "Sales")
     */
    public String dept_name;

    /**
     * Department manager (name as String for now, can later be changed to Employee)
     */
    public Employee manager;
}
