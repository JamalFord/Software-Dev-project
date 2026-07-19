package models;

import java.time.LocalDate;

public class PayStatement {
    private int statementId;
    private int empId;
    private LocalDate paymentDate;
    private double amount;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;

    public PayStatement(int statementId, int empId, LocalDate paymentDate, double amount, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        this.statementId = statementId;
        this.empId = empId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.payPeriodStart = payPeriodStart;
        this.payPeriodEnd = payPeriodEnd;
    }

    public int getStatementId() {
        return statementId;
    }

    public int getEmpId() {
        return empId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPayPeriodStart() {
        return payPeriodStart;
    }

    public LocalDate getPayPeriodEnd() {
        return payPeriodEnd;
    }

    @Override
    public String toString() {
        return String.format("Statement #%d: Date: %s | Amount: $%,.2f | Period: %s to %s",
                statementId, paymentDate, amount, payPeriodStart, payPeriodEnd);
    }
}
