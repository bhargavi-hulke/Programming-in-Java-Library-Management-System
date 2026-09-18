package app;

import exception.LibraryException;
import model.Book;
import model.Member;
import model.Transaction;
import service.LibraryService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console Application Interface for VITyarthi Library Management System.
 * Provides intuitive, menu-driven CLI workflows with input validation.
 */
public class LibraryApp {

    private static final String DATA_DIR = "data";
    private final LibraryService libraryService;
    private final Scanner scanner;

    public LibraryApp() {
        this.libraryService = new LibraryService(DATA_DIR);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        LibraryApp app = new LibraryApp();
        app.start();
    }

    public void start() {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = prompt("Enter your choice (1-6): ");

            switch (choice) {
                case "1":
                    handleBookMenu();
                    break;
                case "2":
                    handleMemberMenu();
                    break;
                case "3":
                    handleCirculationMenu();
                    break;
                case "4":
                    handleAnalytics();
                    break;
                case "5":
                    displayQuickHelp();
                    break;
                case "6":
                    running = false;
                    libraryService.persistAll();
                    System.out.println("\n========================================================");
                    System.out.println("  Thank you for using VITyarthi Library Management System!");
                    System.out.println("  All changes have been successfully saved. Goodbye!");
                    System.out.println("========================================================\n");
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please select an option between 1 and 6.");
            }
        }
    }

    private void printBanner() {
        System.out.println("\n================================================================");
        System.out.println("          VITyarthi - SMART LIBRARY MANAGEMENT SYSTEM          ");
        System.out.println("      Java Object-Oriented Course Project - Flipped Evaluation  ");
        System.out.println("================================================================");
    }

    private void printMainMenu() {
        System.out.println("\n+----------------------------------------------+");
        System.out.println("|                  MAIN MENU                   |");
        System.out.println("+----------------------------------------------+");
        System.out.println("|  1. Book Catalog Management                  |");
        System.out.println("|  2. Member Management                        |");
        System.out.println("|  3. Circulation (Issue / Return Books)       |");
        System.out.println("|  4. Reports & System Dashboard               |");
        System.out.println("|  5. Help & Project Info                      |");
        System.out.println("|  6. Save & Exit                              |");
        System.out.println("+----------------------------------------------+");
    }

    // ================= Book Catalog Menu =================

    private void handleBookMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- [ BOOK CATALOG MANAGEMENT ] ---");
            System.out.println("1. List All Books");
            System.out.println("2. Add New Book");
            System.out.println("3. Search Books by Title");
            System.out.println("4. Search Books by Author");
            System.out.println("5. Search Books by Genre");
            System.out.println("6. Remove Book");
            System.out.println("7. Back to Main Menu");

            String ch = prompt("Select an option (1-7): ");
            switch (ch) {
                case "1":
                    listAllBooks();
                    break;
                case "2":
                    addNewBook();
                    break;
                case "3":
                    searchBooksTitle();
                    break;
                case "4":
                    searchBooksAuthor();
                    break;
                case "5":
                    searchBooksGenre();
                    break;
                case "6":
                    removeBook();
                    break;
                case "7":
                    inMenu = false;
                    break;
                default:
                    System.out.println("[!] Invalid option.");
            }
        }
    }

    private void listAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        System.out.println("\n----------------- CURRENT BOOK CATALOG (" + books.size() + " Titles) -----------------");
        if (books.isEmpty()) {
            System.out.println("Catalog is currently empty.");
            return;
        }
        System.out.printf("%-8s | %-32s | %-20s | %-16s | %s%n", "Book ID", "Title", "Author", "Genre", "Available/Total");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Book b : books) {
            System.out.printf("%-8s | %-32s | %-20s | %-16s | %d/%d%n",
                    b.getBookId(), truncate(b.getTitle(), 32), truncate(b.getAuthor(), 20),
                    truncate(b.getGenre(), 16), b.getAvailableCopies(), b.getTotalCopies());
        }
    }

    private void addNewBook() {
        System.out.println("\n--- Register New Book ---");
        String id = prompt("Enter Book ID (e.g. B108): ").trim();
        if (id.isEmpty()) {
            System.out.println("[!] Book ID cannot be blank.");
            return;
        }
        try {
            if (libraryService.getBook(id) != null) {
                System.out.println("[!] A book with ID '" + id + "' already exists in catalog.");
                return;
            }
        } catch (LibraryException ignored) {}

        String title = prompt("Enter Title: ").trim();
        String author = prompt("Enter Author: ").trim();
        String genre = prompt("Enter Genre (e.g., Computer Science, Fiction): ").trim();
        int copies = promptInt("Enter Total Copies: ");

        if (copies <= 0) {
            System.out.println("[!] Copies count must be greater than zero.");
            return;
        }

        Book newBook = new Book(id, title, author, genre, copies);
        libraryService.addBook(newBook);
        System.out.println("[SUCCESS] Book '" + title + "' (ID: " + id + ") successfully added to catalog!");
    }

    private void searchBooksTitle() {
        String q = prompt("Enter title search keyword: ");
        List<Book> results = libraryService.searchBooksByTitle(q);
        displayBookResults(results);
    }

    private void searchBooksAuthor() {
        String q = prompt("Enter author name keyword: ");
        List<Book> results = libraryService.searchBooksByAuthor(q);
        displayBookResults(results);
    }

    private void searchBooksGenre() {
        String q = prompt("Enter genre keyword: ");
        List<Book> results = libraryService.searchBooksByGenre(q);
        displayBookResults(results);
    }

    private void displayBookResults(List<Book> books) {
        System.out.println("\nSearch Results (" + books.size() + " matches found):");
        if (books.isEmpty()) {
            System.out.println("No books matched the search criteria.");
            return;
        }
        for (Book b : books) {
            System.out.println(" - " + b);
        }
    }

    private void removeBook() {
        String id = prompt("Enter Book ID to remove: ").trim();
        try {
            libraryService.removeBook(id);
            System.out.println("[SUCCESS] Book " + id + " removed successfully.");
        } catch (LibraryException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    // ================= Member Management Menu =================

    private void handleMemberMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- [ MEMBER MANAGEMENT ] ---");
            System.out.println("1. List All Members");
            System.out.println("2. Register New Member");
            System.out.println("3. Search Member by Name");
            System.out.println("4. Remove Member");
            System.out.println("5. Back to Main Menu");

            String ch = prompt("Select an option (1-5): ");
            switch (ch) {
                case "1":
                    listAllMembers();
                    break;
                case "2":
                    registerNewMember();
                    break;
                case "3":
                    searchMemberName();
                    break;
                case "4":
                    removeMember();
                    break;
                case "5":
                    inMenu = false;
                    break;
                default:
                    System.out.println("[!] Invalid option.");
            }
        }
    }

    private void listAllMembers() {
        List<Member> members = libraryService.getAllMembers();
        System.out.println("\n---------------- REGISTERED MEMBERS (" + members.size() + ") ----------------");
        if (members.isEmpty()) {
            System.out.println("No registered members found.");
            return;
        }
        System.out.printf("%-8s | %-20s | %-26s | %-12s | %s%n", "MemberID", "Name", "Email", "Phone", "Borrowed/Limit");
        System.out.println("--------------------------------------------------------------------------------");
        for (Member m : members) {
            System.out.printf("%-8s | %-20s | %-26s | %-12s | %d/%d%n",
                    m.getMemberId(), truncate(m.getName(), 20), truncate(m.getEmail(), 26),
                    m.getPhone(), m.getActiveBorrowCount(), Member.MAX_BORROW_LIMIT);
        }
    }

    private void registerNewMember() {
        System.out.println("\n--- Register New Library Member ---");
        String id = prompt("Enter Member ID (e.g. M105): ").trim();
        if (id.isEmpty()) {
            System.out.println("[!] Member ID cannot be blank.");
            return;
        }
        try {
            if (libraryService.getMember(id) != null) {
                System.out.println("[!] Member ID '" + id + "' is already registered.");
                return;
            }
        } catch (LibraryException ignored) {}

        String name = prompt("Enter Full Name: ").trim();
        String email = prompt("Enter Email: ").trim();
        String phone = prompt("Enter Phone Number: ").trim();

        Member m = new Member(id, name, email, phone);
        libraryService.registerMember(m);
        System.out.println("[SUCCESS] Member '" + name + "' registered successfully with ID: " + id);
    }

    private void searchMemberName() {
        String q = prompt("Enter member name keyword: ");
        List<Member> matches = libraryService.searchMembersByName(q);
        System.out.println("\nSearch Results (" + matches.size() + " matches found):");
        for (Member m : matches) {
            System.out.println(" - " + m);
        }
    }

    private void removeMember() {
        String id = prompt("Enter Member ID to remove: ").trim();
        try {
            libraryService.removeMember(id);
            System.out.println("[SUCCESS] Member " + id + " removed successfully.");
        } catch (LibraryException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    // ================= Circulation (Issue & Return) =================

    private void handleCirculationMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- [ CIRCULATION & LOANS ] ---");
            System.out.println("1. Issue / Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View Active Loans");
            System.out.println("4. View Overdue Loans & Fines");
            System.out.println("5. View Full Transaction History");
            System.out.println("6. Back to Main Menu");

            String ch = prompt("Select an option (1-6): ");
            switch (ch) {
                case "1":
                    borrowBookFlow();
                    break;
                case "2":
                    returnBookFlow();
                    break;
                case "3":
                    listActiveLoans();
                    break;
                case "4":
                    listOverdueLoans();
                    break;
                case "5":
                    listAllTransactions();
                    break;
                case "6":
                    inMenu = false;
                    break;
                default:
                    System.out.println("[!] Invalid option.");
            }
        }
    }

    private void borrowBookFlow() {
        System.out.println("\n--- Issue / Borrow Book ---");
        String bookId = prompt("Enter Book ID: ").trim();
        String memberId = prompt("Enter Member ID: ").trim();

        try {
            Transaction tx = libraryService.borrowBook(bookId, memberId);
            System.out.println("\n[SUCCESS] Book successfully issued!");
            System.out.println("    Transaction ID : " + tx.getTransactionId());
            System.out.println("    Book ID        : " + tx.getBookId());
            System.out.println("    Member ID      : " + tx.getMemberId());
            System.out.println("    Borrow Date    : " + tx.getBorrowDate());
            System.out.println("    Due Date       : " + tx.getDueDate() + " (Loan Period: 14 days)");
        } catch (LibraryException e) {
            System.out.println("\n[!] Transaction Failed: " + e.getMessage());
        }
    }

    private void returnBookFlow() {
        System.out.println("\n--- Return Book ---");
        String txId = prompt("Enter Transaction ID (e.g. TX1001): ").trim();

        try {
            Transaction tx = libraryService.returnBook(txId);
            double fine = tx.calculateFine();
            System.out.println("\n[SUCCESS] Book successfully returned!");
            System.out.println("    Transaction ID : " + tx.getTransactionId());
            System.out.println("    Return Date    : " + tx.getReturnDate());
            if (fine > 0) {
                System.out.printf("    [!] OVERDUE FINE APPLIED: Rs. %.2f (Days overdue: %d)%n",
                        fine, tx.getDaysOverdue());
            } else {
                System.out.println("    [SUCCESS] Returned on time. No overdue fines.");
            }
        } catch (Exception e) {
            System.out.println("\n[!] Return Failed: " + e.getMessage());
        }
    }

    private void listActiveLoans() {
        List<Transaction> active = libraryService.getActiveTransactions();
        System.out.println("\n---------------- ACTIVE LOANS (" + active.size() + ") ----------------");
        if (active.isEmpty()) {
            System.out.println("No active book loans at present.");
            return;
        }
        for (Transaction t : active) {
            System.out.println(t);
        }
    }

    private void listOverdueLoans() {
        List<Transaction> overdue = libraryService.getOverdueTransactions();
        System.out.println("\n---------------- OVERDUE LOANS & FINES (" + overdue.size() + ") ----------------");
        if (overdue.isEmpty()) {
            System.out.println("[SUCCESS] Great! There are no overdue loans currently.");
            return;
        }
        for (Transaction t : overdue) {
            System.out.printf("TX: %s | Book: %s | Member: %s | Due: %s | Overdue: %d days | Fine: Rs. %.2f%n",
                    t.getTransactionId(), t.getBookId(), t.getMemberId(),
                    t.getDueDate(), t.getDaysOverdue(), t.calculateFine());
        }
    }

    private void listAllTransactions() {
        List<Transaction> all = libraryService.getAllTransactions();
        System.out.println("\n---------------- TRANSACTION HISTORY (" + all.size() + ") ----------------");
        for (Transaction t : all) {
            System.out.println(t);
        }
    }

    // ================= Analytics & Dashboard =================

    private void handleAnalytics() {
        Map<String, Object> summary = libraryService.getSystemSummary();
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("|          LIBRARY SYSTEM ANALYTICS SUMMARY          |");
        System.out.println("+----------------------------------------------------+");
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            System.out.printf("|  %-28s : %-17s |%n", entry.getKey(), entry.getValue());
        }
        System.out.println("+----------------------------------------------------+");
    }

    private void displayQuickHelp() {
        System.out.println("\n========================================================");
        System.out.println("  VITyarthi Project: Library Management System");
        System.out.println("  Built in accordance with VITyarthi Project Guidelines.");
        System.out.println("  Concepts Applied:");
        System.out.println("   - Object-Oriented Programming (OOP) Encapsulation & Inheritance");
        System.out.println("   - Collections Framework (Maps, Lists, Stream API)");
        System.out.println("   - Custom Java Exception Hierarchy");
        System.out.println("   - Persistent CSV File Storage");
        System.out.println("   - Date and Time API (java.time.LocalDate)");
        System.out.println("========================================================");
    }

    // Helpers
    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private int promptInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid whole number.");
            }
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
