package storage;

import model.Book;
import model.Member;
import model.Transaction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

/**
 * Handles persistent file storage using CSV format.
 * Ensures data survives system restarts and seeds initial data if missing.
 */
public class FileStorageService {

    private final String dataDir;
    private final String booksFile;
    private final String membersFile;
    private final String transactionsFile;

    public FileStorageService(String dataDir) {
        this.dataDir = dataDir;
        this.booksFile = dataDir + File.separator + "books.csv";
        this.membersFile = dataDir + File.separator + "members.csv";
        this.transactionsFile = dataDir + File.separator + "transactions.csv";
        initDataDirectory();
    }

    private void initDataDirectory() {
        try {
            Path path = Paths.get(dataDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Warning: Unable to create data directory: " + e.getMessage());
        }
    }

    // ================= Books Persistence =================

    public List<Book> loadBooks() {
        List<Book> list = new ArrayList<>();
        File file = new File(booksFile);
        if (!file.exists() || file.length() == 0) {
            list = getSeedBooks();
            saveBooks(list);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip CSV header
                }
                Book b = Book.fromCsv(line);
                if (b != null) list.add(b);
            }
        } catch (IOException e) {
            System.err.println("Error reading books data: " + e.getMessage());
        }
        return list;
    }

    public void saveBooks(Collection<Book> books) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(booksFile))) {
            writer.println("bookId,title,author,genre,totalCopies,availableCopies");
            for (Book b : books) {
                writer.println(b.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Error saving books data: " + e.getMessage());
        }
    }

    // ================= Members Persistence =================

    public List<Member> loadMembers() {
        List<Member> list = new ArrayList<>();
        File file = new File(membersFile);
        if (!file.exists() || file.length() == 0) {
            list = getSeedMembers();
            saveMembers(list);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Member m = Member.fromCsv(line);
                if (m != null) list.add(m);
            }
        } catch (IOException e) {
            System.err.println("Error reading members data: " + e.getMessage());
        }
        return list;
    }

    public void saveMembers(Collection<Member> members) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(membersFile))) {
            writer.println("memberId,name,email,phone,activeBorrowCount");
            for (Member m : members) {
                writer.println(m.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Error saving members data: " + e.getMessage());
        }
    }

    // ================= Transactions Persistence =================

    public List<Transaction> loadTransactions() {
        List<Transaction> list = new ArrayList<>();
        File file = new File(transactionsFile);
        if (!file.exists() || file.length() == 0) {
            list = getSeedTransactions();
            saveTransactions(list);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Transaction t = Transaction.fromCsv(line);
                if (t != null) list.add(t);
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions data: " + e.getMessage());
        }
        return list;
    }

    public void saveTransactions(Collection<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile))) {
            writer.println("transactionId,bookId,memberId,borrowDate,dueDate,returnDate,status");
            for (Transaction t : transactions) {
                writer.println(t.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions data: " + e.getMessage());
        }
    }

    // Seed Data
    private List<Book> getSeedBooks() {
        List<Book> list = new ArrayList<>();
        list.add(new Book("B101", "Introduction to Java Programming", "Y. Daniel Liang", "Technology", 5, 4));
        list.add(new Book("B102", "Effective Java", "Joshua Bloch", "Technology", 3, 2));
        list.add(new Book("B103", "Clean Code", "Robert C. Martin", "Technology", 4, 4));
        list.add(new Book("B104", "Data Structures and Algorithms", "Robert Lafore", "Education", 4, 3));
        list.add(new Book("B105", "Design Patterns", "Erich Gamma", "Software Engineering", 2, 2));
        list.add(new Book("B106", "Operating System Concepts", "Silberschatz", "Education", 3, 3));
        list.add(new Book("B107", "Database System Concepts", "Korth & Sudarshan", "Education", 3, 3));
        return list;
    }

    private List<Member> getSeedMembers() {
        List<Member> list = new ArrayList<>();
        list.add(new Member("M101", "Aarav Sharma", "aarav.s@vit.ac.in", "9876543210", 1));
        list.add(new Member("M102", "Priya Patel", "priya.p@vit.ac.in", "9123456780", 1));
        list.add(new Member("M103", "Rohan Mehta", "rohan.m@vit.ac.in", "9988776655", 0));
        list.add(new Member("M104", "Sneha Rao", "sneha.r@vit.ac.in", "9765432109", 0));
        return list;
    }

    private List<Transaction> getSeedTransactions() {
        List<Transaction> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        // B101 borrowed by M101
        list.add(new Transaction("TX1001", "B101", "M101", now.minusDays(5)));
        // B102 borrowed by M102 (overdue example for demonstration)
        list.add(new Transaction("TX1002", "B102", "M102", now.minusDays(20)));
        return list;
    }
}
