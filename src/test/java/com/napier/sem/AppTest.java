package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

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

    @Test
    void printSalariesTestNull()
    {
        app.printSalaries(null);
    }

    @Test
    void printSalariesTestEmpty()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        app.printSalaries(employees);
    }
    @Test
    void printSalariesTestContainsNull()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        employees.add(null);
        app.printSalaries(employees);
    }
    @Test
    void printSalaries()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.title = "Engineer";
        emp.salary = 55000;
        employees.add(emp);
        app.printSalaries(employees);
    }
    @Test
    void displayEmployeeTestNull()
    {
        app.displayEmployee(null);
    }

    @Test
    void displayEmployeeTestValid()
    {
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "John";
        emp.last_name = "Doe";
        emp.title = "Engineer";
        emp.salary = 60000;
        emp.dept_name = "IT";
        emp.manager = "Alice";

        app.displayEmployee(emp);
    }

    @Test
    void displayEmployeeTestMissingFields()
    {
        Employee emp = new Employee();
        emp.emp_no = 2;
        // Missing first_name, last_name, title, dept_name, manager

        app.displayEmployee(emp);
    }

    @Test
    void displayEmployeeTestNullFields()
    {
        Employee emp = new Employee();
        emp.emp_no = 3;
        emp.first_name = null;
        emp.last_name = null;
        emp.title = null;
        emp.salary = 0;
        emp.dept_name = null;
        emp.manager = null;

        app.displayEmployee(emp);
    }
}