import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// ABSTRACT BASE CLASS: Transaction
// Demonstrates: Abstraction + Inheritance base

abstract class Transaction {
    private double amount; // stores amount of transaction
    private String category; // stores category like Food, Salary
    private String date; // stores date of transaction

    // Constructor to initialize values
    public Transaction(double amount, String category, String date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Getter methods (to access private variables safely)
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    // Abstract method: forces subclasses to implement their own behavior
    // (Polymorphism)
    public abstract double apply(double currentBalance);

    // Abstract method: returns transaction type label
    public abstract String getType();

    @Override // Used when printing transaction details
    public String toString() {
        return String.format("[%s] %s | Category: %s | Amount: %.2f | Date: %s",
                getType(), date, category, amount, date);
    }
}

// SUBCLASS: Income
// Demonstrates: Inheritance + Polymorphism (apply adds to balance)

class Income extends Transaction {

    public Income(double amount, String category, String date) {
        super(amount, category, date);
    }

    // Polymorphic method: Income ADDS to balance
    @Override
    public double apply(double currentBalance) {
        return currentBalance + getAmount();
    }

    @Override
    public String getType() {
        return "INCOME";
    }
}

// SUBCLASS: Expense
// Demonstrates: Inheritance + Polymorphism (apply subtracts from balance)

class Expense extends Transaction {

    public Expense(double amount, String category, String date) {
        super(amount, category, date);
    }

    // Polymorphic method: Expense SUBTRACTS from balance
    @Override
    public double apply(double currentBalance) {
        return currentBalance - getAmount();
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }
}

// CLASS: Account
// Demonstrates: Encapsulation (private balance + transaction list)

class Account {
    private double balance; // current balance
    private List<Transaction> transactions; // list of all transactions
    private String accountHolder; // name of user

    public Account(String accountHolder) {
        this.accountHolder = accountHolder;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    // Add a transaction (validates amount, updates balance using polymorphism)
    public boolean addTransaction(Transaction t) {
        if (t.getAmount() <= 0) {
            System.out.println("  [ERROR] Amount must be greater than 0. Transaction rejected.");
            return false;
        }
        // Polymorphism: apply() behaves differently for Income/Expense
        this.balance = t.apply(this.balance);
        this.transactions.add(t);
        return true;
    }

    // Getters
    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public String getAccountHolder() {
        return accountHolder;
    }
}

// CLASS: ReportGenerator
// Demonstrates: Abstraction (hides report-generation logic)

class ReportGenerator {

    // Generate full financial report for given account
    public void generateReport(Account account) {
        List<Transaction> txList = account.getTransactions();

        if (txList.isEmpty()) {
            System.out.println("\n  No transactions found. Please add transactions first.");
            return;
        }

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        // HashMap for category-wise expense breakdown
        Map<String, Double> expenseByCategory = new HashMap<>();

        // Process each transaction using polymorphism
        for (Transaction t : txList) {
            if (t instanceof Income) {
                totalIncome += t.getAmount();
            } else if (t instanceof Expense) {
                totalExpense += t.getAmount();
                // Accumulate expense per category
                expenseByCategory.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }

        double netBalance = totalIncome - totalExpense;

        // Print report header
        System.out.println("\n  ╔══════════════════════════════════════════╗");
        System.out.println("  ║       PERSONAL FINANCE REPORT            ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.printf("  Account Holder : %s%n", account.getAccountHolder());
        System.out.printf("  Report Date    : %s%n", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        System.out.println("  ------------------------------------------");
        System.out.printf("  Total Income   : Rs. %.2f%n", totalIncome);
        System.out.printf("  Total Expense  : Rs. %.2f%n", totalExpense);
        System.out.printf("  Net Balance    : Rs. %.2f%n", netBalance);
        System.out.println("  ------------------------------------------");

        // Category-wise expense breakdown
        if (!expenseByCategory.isEmpty()) {
            System.out.println("  Category-wise Expense Breakdown:");
            for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
                System.out.printf("    %-15s : Rs. %.2f%n", entry.getKey(), entry.getValue());
            }
        }

        System.out.println("  ------------------------------------------");
        System.out.println("  Transaction History:");
        int i = 1;
        for (Transaction t : txList) {
            System.out.printf("  %2d. [%-7s] %-12s Rs. %8.2f   %s%n",
                    i++, t.getType(), t.getCategory(), t.getAmount(), t.getDate());
        }
        System.out.println("  ==========================================\n");
    }
}

// MAIN CLASS: PersonalFinanceTracker
// Demonstrates: Abstraction via command-line menu UI

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static Account account;
    static ReportGenerator reportGenerator = new ReportGenerator();

    public static void main(String[] args) {
        System.out.println("\n  ╔══════════════════════════════════════════╗");
        System.out.println("  ║   PERSONAL FINANCE TRACKER (OOP - Java)  ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.print("  Enter Account Holder Name: ");
        String name = scanner.nextLine().trim();
        account = new Account(name.isEmpty() ? "User" : name);
        System.out.println("\n  Welcome, " + account.getAccountHolder() + "!");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntInput("  Enter choice: ");
            switch (choice) {
                case 1 -> handleAddIncome();
                case 2 -> handleAddExpense();
                case 3 -> reportGenerator.generateReport(account);
                case 4 -> {
                    System.out.println("\n  Thank you for using Personal Finance Tracker. Goodbye!\n");
                    running = false;
                }
                default -> System.out.println("  [WARN] Invalid choice. Please enter 1-4.\n");
            }
        }
        scanner.close();
    }

    // Display main menu
    static void printMenu() {
        System.out.println("\n  ┌────────────────────────────────────┐");
        System.out.println("  │           MAIN MENU                │");
        System.out.println("  ├────────────────────────────────────┤");
        System.out.printf("  │  Current Balance: Rs. %-12.2f │%n", account.getBalance());
        System.out.println("  ├────────────────────────────────────┤");
        System.out.println("  │  1. Add Income                     │");
        System.out.println("  │  2. Add Expense                    │");
        System.out.println("  │  3. View Report                    │");
        System.out.println("  │  4. Exit                           │");
        System.out.println("  └────────────────────────────────────┘");
    }

    // Handle Add Income flow
    static void handleAddIncome() {
        System.out.println("\n  --- ADD INCOME ---");
        System.out.println("  Categories: Salary, Business, Freelance, Investment, Other");
        double amount = readDoubleInput("  Enter Amount (Rs.): ");
        System.out.print("  Enter Category: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty())
            category = "Other";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Income income = new Income(amount, category, date);
        if (account.addTransaction(income)) {
            System.out.println("\n  ✔ Transaction successful!");
            System.out.printf("  Current Balance: Rs. %.2f%n", account.getBalance());
        }
    }

    // Handle Add Expense flow
    static void handleAddExpense() {
        System.out.println("\n  --- ADD EXPENSE ---");
        System.out.println("  Categories: Food, Rent, Travel, Shopping, Healthcare, Education, Entertainment, Other");
        double amount = readDoubleInput("  Enter Amount (Rs.): ");
        System.out.print("  Enter Category: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty())
            category = "Other";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Expense expense = new Expense(amount, category, date);
        if (account.addTransaction(expense)) {
            System.out.println("\n  ✔ Transaction successful!");
            System.out.printf("  Current Balance: Rs. %.2f%n", account.getBalance());
        }
    }

    // Utility: read validated integer input
    static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Please enter a valid number.");
            }
        }
    }

    // Utility: read validated double input
    static double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Please enter a valid amount.");
            }
        }
    }
}
