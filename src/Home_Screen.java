import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

class AccountingLedgerApplication {
    private static final String TRANSACTION_FILE = "transactions.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadTransactions();
        showHomeScreen();
    }

    // Transaction class to represent each financial transaction
    static final class Transaction {
        private final LocalDate date;
        private final LocalTime time;
        private final String description;
        private final String vendor;
        private final double amount;

        Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
            this.date = date;
            this.time = time;
            this.description = description;
            this.vendor = vendor;
            this.amount = amount;
        }

            // Parse a CSV line into a Transaction object
            public static Transaction fromCSV(String line) {
                String[] parts = line.split("\\|");
                if (parts.length != 5) {
                    throw new IllegalArgumentException("Invalid CSV line format: " + line);
                }

                LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                LocalTime time = LocalTime.parse(parts[1], TIME_FORMATTER);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);

                return new Transaction(date, time, description, vendor, amount);
            }

        // Convert Transaction to CSV format
        public String toCSV() {
            return String.format("%s|%s|%s|%s|%.2f",
                    date.format(DATE_FORMATTER),
                    time.format(TIME_FORMATTER),
                    description,
                    vendor,
                    amount);
        }

        public LocalDateTime getDateTime() {
            return LocalDateTime.of(date, time);
        }

        @Override
        public String toString() {
            return String.format("%-10s | %-8s | %-20s | %-15s | $%10.2f",
                    date.format(DATE_FORMATTER),
                    time.format(TIME_FORMATTER),
                    description.length() > 20 ? description.substring(0, 17) + "..." : description,
                    vendor.length() > 15 ? vendor.substring(0, 12) + "..." : vendor,
                    amount);
        }

        public LocalDate date() {
            return date;
        }

        public LocalTime time() {
            return time;
        }

        public String description() {
            return description;
        }

        public String vendor() {
            return vendor;
        }

        public double amount() {
            return amount;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Transaction) obj;
            return Objects.equals(this.date, that.date) &&
                    Objects.equals(this.time, that.time) &&
                    Objects.equals(this.description, that.description) &&
                    Objects.equals(this.vendor, that.vendor) &&
                    Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, time, description, vendor, amount);
        }

    }

    // Load transactions from the CSV file
    private static void loadTransactions() {
        transactions.clear();
        Path path = Paths.get(TRANSACTION_FILE);

        if (!Files.exists(path)) {
            System.out.println("No transaction file found. Starting with an empty ledger.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    transactions.add(Transaction.fromCSV(line));
                }
            }
            System.out.println("Loaded " + transactions.size() + " transactions.");
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing transactions: " + e.getMessage());
        }

        // Sort transactions by date and time (newest first)
        transactions.sort(Comparator.comparing(Transaction::getDateTime).reversed());
    }

    // Save transactions to the CSV file
    private static void saveTransactions() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(TRANSACTION_FILE))) {
            for (Transaction transaction : transactions) {
                writer.write(transaction.toCSV());
                writer.newLine();
            }
            System.out.println("Transactions saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    // Display the home screen
    private static void showHomeScreen() {
        while (true) {
            System.out.println("\n===== Accounting Ledger Application =====");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("\nPlease select an option: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "D":
                    addDeposit();
                    break;
                case "P":
                    makePayment();
                    break;
                case "L":
                    showLedgerScreen();
                    break;
                case "X":
                    System.out.println("Thank you for using the Accounting Ledger Application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Add a deposit
    private static void addDeposit() {
        System.out.println("\n===== Add Deposit =====");

        LocalDate date = promptForDate();
        LocalTime time = promptForTime();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        double amount = promptForAmount("Enter deposit amount: $");
        if (amount < 0) {
            amount = Math.abs(amount); // Ensure deposit is positive
        }

        Transaction deposit = new Transaction(date, time, description, vendor, amount);
        transactions.add(0, deposit); // Add to the beginning for display purposes
        saveTransactions();

        System.out.println("Deposit added successfully!");
        System.out.println(deposit);
    }

    // Make a payment (debit)
    private static void makePayment() {
        System.out.println("\n===== Make Payment =====");

        LocalDate date = promptForDate();
        LocalTime time = promptForTime();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        double amount = promptForAmount("Enter payment amount: $");
        if (amount > 0) {
            amount = -amount; // Ensure payment is negative
        }

        Transaction payment = new Transaction(date, time, description, vendor, amount);
        transactions.addFirst(payment); // Add to the beginning for display purposes
        saveTransactions();

        System.out.println("Payment added successfully!");
        System.out.println(payment);
    }

    // Display the ledger screen
    private static void showLedgerScreen() {
        while (true) {
            System.out.println("\n===== Ledger =====");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("\nPlease select an option: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A":
                    showAllTransactions();
                    break;
                case "D":
                    showDeposits();
                    break;
                case "P":
                    showPayments();
                    break;
                case "R":
                    showReportsScreen();
                    break;
                case "H":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Display all transactions
    private static void showAllTransactions() {
        System.out.println("\n===== All Transactions =====");
        displayTransactionHeader();

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Display only deposits
    private static void showDeposits() {
        System.out.println("\n===== Deposits =====");
        displayTransactionHeader();

        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.amount() > 0) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No deposits found.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Display only payments
    private static void showPayments() {
        System.out.println("\n===== Payments =====");
        displayTransactionHeader();

        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.amount() < 0) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No payments found.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Display the reports screen
    private static void showReportsScreen() {
        while (true) {
            System.out.println("\n===== Reports =====");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");
            System.out.print("\nPlease select an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showMonthToDateReport();
                    break;
                case "2":
                    showPreviousMonthReport();
                    break;
                case "3":
                    showYearToDateReport();
                    break;
                case "4":
                    showPreviousYearReport();
                    break;
                case "5":
                    searchByVendor();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Show month-to-date report
    private static void showMonthToDateReport() {
        System.out.println("\n===== Month To Date Report =====");
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        displayTransactionHeader();
        printTransactionsInDateRange(startOfMonth, today);
    }

    // Show previous month report
    private static void showPreviousMonthReport() {
        System.out.println("\n===== Previous Month Report =====");
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfPreviousMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = today.withDayOfMonth(1).minusDays(1);

        displayTransactionHeader();
        printTransactionsInDateRange(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
    }

    // Show year-to-date report
    private static void showYearToDateReport() {
        System.out.println("\n===== Year To Date Report =====");
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);

        displayTransactionHeader();
        printTransactionsInDateRange(startOfYear, today);
    }

    // Show previous year report
    private static void showPreviousYearReport() {
        System.out.println("\n===== Previous Year Report =====");
        int previousYear = Year.now().getValue() - 1;
        LocalDate startOfPreviousYear = LocalDate.of(previousYear, Month.JANUARY, 1);
        LocalDate endOfPreviousYear = LocalDate.of(previousYear, Month.DECEMBER, 31);

        displayTransactionHeader();
        printTransactionsInDateRange(startOfPreviousYear, endOfPreviousYear);
    }

    // Search transactions by vendor
    private static void searchByVendor() {
        System.out.print("\nEnter vendor name: ");
        String vendorSearch = scanner.nextLine().trim().toLowerCase();

        System.out.println("\n===== Transactions for Vendor: " + vendorSearch + " =====");
        displayTransactionHeader();

        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.vendor().toLowerCase().contains(vendorSearch)) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found for vendor: " + vendorSearch);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Print transactions within a date range
    private static void printTransactionsInDateRange(LocalDate startDate, LocalDate endDate) {
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (!transaction.date().isBefore(startDate) && !transaction.date().isAfter(endDate)) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found in the specified date range.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    // Display transaction header
    private static void displayTransactionHeader() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-8s | %-20s | %-15s | %-10s\n", "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("----------------------------------------------------------------------------------");
    }
    // Helper methods for user input
    private static LocalDate promptForDate() {
        System.out.print("Enter date (yyyy-MM-dd) or press Enter for today: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (Exception e) {
            System.out.println("Invalid date format. Using today's date.");
            return LocalDate.now();
        }
    }

    private static LocalTime promptForTime() {
        System.out.print("Enter time (HH:mm:ss) or press Enter for now: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return LocalTime.now();
        }
        try {
            return LocalTime.parse(input, TIME_FORMATTER);
        } catch (Exception e) {
            System.out.println("Invalid time format. Using current time.");
            return LocalTime.now();
        }
    }
    private static double promptForAmount(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
    }
}

