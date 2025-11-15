package com.napier.sem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

@SpringBootApplication
@RestController
public class App
{
    /**
     * Connection to MySQL database.
     */
    private static Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public static void connect(String location, int delay)
    {
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
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public static void disconnect()
    {
        if (con != null)
        {
            try {
                con.close();
                System.out.println("Database connection closed.");
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Get a single employee via URL:
     * http://localhost:8080/employee?id=10002
     */
    @RequestMapping("employee")
    public Employee getEmployee(@RequestParam(value = "id") String ID)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect = "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = " + ID;
            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    // ------------------------------
    // EVERYTHING BELOW HERE IS UNCHANGED
    // ------------------------------

    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }

    public ArrayList<Employee> getAllSalaries()
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect = "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                    "FROM employees, salaries " +
                    "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' " +
                    "ORDER BY employees.emp_no ASC";

            ResultSet rset = stmt.executeQuery(strSelect);
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

    public void printSalaries(ArrayList<Employee> employees)
    {
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }

        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        for (Employee emp : employees)
        {
            if (emp == null) continue;
            String emp_string = String.format("%-10s %-15s %-20s %-8s",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    public void addEmployee(Employee emp)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strUpdate = "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
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

    public static ArrayList<Employee> getSalariesByRole(String role) {
        try {
            String sql = "SELECT employees.emp_no, employees.first_name, employees.last_name, " +
                    "titles.title, salaries.salary, departments.dept_name, dept_manager.emp_no AS manager_emp_no " +
                    "FROM employees, salaries, titles, departments, dept_emp, dept_manager " +
                    "WHERE employees.emp_no = salaries.emp_no " +
                    "AND salaries.to_date = '9999-01-01' " +
                    "AND titles.emp_no = employees.emp_no " +
                    "AND titles.to_date = '9999-01-01' " +
                    "AND dept_emp.emp_no = employees.emp_no " +
                    "AND dept_emp.to_date = '9999-01-01' " +
                    "AND departments.dept_no = dept_emp.dept_no " +
                    "AND dept_manager.dept_no = dept_emp.dept_no " +
                    "AND dept_manager.to_date = '9999-01-01' " +
                    "AND titles.title = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, role);

            ResultSet rset = pstmt.executeQuery();
            ArrayList<Employee> employees = new ArrayList<Employee>();

            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");
                emp.manager = String.valueOf(rset.getInt("manager_emp_no"));
                employees.add(emp);
            }

            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public static void outputEmployees(ArrayList<Employee> employees, String filename) {
        if (employees == null) {
            System.out.println("No employees");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("| Emp No | First Name | Last Name | Title | Salary | Department | Manager |\r\n");
        sb.append("| --- | --- | --- | --- | --- | --- | --- |\r\n");

        for (Employee emp : employees) {
            if (emp == null) continue;
            sb.append("| " + emp.emp_no + " | " +
                    emp.first_name + " | " +
                    emp.last_name + " | " +
                    emp.title + " | " +
                    emp.salary + " | " +
                    emp.dept_name + " | " +
                    emp.manager + " |\r\n");
        }

        try {
            new File("./reports/").mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + filename)));
            writer.write(sb.toString());
            writer.close();
            System.out.println("Markdown report generated: ./reports/" + filename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to write report");
        }
    }

    /**
     * Start Spring Boot app
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            connect("localhost:33060,", 3000);
        } else {
            connect(args[0], 3000);
        }

        SpringApplication.run(App.class, args);
    }
}
