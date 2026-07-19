-- Create database if it does not exist
CREATE DATABASE IF NOT EXISTS employeeData;
USE employeeData;

-- Drop tables in reverse order of FK dependencies to avoid errors
DROP TABLE IF EXISTS pay_statements;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS divisions;

-- 1. Create divisions table
CREATE TABLE divisions (
    division_id INT AUTO_INCREMENT PRIMARY KEY,
    division_name VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Create jobs table
CREATE TABLE jobs (
    job_id INT AUTO_INCREMENT PRIMARY KEY,
    job_title VARCHAR(100) NOT NULL UNIQUE
);

-- 3. Create employees table (Initially without SSN, then we alter it to add SSN to satisfy the requirements)
CREATE TABLE employees (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    address VARCHAR(255),
    salary DOUBLE NOT NULL, -- Monthly salary for Full-Time, Hourly wage for Part-Time
    hours_worked INT DEFAULT 0, -- Weekly hours (used for Part-Time annual salary calculation)
    employment_type VARCHAR(20) NOT NULL CHECK (employment_type IN ('Full-Time', 'Part-Time')),
    job_id INT,
    division_id INT,
    FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE SET NULL,
    FOREIGN KEY (division_id) REFERENCES divisions(division_id) ON DELETE SET NULL
);

-- Requirement 1: Change employee table; add column SSN (no dashes)
-- We will write the ALTER TABLE statement to satisfy this specific requirement
ALTER TABLE employees ADD COLUMN ssn VARCHAR(9) AFTER last_name;

-- 4. Create pay_statements table (to store pay statement history)
CREATE TABLE pay_statements (
    statement_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    payment_date DATE NOT NULL,
    amount DOUBLE NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- Insert divisions
INSERT INTO divisions (division_name) VALUES 
('Engineering'),
('Sales'),
('Human Resources'),
('Finance'),
('Information Technology');

-- Insert jobs
INSERT INTO jobs (job_title) VALUES 
('Software Engineer'),
('Senior Developer'),
('Sales Manager'),
('HR Specialist'),
('Financial Analyst'),
('IT Support Technician'),
('General Manager');

-- Insert employees (Less than 20 full-time employees, plus some part-time)
-- We insert 12 full-time employees, and 4 part-time employees.
-- Full-time employees (salaries are monthly, e.g., $5,000/mo = $60,000/yr, which falls into the 58K-105K range)
INSERT INTO employees (first_name, last_name, ssn, age, address, salary, employment_type, job_id, division_id) VALUES
('John', 'Doe', '123456789', 30, '123 Maple Street', 5000.00, 'Full-Time', 1, 1),      -- $60K/yr
('Alice', 'Johnson', '987654321', 40, '789 Birch Lane', 8000.00, 'Full-Time', 7, 5),   -- $96K/yr
('Jane', 'Smith', '456789123', 28, '456 Oak Avenue', 5500.00, 'Full-Time', 1, 1),      -- $66K/yr
('Bob', 'Miller', '321654987', 35, '555 Pine Road', 4800.00, 'Full-Time', 6, 5),       -- $57.6K/yr
('Charlie', 'Brown', '789123456', 45, '777 Cedar Blvd', 9000.00, 'Full-Time', 3, 2),   -- $108K/yr
('Emily', 'Davis', '654987321', 32, '888 Elm Way', 6200.00, 'Full-Time', 5, 4),        -- $74.4K/yr
('David', 'Wilson', '159263487', 29, '111 Ash Court', 5100.00, 'Full-Time', 1, 1),     -- $61.2K/yr
('Fiona', 'Garcia', '852963741', 38, '222 Walnut Dr', 5800.00, 'Full-Time', 4, 3),     -- $69.6K/yr
('George', 'Martinez', '753159852', 42, '333 Cypress Ln', 7500.00, 'Full-Time', 2, 1),  -- $90K/yr
('Hannah', 'Thomas', '951753852', 26, '444 Redwood Ave', 4900.00, 'Full-Time', 6, 5),  -- $58.8K/yr
('Ian', 'White', '357951486', 31, '555 Spruce St', 6000.00, 'Full-Time', 5, 4),        -- $72K/yr
('Julia', 'Taylor', '147258369', 37, '666 Larch Road', 8200.00, 'Full-Time', 7, 1);    -- $98.4K/yr

-- Part-time employees
INSERT INTO employees (first_name, last_name, ssn, age, address, salary, hours_worked, employment_type, job_id, division_id) VALUES
('Kevin', 'Harris', '258369147', 22, '102 Main St', 25.00, 20, 'Part-Time', 6, 5),         -- $25.00/hr
('Laura', 'Clark', '369147258', 24, '204 High St', 22.50, 20, 'Part-Time', 4, 3),          -- $22.50/hr
('Mark', 'Lewis', '147369258', 23, '306 Broad St', 24.00, 15, 'Part-Time', 1, 1),          -- $24.00/hr
('Nora', 'Walker', '963852741', 25, '408 Park Ave', 26.50, 25, 'Part-Time', 5, 4);         -- $26.50/hr

-- Insert pay statements (payment history for full-time employees for the month of May and June)
INSERT INTO pay_statements (emp_id, payment_date, amount, pay_period_start, pay_period_end) VALUES
-- John Doe (emp_id = 1)
(1, '2026-05-31', 5000.00, '2026-05-01', '2026-05-31'),
(1, '2026-06-30', 5000.00, '2026-06-01', '2026-06-30'),
-- Alice Johnson (emp_id = 2)
(2, '2026-05-31', 8000.00, '2026-05-01', '2026-05-31'),
(2, '2026-06-30', 8000.00, '2026-06-01', '2026-06-30'),
-- Jane Smith (emp_id = 3)
(3, '2026-05-31', 5500.00, '2026-05-01', '2026-05-31'),
(3, '2026-06-30', 5500.00, '2026-06-01', '2026-06-30'),
-- Bob Miller (emp_id = 4)
(4, '2026-05-31', 4800.00, '2026-05-01', '2026-05-31'),
(4, '2026-06-30', 4800.00, '2026-06-01', '2026-06-30'),
-- Charlie Brown (emp_id = 5)
(5, '2026-05-31', 9000.00, '2026-05-01', '2026-05-31'),
(5, '2026-06-30', 9000.00, '2026-06-01', '2026-06-30'),
-- Emily Davis (emp_id = 6)
(6, '2026-05-31', 6200.00, '2026-05-01', '2026-05-31'),
(6, '2026-06-30', 6200.00, '2026-06-01', '2026-06-30'),
-- David Wilson (emp_id = 7)
(7, '2026-05-31', 5100.00, '2026-05-01', '2026-05-31'),
(7, '2026-06-30', 5100.00, '2026-06-01', '2026-06-30'),
-- Fiona Garcia (emp_id = 8)
(8, '2026-05-31', 5800.00, '2026-05-01', '2026-05-31'),
(8, '2026-06-30', 5800.00, '2026-06-01', '2026-06-30'),
-- George Martinez (emp_id = 9)
(9, '2026-05-31', 7500.00, '2026-05-01', '2026-05-31'),
(9, '2026-06-30', 7500.00, '2026-06-01', '2026-06-30'),
-- Hannah Thomas (emp_id = 10)
(10, '2026-05-31', 4900.00, '2026-05-01', '2026-05-31'),
(10, '2026-06-30', 4900.00, '2026-06-01', '2026-06-30'),
-- Ian White (emp_id = 11)
(11, '2026-05-31', 6000.00, '2026-05-01', '2026-05-31'),
(11, '2026-06-30', 6000.00, '2026-06-01', '2026-06-30'),
-- Julia Taylor (emp_id = 12)
(12, '2026-05-31', 8200.00, '2026-05-01', '2026-05-31'),
(12, '2026-06-30', 8200.00, '2026-06-01', '2026-06-30');
