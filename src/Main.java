import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Financial Tracker Application
 *
 * This application allows users to track financial transactions including
 * deposits and payments, and view a transaction ledger with various filtering options.
 */
class FinancialTracker {
    // Constants
    private static final String TRANSACTIONS_FILE = "transactions.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Scanner for user input
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Represents a financial transaction
     */
    private static class Transaction {
        private Date date;
        private String type;
        private double amount;
        private String description;

        public Transaction(Date date, String type, double amount, String description) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.description = description;
        }

        public Date getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public boolean isDeposit() {
            return "Deposit".equals(type);
        }

        public boolean isPayment() {
            return "Payment".equals(type);
        }

        public String getFormattedAmount() {
            if (isDeposit()) {
                return String.format("+$%.2f", amount);
            } else {
                return String.format("-$%.2f", amount);
            }
        }

        @Override
        public String toString() {
            return String.format("%-12s %-10s %-10s %-30s",
                    DATE_FORMAT.format(date), type, getFormattedAmount(), description);
        }
    }

    /**
     * Main method - entry point of the application
     */
    public static void main(String[] args) {
        initializeCSV();
        displayMainMenu();
    }

    /**
     * Initialize the CSV file with headers if it doesn't exist
     */
    private static void initializeCSV() {
        File file = new File(TRANSACTIONS_FILE);

        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Date,Type,Amount,Description");
            } catch (IOException e) {
                System.out.println("Error initializing transaction file: " + e.getMessage());
            }
        }
    }

    /**
     * Display main menu and handle user choices
     */
    private static void displayMainMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== FINANCIAL TRACKER =====");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "D":
                    addDeposit();
                    break;
                case "P":
                    makePayment();
                    break;
                case "L":
                    displayLedgerMenu();
                    break;
                case "X":
                    exit = true;
                    System.out.println("Thank you for using Financial Tracker. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Display the ledger menu options
     */
    private static void displayLedgerMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n===== LEDGER MENU =====");
            System.out.println("A) All - Display all entries");
            System.out.println("D) Deposits - Display only deposits");
            System.out.println("P) Payments - Display only payments");
            System.out.println("R) Reports - Run predefined reports");
            System.out.println("H) Home - Go back to the home page");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A":
                    displayAllTransactions();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    displayReportsMenu();
                    break;
                case "H":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Display the reports menu options
     */
    private static void displayReportsMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n===== REPORTS MENU =====");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back - Go back to the ledger menu");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayMonthToDateReport();
                    break;
                case "2":
                    displayPreviousMonthReport();
                    break;
                case "3":
                    displayYearToDateReport();
                    break;
                case "4":
                    displayPreviousYearReport();
                    break;
                case "5":
                    searchByVendor();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Add a deposit transaction
     */
    private static void addDeposit() {
        System.out.println("\n===== ADD DEPOSIT =====");

        try {
            System.out.print("Enter deposit amount: $");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Error: Amount must be positive");
                return;
            }

            System.out.print("Enter description/vendor: ");
            String description = scanner.nextLine();

            // Save to CSV
            String date = DATE_FORMAT.format(new Date());
            addTransactionToCSV(date, "Deposit", amount, description);

            System.out.printf("Deposit of $%.2f added successfully!\n", amount);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number for amount");
        }
    }

    /**
     * Add a payment (debit) transaction
     */
    private static void makePayment() {
        System.out.println("\n===== MAKE PAYMENT =====");

        try {
            System.out.print("Enter payment amount: $");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Error: Amount must be positive");
                return;
            }

            System.out.print("Enter description/vendor: ");
            String description = scanner.nextLine();

            // Save to CSV
            String date = DATE_FORMAT.format(new Date());
            addTransactionToCSV(date, "Payment", amount, description);

            System.out.printf("Payment of $%.2f recorded successfully!\n", amount);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number for amount");
        }
    }

    /**
     * Add a transaction to the CSV file
     */
    private static void addTransactionToCSV(String date, String type, double amount, String description) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            // Escape commas in description if needed
            if (description.contains(",")) {
                description = "\"" + description + "\"";
            }

            writer.println(date + "," + type + "," + amount + "," + description);
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    /**
     * Read all transactions from the CSV file
     */
    private static List<Transaction> readAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);

        if (!file.exists()) {
            return transactions;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(TRANSACTIONS_FILE));

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = parseCSVLine(line);

                if (parts.length < 4) continue;

                try {
                    Date date = DATE_FORMAT.parse(parts[0]);
                    String type = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String description = parts[3];

                    transactions.add(new Transaction(date, type, amount, description));
                } catch (ParseException | NumberFormatException e) {
                    System.out.println("Error parsing transaction: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }

        // Sort transactions by date (newest first)
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        return transactions;
    }

    /**
     * Display all transactions
     */
    private static void displayAllTransactions() {
        List<Transaction> transactions = readAllTransactions();
        displayTransactions(transactions, "ALL TRANSACTIONS");
    }

    /**
     * Display only deposit transactions
     */
    private static void displayDeposits() {
        List<Transaction> transactions = readAllTransactions();
        List<Transaction> deposits = transactions.stream()
                .filter(Transaction::isDeposit)
                .collect(Collectors.toList());

        displayTransactions(deposits, "DEPOSITS ONLY");
    }

    /**
     * Display only payment transactions
     */
    private static void displayPayments() {
        List<Transaction> transactions = readAllTransactions();
        List<Transaction> payments = transactions.stream()
                .filter(Transaction::isPayment)
                .collect(Collectors.toList());

        displayTransactions(payments, "PAYMENTS ONLY");
    }

    /**
     * Display month to date report
     */
    private static void displayMonthToDateReport() {
        List<Transaction> transactions = readAllTransactions();

        // Get current month and year
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        List<Transaction> filtered = transactions.stream()
                .filter(t -> {
                    Calendar transactionCal = Calendar.getInstance();
                    transactionCal.setTime(t.getDate());
                    return transactionCal.get(Calendar.MONTH) == currentMonth &&
                            transactionCal.get(Calendar.YEAR) == currentYear;
                })
                .collect(Collectors.toList());

        displayTransactions(filtered, "MONTH TO DATE REPORT");
    }

    /**
     * Display previous month report
     */
    private static void displayPreviousMonthReport() {
        List<Transaction> transactions = readAllTransactions();

        // Get previous month and year
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        int prevMonth = cal.get(Calendar.MONTH);
        int prevMonthYear = cal.get(Calendar.YEAR);

        List<Transaction> filtered = transactions.stream()
                .filter(t -> {
                    Calendar transactionCal = Calendar.getInstance();
                    transactionCal.setTime(t.getDate());
                    return transactionCal.get(Calendar.MONTH) == prevMonth &&
                            transactionCal.get(Calendar.YEAR) == prevMonthYear;
                })
                .collect(Collectors.toList());

        displayTransactions(filtered, "PREVIOUS MONTH REPORT");
    }

    /**
     * Display year to date report
     */
    private static void displayYearToDateReport() {
        List<Transaction> transactions = readAllTransactions();

        // Get current year
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);

        List<Transaction> filtered = transactions.stream()
                .filter(t -> {
                    Calendar transactionCal = Calendar.getInstance();
                    transactionCal.setTime(t.getDate());
                    return transactionCal.get(Calendar.YEAR) == currentYear;
                })
                .collect(Collectors.toList());

        displayTransactions(filtered, "YEAR TO DATE REPORT");
    }

    /**
     * Display previous year report
     */
    private static void displayPreviousYearReport() {
        List<Transaction> transactions = readAllTransactions();

        // Get previous year
        Calendar cal = Calendar.getInstance();
        int prevYear = cal.get(Calendar.YEAR) - 1;

        List<Transaction> filtered = transactions.stream()
                .filter(t -> {
                    Calendar transactionCal = Calendar.getInstance();
                    transactionCal.setTime(t.getDate());
                    return transactionCal.get(Calendar.YEAR) == prevYear;
                })
                .collect(Collectors.toList());

        displayTransactions(filtered, "PREVIOUS YEAR REPORT");
    }

    /**
     * Search transactions by vendor/description
     */
    private static void searchByVendor() {
        System.out.print("Enter vendor/description to search for: ");
        String searchTerm = scanner.nextLine().toLowerCase();

        List<Transaction> transactions = readAllTransactions();
        List<Transaction> filtered = transactions.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        displayTransactions(filtered, "SEARCH RESULTS FOR: " + searchTerm);
    }

    /**
     * Display a list of transactions with appropriate formatting
     */
    private static void displayTransactions(List<Transaction> transactions, String title) {
        System.out.println("\n===== " + title + " =====");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        // Calculate balance
        double balance = 0.0;
        for (Transaction t : readAllTransactions()) {
            if (t.isDeposit()) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
        }

        // Display headers
        System.out.printf("%-12s %-10s %-10s %-30s\n", "Date", "Type", "Amount", "Description");
        System.out.println("==============================================================");

        // Display transactions
        for (Transaction t : transactions) {
            System.out.println(t);
        }

        System.out.println("==============================================================");
        System.out.printf("Number of Transactions: %d\n", transactions.size());
        System.out.printf("Current Overall Balance: $%.2f\n", balance);

        // Pause before returning to menu
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Parse a CSV line, handling quoted fields
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        result.add(field.toString());
        return result.toArray(new String[0]);
    }
}

