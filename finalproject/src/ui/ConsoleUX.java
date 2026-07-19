package ui;

import db.DBConnection;
import models.Employee;
import services.EmployeeService;
import services.ReportService;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class ConsoleUX {
    private final EmployeeService employeeService = new EmployeeService();
    private final ReportService reportService = new ReportService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("==========================================================================");
        System.out.println("          WELCOME TO THE COMPANY Z EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("==========================================================================");
        
        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Select an option (1-8): ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    handleSearch();
                    break;
                case "2":
                    handleInsert();
                    break;
                case "3":
                    handleUpdate();
                    break;
                case "4":
                    handleDelete();
                    break;
                case "5":
                    handleSalaryRangeUpdate();
                    break;
                case "6":
                    handleSalaryBelowThresholdUpdate();
                    break;
                case "7":
                    handleReportsMenu();
                    break;
                case "8":
                    running = false;
                    System.out.println("Exiting the system. Thank you for using Company Z EMS!");
                    break;
                default:
                    System.out.println("Invalid selection. Please enter a number between 1 and 8.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("1. Search Employee (by Name, SSN, or ID)");
        System.out.println("2. Add New Employee");
        System.out.println("3. Update Employee Data");
        System.out.println("4. Delete Employee");
        System.out.println("5. Batch Update Salary by Percentage Range (Annual Salary)");
        System.out.println("6. Batch Update Salary Below Threshold (Annual Salary)");
        System.out.println("7. Generate Reports");
        System.out.println("8. Exit");
    }

    private void handleSearch() {
        System.out.println("\n--- SEARCH EMPLOYEE ---");
        System.out.print("Enter name, SSN, or Employee ID to search: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }

        List<Employee> results = employeeService.searchEmployees(query);
        if (results.isEmpty()) {
            System.out.println("No matching employee found.");
        } else {
            System.out.printf("Found %d matching record(s):\n", results.size());
            for (Employee emp : results) {
                System.out.println("\n" + emp.getFormattedInfo());
            }
        }
    }

    private void handleInsert() {
        System.out.println("\n--- ADD NEW EMPLOYEE ---");
        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("SSN (9 digits, no dashes): ");
        String ssn = scanner.nextLine().trim();
        if (ssn.length() != 9 || !ssn.matches("\\d+")) {
            System.out.println("Invalid SSN. SSN must be exactly 9 digits with no dashes.");
            return;
        }

        System.out.print("Age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Must be a number.");
            return;
        }

        System.out.print("Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Employment Type (Full-Time / Part-Time): ");
        String empTypeInput = scanner.nextLine().trim();
        String empType;
        if (empTypeInput.equalsIgnoreCase("full-time") || empTypeInput.equalsIgnoreCase("fulltime")) {
            empType = "Full-Time";
        } else if (empTypeInput.equalsIgnoreCase("part-time") || empTypeInput.equalsIgnoreCase("parttime")) {
            empType = "Part-Time";
        } else {
            System.out.println("Invalid employment type. Must be 'Full-Time' or 'Part-Time'.");
            return;
        }

        double salary;
        int hoursWorked = 0;
        if (empType.equals("Full-Time")) {
            System.out.print("Monthly Salary: ");
            try {
                salary = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid monthly salary.");
                return;
            }
        } else {
            System.out.print("Hourly Wage: ");
            try {
                salary = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid hourly wage.");
                return;
            }
            System.out.print("Hours Worked per Week: ");
            try {
                hoursWorked = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid hours worked.");
                return;
            }
        }

        printDivisionsList();
        System.out.print("Select Division ID: ");
        int divisionId;
        try {
            divisionId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid division ID.");
            return;
        }

        printJobsList();
        System.out.print("Select Job Title ID: ");
        int jobId;
        try {
            jobId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid job ID.");
            return;
        }

        boolean success = employeeService.insertEmployee(firstName, lastName, ssn, age, address, salary, hoursWorked, empType, jobId, divisionId);
        if (success) {
            System.out.println("Employee added successfully!");
        } else {
            System.out.println("Failed to add employee.");
        }
    }

    private void handleUpdate() {
        System.out.println("\n--- UPDATE EMPLOYEE DATA ---");
        System.out.print("Enter the Employee ID to update: ");
        String query = scanner.nextLine().trim();
        List<Employee> results = employeeService.searchEmployees(query);
        if (results.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }
        
        Employee emp = results.get(0);
        System.out.println("Updating details for: " + emp.getName());
        
        System.out.print("First Name [" + emp.getName().split(" ")[0] + "]: ");
        String firstName = scanner.nextLine().trim();
        if (firstName.isEmpty()) firstName = emp.getName().split(" ")[0];

        System.out.print("Last Name [" + emp.getName().split(" ")[1] + "]: ");
        String lastName = scanner.nextLine().trim();
        if (lastName.isEmpty()) lastName = emp.getName().split(" ")[1];

        System.out.print("SSN [" + emp.getSsn() + "]: ");
        String ssn = scanner.nextLine().trim();
        if (ssn.isEmpty()) {
            ssn = emp.getSsn();
        } else if (ssn.length() != 9 || !ssn.matches("\\d+")) {
            System.out.println("Invalid SSN. SSN must be exactly 9 digits with no dashes.");
            return;
        }

        System.out.print("Age [" + emp.getAge() + "]: ");
        String ageStr = scanner.nextLine().trim();
        int age = ageStr.isEmpty() ? emp.getAge() : Integer.parseInt(ageStr);

        System.out.print("Address [" + emp.getAddress() + "]: ");
        String address = scanner.nextLine().trim();
        if (address.isEmpty()) address = emp.getAddress();

        System.out.print("Employment Type [" + emp.getEmploymentType() + "] (Full-Time / Part-Time): ");
        String empTypeInput = scanner.nextLine().trim();
        String empType = emp.getEmploymentType();
        if (!empTypeInput.isEmpty()) {
            if (empTypeInput.equalsIgnoreCase("full-time") || empTypeInput.equalsIgnoreCase("fulltime")) {
                empType = "Full-Time";
            } else if (empTypeInput.equalsIgnoreCase("part-time") || empTypeInput.equalsIgnoreCase("parttime")) {
                empType = "Part-Time";
            } else {
                System.out.println("Invalid employment type.");
                return;
            }
        }

        double salary = emp.getSalary();
        int hoursWorked = 0;
        if (emp instanceof models.PartTimeEmployee) {
            hoursWorked = ((models.PartTimeEmployee) emp).getHoursWorkedPerWeek();
        }

        if (empType.equals("Full-Time")) {
            System.out.print("Monthly Salary [" + emp.getSalary() + "]: ");
            String salStr = scanner.nextLine().trim();
            if (!salStr.isEmpty()) salary = Double.parseDouble(salStr);
        } else {
            System.out.print("Hourly Wage [" + emp.getSalary() + "]: ");
            String salStr = scanner.nextLine().trim();
            if (!salStr.isEmpty()) salary = Double.parseDouble(salStr);

            System.out.print("Hours Worked per Week [" + hoursWorked + "]: ");
            String hrStr = scanner.nextLine().trim();
            if (!hrStr.isEmpty()) hoursWorked = Integer.parseInt(hrStr);
        }

        printDivisionsList();
        System.out.print("Select Division ID [" + emp.getDivisionId() + "]: ");
        String divStr = scanner.nextLine().trim();
        int divisionId = divStr.isEmpty() ? emp.getDivisionId() : Integer.parseInt(divStr);

        printJobsList();
        System.out.print("Select Job Title ID [" + emp.getJobId() + "]: ");
        String jobStr = scanner.nextLine().trim();
        int jobId = jobStr.isEmpty() ? emp.getJobId() : Integer.parseInt(jobStr);

        boolean success = employeeService.updateEmployee(emp.getEmpId(), firstName, lastName, ssn, age, address, salary, hoursWorked, empType, jobId, divisionId);
        if (success) {
            System.out.println("Employee updated successfully!");
        } else {
            System.out.println("Failed to update employee.");
        }
    }

    private void handleDelete() {
        System.out.println("\n--- DELETE EMPLOYEE ---");
        System.out.print("Enter Employee ID to delete: ");
        int empId;
        try {
            empId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        List<Employee> results = employeeService.searchEmployees(String.valueOf(empId));
        if (results.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }

        Employee emp = results.get(0);
        System.out.print("Are you sure you want to delete employee " + emp.getName() + "? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            boolean success = employeeService.deleteEmployee(empId);
            if (success) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void handleSalaryRangeUpdate() {
        System.out.println("\n--- BATCH UPDATE SALARY BY RANGE ---");
        System.out.print("Enter percentage increase (e.g. 3.2): ");
        double percentage;
        try {
            percentage = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid percentage.");
            return;
        }

        System.out.print("Enter minimum annual salary threshold (e.g. 58000): ");
        double minAnnual;
        try {
            minAnnual = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        System.out.print("Enter maximum annual salary threshold (e.g. 105000): ");
        double maxAnnual;
        try {
            maxAnnual = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        int affected = employeeService.updateSalariesByPercentageRange(percentage, minAnnual, maxAnnual);
        System.out.printf("Salary update complete. %d employees' salaries were adjusted.\n", affected);
    }

    private void handleSalaryBelowThresholdUpdate() {
        System.out.println("\n--- BATCH UPDATE SALARY BELOW THRESHOLD ---");
        System.out.print("Enter percentage increase (e.g. 5.0): ");
        double percentage;
        try {
            percentage = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid percentage.");
            return;
        }

        System.out.print("Enter maximum annual salary threshold (employees making LESS than this will be updated): ");
        double thresholdAnnual;
        try {
            thresholdAnnual = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid threshold.");
            return;
        }

        int affected = employeeService.updateSalariesBelowThreshold(percentage, thresholdAnnual);
        System.out.printf("Salary update complete. %d employees' salaries were adjusted.\n", affected);
    }

    private void handleReportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\nREPORTS MENU");
            System.out.println("1. Report 1: Full-time employees information with pay statement history");
            System.out.println("2. Report 2: Total pay for a month by job title");
            System.out.println("3. Report 3: Total pay for a month by Division");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select an option (1-4): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    reportService.generateFullTimeEmployeeHistoryReport();
                    break;
                case "2": {
                    System.out.print("Enter month (1-12): ");
                    int m = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter year (YYYY): ");
                    int y = Integer.parseInt(scanner.nextLine().trim());
                    reportService.generateTotalPayByJobTitleReport(m, y);
                    break;
                }
                case "3": {
                    System.out.print("Enter month (1-12): ");
                    int m = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter year (YYYY): ");
                    int y = Integer.parseInt(scanner.nextLine().trim());
                    reportService.generateTotalPayByDivisionReport(m, y);
                    break;
                }
                case "4":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    private void printDivisionsList() {
        System.out.println("Available Divisions:");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM divisions ORDER BY division_id")) {
            while (rs.next()) {
                System.out.printf("  [%d] %s\n", rs.getInt("division_id"), rs.getString("division_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading divisions: " + e.getMessage());
        }
    }

    private void printJobsList() {
        System.out.println("Available Jobs:");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM jobs ORDER BY job_id")) {
            while (rs.next()) {
                System.out.printf("  [%d] %s\n", rs.getInt("job_id"), rs.getString("job_title"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading jobs: " + e.getMessage());
        }
    }
}
