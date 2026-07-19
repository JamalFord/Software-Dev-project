package services;

import db.DBConnection;
import models.Employee;
import models.FullTimeEmployee;
import models.PartTimeEmployee;
import models.PayStatement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    // 1. Insert a new employee
    public boolean insertEmployee(String firstName, String lastName, String ssn, int age, String address, 
                                   double salary, int hoursWorked, String employmentType, int jobId, int divisionId) {
        String sql = "INSERT INTO employees (first_name, last_name, ssn, age, address, salary, hours_worked, employment_type, job_id, division_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, ssn);
            pstmt.setInt(4, age);
            pstmt.setString(5, address);
            pstmt.setDouble(6, salary);
            pstmt.setInt(7, hoursWorked);
            pstmt.setString(8, employmentType);
            
            if (jobId > 0) pstmt.setInt(9, jobId);
            else pstmt.setNull(9, Types.INTEGER);
            
            if (divisionId > 0) pstmt.setInt(10, divisionId);
            else pstmt.setNull(10, Types.INTEGER);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting employee: " + e.getMessage());
            return false;
        }
    }

    // 2. Search for an employee (using name, SSN, or empid)
    public List<Employee> searchEmployees(String query) {
        List<Employee> results = new ArrayList<>();
        String sql = "SELECT e.*, j.job_title, d.division_name " +
                     "FROM employees e " +
                     "LEFT JOIN jobs j ON e.job_id = j.job_id " +
                     "LEFT JOIN divisions d ON e.division_id = d.division_id " +
                     "WHERE e.first_name LIKE ? OR e.last_name LIKE ? OR e.ssn = ? OR e.emp_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String wildCardQuery = "%" + query + "%";
            pstmt.setString(1, wildCardQuery);
            pstmt.setString(2, wildCardQuery);
            pstmt.setString(3, query);
            
            int numericId = -1;
            try {
                numericId = Integer.parseInt(query);
            } catch (NumberFormatException e) {
                // Ignore, it's not a numeric id
            }
            pstmt.setInt(4, numericId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int empId = rs.getInt("emp_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String ssn = rs.getString("ssn");
                    int age = rs.getInt("age");
                    String address = rs.getString("address");
                    double salary = rs.getDouble("salary");
                    int hoursWorked = rs.getInt("hours_worked");
                    String empType = rs.getString("employment_type");
                    int jobId = rs.getInt("job_id");
                    int divisionId = rs.getInt("division_id");
                    String jobTitle = rs.getString("job_title");
                    String divisionName = rs.getString("division_name");

                    Employee emp;
                    if ("Full-Time".equalsIgnoreCase(empType)) {
                        emp = new FullTimeEmployee(firstName + " " + lastName, age, address, empId, ssn, salary, jobId, divisionId);
                    } else {
                        emp = new PartTimeEmployee(firstName + " " + lastName, age, address, empId, ssn, salary, hoursWorked, jobId, divisionId);
                    }
                    emp.setJobTitle(jobTitle);
                    emp.setDivisionName(divisionName);
                    
                    // Fetch pay statements
                    emp.setPayHistory(getPayStatements(conn, empId));
                    results.add(emp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
        }
        return results;
    }

    // Helper to get pay statements for an employee
    private List<PayStatement> getPayStatements(Connection conn, int empId) throws SQLException {
        List<PayStatement> list = new ArrayList<>();
        String sql = "SELECT * FROM pay_statements WHERE emp_id = ? ORDER BY payment_date DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int stmtId = rs.getInt("statement_id");
                    LocalDate payDate = rs.getDate("payment_date").toLocalDate();
                    double amount = rs.getDouble("amount");
                    LocalDate start = rs.getDate("pay_period_start").toLocalDate();
                    LocalDate end = rs.getDate("pay_period_end").toLocalDate();
                    list.add(new PayStatement(stmtId, empId, payDate, amount, start, end));
                }
            }
        }
        return list;
    }

    // 3. Update an employee's data
    public boolean updateEmployee(int empId, String firstName, String lastName, String ssn, int age, String address, 
                                   double salary, int hoursWorked, String employmentType, int jobId, int divisionId) {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, ssn = ?, age = ?, address = ?, " +
                     "salary = ?, hours_worked = ?, employment_type = ?, job_id = ?, division_id = ? " +
                     "WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, ssn);
            pstmt.setInt(4, age);
            pstmt.setString(5, address);
            pstmt.setDouble(6, salary);
            pstmt.setInt(7, hoursWorked);
            pstmt.setString(8, employmentType);
            
            if (jobId > 0) pstmt.setInt(9, jobId);
            else pstmt.setNull(9, Types.INTEGER);
            
            if (divisionId > 0) pstmt.setInt(10, divisionId);
            else pstmt.setNull(10, Types.INTEGER);
            
            pstmt.setInt(11, empId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    // Delete an employee
    public boolean deleteEmployee(int empId) {
        String sql = "DELETE FROM employees WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    // 4. Update salary by percentage for a salary range (based on annual salary)
    // E.g., 3.2% for salary between 58K and 105K.
    public int updateSalariesByPercentageRange(double percentage, double minAnnual, double maxAnnual) {
        String sql = "UPDATE employees SET salary = salary * (1 + ? / 100.0) WHERE " +
                     "(employment_type = 'Full-Time' AND (salary * 12) >= ? AND (salary * 12) < ?) OR " +
                     "(employment_type = 'Part-Time' AND (salary * hours_worked * 52) >= ? AND (salary * hours_worked * 52) < ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, percentage);
            pstmt.setDouble(2, minAnnual);
            pstmt.setDouble(3, maxAnnual);
            pstmt.setDouble(4, minAnnual);
            pstmt.setDouble(5, maxAnnual);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating salaries in range: " + e.getMessage());
            return 0;
        }
    }

    // 5. Update salary for all employees whose annual salary is less than a particular threshold
    public int updateSalariesBelowThreshold(double percentage, double thresholdAnnual) {
        String sql = "UPDATE employees SET salary = salary * (1 + ? / 100.0) WHERE " +
                     "(employment_type = 'Full-Time' AND (salary * 12) < ?) OR " +
                     "(employment_type = 'Part-Time' AND (salary * hours_worked * 52) < ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, percentage);
            pstmt.setDouble(2, thresholdAnnual);
            pstmt.setDouble(3, thresholdAnnual);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating salaries below threshold: " + e.getMessage());
            return 0;
        }
    }
}
