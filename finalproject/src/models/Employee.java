package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Employee extends Person implements Compensable {
    protected int empId;
    protected String ssn;
    protected String employmentType;
    protected double salary; // Monthly salary for Full-Time, Hourly rate for Part-Time
    protected int jobId;
    protected int divisionId;
    protected String jobTitle;
    protected String divisionName;
    protected List<PayStatement> payHistory;

    public Employee(String name, int age, String address, int empId, String ssn, 
                    String employmentType, double salary, int jobId, int divisionId) {
        super(name, age, address);
        this.empId = empId;
        this.ssn = ssn;
        this.employmentType = employmentType;
        this.salary = salary;
        this.jobId = jobId;
        this.divisionId = divisionId;
        this.payHistory = new ArrayList<>();
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public List<PayStatement> getPayHistory() {
        return payHistory;
    }

    public void setPayHistory(List<PayStatement> payHistory) {
        this.payHistory = payHistory;
    }

    public void addPayStatement(PayStatement statement) {
        this.payHistory.add(statement);
    }

    public double calculateAnnualSalary() {
        // Base implementation (can be overridden)
        return salary * 12;
    }

    @Override
    public double calculateTotalCompensation() {
        return calculateAnnualSalary();
    }

    @Override
    public String getFormattedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getFormattedInfo()).append("\n")
          .append("Employee Information:\n")
          .append("Employee ID: ").append(empId).append("\n")
          .append("SSN: ").append(ssn != null ? ssn : "N/A").append("\n")
          .append("Division/Department: ").append(divisionName != null ? divisionName : "N/A").append("\n")
          .append("Job Title: ").append(jobTitle != null ? jobTitle : "N/A").append("\n")
          .append("Employment Type: ").append(employmentType).append("\n");
        
        if ("Full-Time".equalsIgnoreCase(employmentType)) {
            sb.append(String.format(Locale.US, "Monthly Salary: $%,.2f\n", salary))
              .append(String.format(Locale.US, "Annual Salary: $%,.2f\n", calculateAnnualSalary()));
        } else {
            sb.append(String.format(Locale.US, "Hourly Rate: $%,.2f/hr\n", salary));
        }

        if (!payHistory.isEmpty()) {
            sb.append("Pay Statement History:\n");
            for (PayStatement ps : payHistory) {
                sb.append("  - ").append(ps.toString()).append("\n");
            }
        }
        return sb.toString().trim();
    }
}
