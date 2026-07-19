package models;

public class FullTimeEmployee extends Employee {
    public FullTimeEmployee(String name, int age, String address, int empId, String ssn, 
                            double salary, int jobId, int divisionId) {
        super(name, age, address, empId, ssn, "Full-Time", salary, jobId, divisionId);
    }

    @Override
    public double calculateAnnualSalary() {
        return salary * 12;
    }
}
