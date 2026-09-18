package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a borrow/return transaction in the Library Management System.
 * Tracks book checkout, return status, due dates, and fine calculations.
 */
public class Transaction {

    public enum Status {
        ACTIVE,   // Currently checked out
        RETURNED, // Returned successfully
        OVERDUE   // Exceeded due date without return
    }

    private String transactionId;
    private String bookId;
    private String memberId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Status status;

    // Standard loan period and fine rate
    public static final int LOAN_PERIOD_DAYS = 14;
    public static final double DAILY_FINE_RATE = 5.0; // Rs. 5 per overdue day

    // Constructor for new transaction
    public Transaction(String transactionId, String bookId, String memberId, LocalDate borrowDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(LOAN_PERIOD_DAYS);
        this.returnDate = null;
        this.status = Status.ACTIVE;
    }

    // Full constructor (for loading from storage)
    public Transaction(String transactionId, String bookId, String memberId, LocalDate borrowDate,
                       LocalDate dueDate, LocalDate returnDate, Status status) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public String getBookId() { return bookId; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public Status getStatus() { return status; }

    /**
     * Marks the transaction as returned.
     */
    public void markReturned() {
        this.returnDate = LocalDate.now();
        this.status = Status.RETURNED;
    }

    /**
     * Checks if transaction is overdue (past due date and not returned).
     */
    public boolean isOverdue() {
        return (status == Status.OVERDUE) || (status == Status.ACTIVE && LocalDate.now().isAfter(dueDate));
    }

    /**
     * Refreshes status based on whether today's date exceeds the due date.
     */
    public void refreshStatus() {
        if (status == Status.ACTIVE && LocalDate.now().isAfter(dueDate)) {
            this.status = Status.OVERDUE;
        }
    }

    /**
     * Calculates overdue days. Positive means overdue; 0 if returned or on time.
     */
    public long getDaysOverdue() {
        if (status == Status.RETURNED) {
            if (returnDate != null && returnDate.isAfter(dueDate)) {
                return ChronoUnit.DAYS.between(dueDate, returnDate);
            }
            return 0;
        }
        if (LocalDate.now().isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }

    /**
     * Calculates fine incurred for this transaction.
     */
    public double calculateFine() {
        long days = getDaysOverdue();
        return days > 0 ? days * DAILY_FINE_RATE : 0.0;
    }

    /**
     * Converts transaction to CSV string.
     */
    public String toCsv() {
        String retStr = (returnDate == null) ? "NONE" : returnDate.toString();
        return String.format("%s,%s,%s,%s,%s,%s,%s",
            transactionId,
            bookId,
            memberId,
            borrowDate.toString(),
            dueDate.toString(),
            retStr,
            status.name()
        );
    }

    /**
     * Parses a CSV row into a Transaction object.
     */
    public static Transaction fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 7) {
            String txId = parts[0].trim();
            String bId = parts[1].trim();
            String mId = parts[2].trim();
            LocalDate bDate = LocalDate.parse(parts[3].trim());
            LocalDate dDate = LocalDate.parse(parts[4].trim());
            String retPart = parts[5].trim();
            LocalDate rDate = retPart.equals("NONE") || retPart.isEmpty() ? null : LocalDate.parse(retPart);
            Status st = Status.valueOf(parts[6].trim());
            return new Transaction(txId, bId, mId, bDate, dDate, rDate, st);
        }
        return null;
    }

    @Override
    public String toString() {
        String returnInfo = (returnDate != null) ? returnDate.toString() : "Not Returned";
        return String.format(
            "Tx[ID=%-8s | Book=%-6s | Member=%-6s | Borrowed=%s | Due=%s | Returned=%-12s | Status=%-8s | Fine=Rs.%.2f]",
            transactionId, bookId, memberId, borrowDate, dueDate, returnInfo, status, calculateFine()
        );
    }
}
