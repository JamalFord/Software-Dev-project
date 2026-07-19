package tests;

import db.DBConnection;
import models.Employee;
import services.EmployeeService;

import java.sql.*;
import java.util.List;

public class SystemTests {
    private static final EmployeeService employeeService = new EmployeeService();

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("                  SYSTEM AUTOMATED TEST RUNNER");
        System.out.println("==========================================================================");

        boolean testAPassed = testUpdateEmployee();
        boolean testBPassed = testSearchEmployee();
        boolean testCPassed = testSalaryThresholdUpdate();

        System.out.println("\n==========================================================================");
        System.out.println("                              TEST RESULTS");
        System.out.println("==========================================================================");
        System.out.println("Test Case A (Update Employee):             " + (testAPassed ? "PASS" : "FAIL"));
        System.out.println("Test Case B (Search Employee):             " + (testBPassed ? "PASS" : "FAIL"));
        System.out.println("Test Case C (Salary Below Threshold):       " + (testCPassed ? "PASS" : "FAIL"));
        System.out.println("==========================================================================");

        if (testAPassed && testBPassed && testCPassed) {
            System.out.println("All system tests completed successfully. (PASS)");
            System.exit(0);
        } else {
            System.out.println("One or more tests failed. (FAIL)");
            System.exit(1);
        }
    }

    // Test Case A: Update employee data
    private static boolean testUpdateEmployee() {
        System.out.println("\n[RUNNING] Test Case A: Update Employee Data");
        
        // 1. Fetch current details of employee #1 (John Doe)
        List<Employee> queryResult = employeeService.searchEmployees("1");
        if (queryResult.isEmpty()) {
            System.out.println("  [FAIL] Seed employee ID 1 (John Doe) not found.");
            return false;
        }
        Employee original = queryResult.get(0);
        System.out.printf("  Original data: Name = %s, Age = %d, Address = %s, Salary = $%,.2f\n", 
                original.getName(), original.getAge(), original.getAddress(), original.getSalary());

        // 2. Perform the update
        System.out.println("  Performing update (Age -> 31, Address -> '404 New Lane', Salary -> 5200.00)...");
        boolean updateSuccess = employeeService.updateEmployee(
                1, "John", "Doe", original.getSsn(), 31, "404 New Lane", 5200.00, 0, "Full-Time", original.getJobId(), original.getDivisionId()
        );
        if (!updateSuccess) {
            System.out.println("  [FAIL] EmployeeService.updateEmployee returned false.");
            return false;
        }

        // 3. Fetch again and verify
        List<Employee> updatedResult = employeeService.searchEmployees("1");
        if (updatedResult.isEmpty()) {
            System.out.println("  [FAIL] Failed to retrieve employee 1 after update.");
            return false;
        }
        Employee updated = updatedResult.get(0);
        System.out.printf("  Updated data: Name = %s, Age = %d, Address = %s, Salary = $%,.2f\n", 
                updated.getName(), updated.getAge(), updated.getAddress(), updated.getSalary());

        boolean success = (updated.getAge() == 31) && 
                          "404 New Lane".equals(updated.getAddress()) && 
                          (updated.getSalary() == 5200.00);

        // 4. Restore original data
        System.out.println("  Restoring original employee data...");
        employeeService.updateEmployee(
                1, "John", "Doe", original.getSsn(), original.getAge(), original.getAddress(), original.getSalary(), 0, "Full-Time", original.getJobId(), original.getDivisionId()
        );

        if (success) {
            System.out.println("  [PASS] Test Case A completed successfully.");
            return true;
        } else {
            System.out.println("  [FAIL] Test Case A data verification failed.");
            return false;
        }
    }

    // Test Case B: Search for employee
    private static boolean testSearchEmployee() {
        System.out.println("\n[RUNNING] Test Case B: Search for Employee");

        // 1. Search by Name
        System.out.println("  1. Searching for name 'Jane'...");
        List<Employee> searchName = employeeService.searchEmployees("Jane");
        if (searchName.isEmpty()) {
            System.out.println("  [FAIL] Could not find any employee with name 'Jane'.");
            return false;
        }
        System.out.printf("     Found: %s (ID: %d)\n", searchName.get(0).getName(), searchName.get(0).getEmpId());

        // 2. Search by SSN
        System.out.println("  2. Searching for SSN '987654321' (Alice Johnson)...");
        List<Employee> searchSsn = employeeService.searchEmployees("987654321");
        if (searchSsn.isEmpty()) {
            System.out.println("  [FAIL] Could not find employee with SSN '987654321'.");
            return false;
        }
        System.out.printf("     Found: %s (ID: %d)\n", searchSsn.get(0).getName(), searchSsn.get(0).getEmpId());

        // 3. Search by ID
        System.out.println("  3. Searching for Employee ID '5' (Charlie Brown)...");
        List<Employee> searchId = employeeService.searchEmployees("5");
        if (searchId.isEmpty()) {
            System.out.println("  [FAIL] Could not find employee with ID 5.");
            return false;
        }
        System.out.printf("     Found: %s (SSN: %s)\n", searchId.get(0).getName(), searchId.get(0).getSsn());

        System.out.println("  [PASS] Test Case B completed successfully.");
        return true;
    }

    // Test Case C: Update salary for all employees less than a particular amount
    private static boolean testSalaryThresholdUpdate() {
        System.out.println("\n[RUNNING] Test Case C: Batch Update Salary Below Threshold");

        double thresholdAnnual = 60000.0; // $60K threshold
        double increasePercentage = 10.0; // 10% increase

        System.out.printf("  Target: 10%% salary increase for employees making less than $%,.2f/year.\n", thresholdAnnual);

        // 1. Get employees below/above threshold before change
        double emp4AnnualBefore = 0;
        double emp5AnnualBefore = 0;

        try (Connection conn = DBConnection.getConnection()) {
            // Fetch Bob Miller (emp_id = 4, makes $4800/mo = $57,600/yr -> below threshold)
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT salary FROM employees WHERE emp_id = 4")) {
                if (rs.next()) emp4AnnualBefore = rs.getDouble("salary") * 12;
            }
            // Fetch Charlie Brown (emp_id = 5, makes $9000/mo = $108,000/yr -> above threshold)
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT salary FROM employees WHERE emp_id = 5")) {
                if (rs.next()) emp5AnnualBefore = rs.getDouble("salary") * 12;
            }
        } catch (SQLException e) {
            System.err.println("  [FAIL] SQL error during pre-check: " + e.getMessage());
            return false;
        }

        System.out.printf("  Before Update: Bob Miller (ID 4) Annual = $%,.2f (Below threshold)\n", emp4AnnualBefore);
        System.out.printf("  Before Update: Charlie Brown (ID 5) Annual = $%,.2f (Above threshold)\n", emp5AnnualBefore);

        // 2. Execute threshold update
        int affected = employeeService.updateSalariesBelowThreshold(increasePercentage, thresholdAnnual);
        System.out.printf("  Executed update. Database reports %d rows modified.\n", affected);

        // 3. Fetch salaries again and verify
        double emp4AnnualAfter = 0;
        double emp5AnnualAfter = 0;

        try (Connection conn = DBConnection.getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT salary FROM employees WHERE emp_id = 4")) {
                if (rs.next()) emp4AnnualAfter = rs.getDouble("salary") * 12;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT salary FROM employees WHERE emp_id = 5")) {
                if (rs.next()) emp5AnnualAfter = rs.getDouble("salary") * 12;
            }
        } catch (SQLException e) {
            System.err.println("  [FAIL] SQL error during post-check: " + e.getMessage());
            return false;
        }

        System.out.printf("  After Update: Bob Miller (ID 4) Annual = $%,.2f\n", emp4AnnualAfter);
        System.out.printf("  After Update: Charlie Brown (ID 5) Annual = $%,.2f\n", emp5AnnualAfter);

        // Verify Bob Miller increased by 10% (from $57,600 to $63,360)
        boolean bobUpdated = Math.abs(emp4AnnualAfter - (emp4AnnualBefore * 1.10)) < 0.01;
        // Verify Charlie Brown was NOT updated (since $108,000 >= $60,000)
        boolean charlieUnchanged = Math.abs(emp5AnnualAfter - emp5AnnualBefore) < 0.01;

        // 4. Restore database (revert the update)
        System.out.println("  Reverting salary changes in database...");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Division by 1.10 to restore the original salary
            stmt.executeUpdate("UPDATE employees SET salary = salary / 1.10 WHERE " +
                               "(employment_type = 'Full-Time' AND (salary * 12) < 66000.0) OR " +
                               "(employment_type = 'Part-Time' AND (salary * hours_worked * 52) < 66000.0)");
        } catch (SQLException e) {
            System.err.println("  Error reverting changes: " + e.getMessage());
        }

        if (bobUpdated && charlieUnchanged) {
            System.out.println("  [PASS] Test Case C completed successfully.");
            return true;
        } else {
            System.out.println("  [FAIL] Test Case C verification failed.");
            return false;
        }
    }
}
