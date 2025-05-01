Accounting Ledger Application: Specific Analysis
Core Functionality
This Java application implements a personal financial tracking system that:

Records two types of transactions:

Positive amounts (deposits/income)
Negative amounts (payments/expenses)


Stores data using a CSV file format:

Uses pipe (|) delimiters specifically to avoid conflicts with commas in text fields
File name: "transactions.csv"
Format: date|time|description|vendor|amount


Provides a four-level menu hierarchy:

Home Screen: Add Deposit, Make Payment, Ledger, Exit
Ledger Screen: All Transactions, Deposits, Payments, Reports, Home
Reports Screen: Month-to-Date, Previous Month, Year-to-Date, Previous Year, Search by Vendor
Transaction Entry: Date, Time, Description, Vendor, Amount input screens

    
   
![Alt text](screenShoots/Screenshot1.png)
![Alt text](screenShoots/Screenshot2.png)

