# Company Z Employee Management System

##  How to Run

1. **Start MySQL**: Make sure your local MySQL server is running (port 3306).
2. **Execute the App**:
   * If you are in the **parent directory** (`Software Development - CTW`):
     ```bash
     java -cp finalproject/bin:finalproject/lib/mysql-connector-j.jar Driver
     ```
   * If you are **inside the `finalproject` folder**:
     ```bash
     java -cp bin:lib/mysql-connector-j.jar Driver
     ```

----------

## Database Setup
If setting up for the first time, run this command in your terminal to initialize and seed the database:
```bash
mysql -u root -p < db_setup.sql
```
*(Connection credentials can be changed in `src/db/DBConnection.java`)*
