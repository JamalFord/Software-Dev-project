package models;

public class PartTimeEmployee extends Employee {
    private int hoursWorkedPerWeek;

    public PartTimeEmployee(String name, int age, String address, int empId, String ssn, 
                            double hourlyRate, int hoursWorkedPerWeek, int jobId, int divisionId) {
        super(name, age, address, empId, ssn, "Part-Time", hourlyRate, jobId, divisionId);
        this.hoursWorkedPerWeek = hoursWorkedPerWeek;
    }

    public int getHoursWorkedPerWeek() {
        return hoursWorkedPerWeek;
    }

    public void setHoursWorkedPerWeek(int hoursWorkedPerWeek) {
        this.hoursWorkedPerWeek = hoursWorkedPerWeek;
    }

    @Override
    public double calculateAnnualSalary() {
        // Hourly rate (stored in salary) * hours per week * 52 weeks
        return salary * hoursWorkedPerWeek * 52;
    }
}
