package services;

import db.DBConnection;
import models.Employee;
import models.FullTimeEmployee;
import models.PayStatement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportService {

    // Report 1: Full-time employee information with pay statement history.
    public void generateFullTimeEmployeeHistoryReport() {
        System.out.println("\n==========================================================================");
        System.out.println("   REPORT 1: FULL-TIME EMPLOYEE INFORMATION & PAY STATEMENT HISTORY");
        System.out.println("==========================================================================");

        String sql = "SELECT e.*, j.job_title, d.division_name " +
                     "FROM employees e " +
                     "LEFT JOIN jobs j ON e.job_id = j.job_id " +
                     "LEFT JOIN divisions d ON e.division_id = d.division_id " +
                     "WHERE e.employment_type = 'Full-Time'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                count++;
                int empId = rs.getInt("emp_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String ssn = rs.getString("ssn");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                double salary = rs.getDouble("salary");
                int jobId = rs.getInt("job_id");
                int divisionId = rs.getInt("division_id");
                String jobTitle = rs.getString("job_title");
                String divisionName = rs.getString("division_name");

                FullTimeEmployee emp = new FullTimeEmployee(firstName + " " + lastName, age, address, empId, ssn, salary, jobId, divisionId);
                emp.setJobTitle(jobTitle);
                emp.setDivisionName(divisionName);

                // Fetch pay statements
                List<PayStatement> history = new ArrayList<>();
                String psSql = "SELECT * FROM pay_statements WHERE emp_id = ? ORDER BY payment_date DESC";
                try (PreparedStatement psPstmt = conn.prepareStatement(psSql)) {
                    psPstmt.setInt(1, empId);
                    try (ResultSet psRs = psPstmt.executeQuery()) {
                        while (psRs.next()) {
                            int stmtId = psRs.getInt("statement_id");
                            LocalDate payDate = psRs.getDate("payment_date").toLocalDate();
                            double amount = psRs.getDouble("amount");
                            LocalDate start = psRs.getDate("pay_period_start").toLocalDate();
                            LocalDate end = psRs.getDate("pay_period_end").toLocalDate();
                            history.add(new PayStatement(stmtId, empId, payDate, amount, start, end));
                        }
                    }
                }
                emp.setPayHistory(history);

                System.out.println("\n--------------------------------------------------------------------------");
                System.out.println(emp.getFormattedInfo());
            }
            if (count == 0) {
                System.out.println("No Full-Time employees found in the database.");
            }
        } catch (SQLException e) {
            System.err.println("Error generating Full-Time employee history report: " + e.getMessage());
        }
        System.out.println("\n==========================================================================");
    }

    // Report 2: Total pay for month by job title.
    public void generateTotalPayByJobTitleReport(int month, int year) {
        System.out.println("\n==========================================================================");
        System.out.printf("   REPORT 2: TOTAL PAY FOR %02d/%d BY JOB TITLE\n", month, year);
        System.out.println("==========================================================================");
        System.out.printf("%-30s | %-20s\n", "Job Title", "Total Pay Amount");
        System.out.println("--------------------------------------------------------------------------");

        String sql = "SELECT j.job_title, SUM(ps.amount) AS total_pay " +
                     "FROM pay_statements ps " +
                     "JOIN employees e ON ps.emp_id = e.emp_id " +
                     "JOIN jobs j ON e.job_id = j.job_id " +
                     "WHERE MONTH(ps.payment_date) = ? AND YEAR(ps.payment_date) = ? " +
                     "GROUP BY j.job_title " +
                     "ORDER BY total_pay DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                double grandTotal = 0;
                int count = 0;
                while (rs.next()) {
                    count++;
                    String title = rs.getString("job_title");
                    double totalPay = rs.getDouble("total_pay");
                    grandTotal += totalPay;
                    System.out.printf(Locale.US, "%-30s | $%,.2f\n", title, totalPay);
                }
                if (count == 0) {
                    System.out.printf("No pay statements found for %02d/%d.\n", month, year);
                } else {
                    System.out.println("--------------------------------------------------------------------------");
                    System.out.printf(Locale.US, "%-30s | $%,.2f\n", "GRAND TOTAL", grandTotal);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating total pay by job title report: " + e.getMessage());
        }
        System.out.println("==========================================================================");
    }

    // Report 3: Total pay for month by Division.
    public void generateTotalPayByDivisionReport(int month, int year) {
        System.out.println("\n==========================================================================");
        System.out.printf("   REPORT 3: TOTAL PAY FOR %02d/%d BY DIVISION\n", month, year);
        System.out.println("==========================================================================");
        System.out.printf("%-30s | %-20s\n", "Division Name", "Total Pay Amount");
        System.out.println("--------------------------------------------------------------------------");

        String sql = "SELECT d.division_name, SUM(ps.amount) AS total_pay " +
                     "FROM pay_statements ps " +
                     "JOIN employees e ON ps.emp_id = e.emp_id " +
                     "JOIN divisions d ON e.division_id = d.division_id " +
                     "WHERE MONTH(ps.payment_date) = ? AND YEAR(ps.payment_date) = ? " +
                     "GROUP BY d.division_name " +
                     "ORDER BY total_pay DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                double grandTotal = 0;
                int count = 0;
                while (rs.next()) {
                    count++;
                    String division = rs.getString("division_name");
                    double totalPay = rs.getDouble("total_pay");
                    grandTotal += totalPay;
                    System.out.printf(Locale.US, "%-30s | $%,.2f\n", division, totalPay);
                }
                if (count == 0) {
                    System.out.printf("No pay statements found for %02d/%d.\n", month, year);
                } else {
                    System.out.println("--------------------------------------------------------------------------");
                    System.out.printf(Locale.US, "%-30s | $%,.2f\n", "GRAND TOTAL", grandTotal);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating total pay by division report: " + e.getMessage());
        }
        System.out.println("==========================================================================");
    }
}
