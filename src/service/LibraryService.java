package service;

import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.BorrowLimitExceededException;
import exception.MemberNotFoundException;
import model.Book;
import model.Member;
import model.Transaction;
import storage.FileStorageService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Layer orchestrating core library business logic.
 * Manages Books, Members, Circulation Transactions, and Reporting.
 */
public class LibraryService {

    private final Map<String, Book> bookCatalog;
    private final Map<String, Member> memberRegistry;
    private final Map<String, Transaction> transactionLog;
    private final FileStorageService storageService;
    private int nextTxIdNumber = 1003;

    public LibraryService(String dataDir) {
        this.storageService = new FileStorageService(dataDir);
        this.bookCatalog = new LinkedHashMap<>();
        this.memberRegistry = new LinkedHashMap<>();
        this.transactionLog = new LinkedHashMap<>();
        loadData();
    }

    private void loadData() {
        for (Book b : storageService.loadBooks()) {
            bookCatalog.put(b.getBookId().toUpperCase(), b);
        }
        for (Member m : storageService.loadMembers()) {
            memberRegistry.put(m.getMemberId().toUpperCase(), m);
        }
        for (Transaction t : storageService.loadTransactions()) {
            t.refreshStatus();
            transactionLog.put(t.getTransactionId().toUpperCase(), t);
            // track highest transaction id
            try {
                String numPart = t.getTransactionId().replaceAll("[^0-9]", "");
                if (!numPart.isEmpty()) {
                    int idNum = Integer.parseInt(numPart);
                    if (idNum >= nextTxIdNumber) {
                        nextTxIdNumber = idNum + 1;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    public void persistAll() {
        storageService.saveBooks(bookCatalog.values());
        storageService.saveMembers(memberRegistry.values());
        storageService.saveTransactions(transactionLog.values());
    }

    // ================= Book Management =================

    public void addBook(Book book) {
        if (book == null || book.getBookId() == null) return;
        bookCatalog.put(book.getBookId().toUpperCase(), book);
        storageService.saveBooks(bookCatalog.values());
    }

    public boolean removeBook(String bookId) throws BookNotFoundException {
        if (!bookCatalog.containsKey(bookId.toUpperCase())) {
            throw new BookNotFoundException("Book not found with ID: " + bookId);
        }
        bookCatalog.remove(bookId.toUpperCase());
        storageService.saveBooks(bookCatalog.values());
        return true;
    }

    public Book getBook(String bookId) throws BookNotFoundException {
        Book b = bookCatalog.get(bookId.toUpperCase());
        if (b == null) {
            throw new BookNotFoundException("Book not found with ID: " + bookId);
        }
        return b;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(bookCatalog.values());
    }

    public List<Book> searchBooksByTitle(String query) {
        String q = query.toLowerCase().trim();
        return bookCatalog.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByAuthor(String query) {
        String q = query.toLowerCase().trim();
        return bookCatalog.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByGenre(String query) {
        String q = query.toLowerCase().trim();
        return bookCatalog.values().stream()
                .filter(b -> b.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ================= Member Management =================

    public void registerMember(Member member) {
        if (member == null || member.getMemberId() == null) return;
        memberRegistry.put(member.getMemberId().toUpperCase(), member);
        storageService.saveMembers(memberRegistry.values());
    }

    public boolean removeMember(String memberId) throws MemberNotFoundException {
        if (!memberRegistry.containsKey(memberId.toUpperCase())) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
        memberRegistry.remove(memberId.toUpperCase());
        storageService.saveMembers(memberRegistry.values());
        return true;
    }

    public Member getMember(String memberId) throws MemberNotFoundException {
        Member m = memberRegistry.get(memberId.toUpperCase());
        if (m == null) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
        return m;
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(memberRegistry.values());
    }

    public List<Member> searchMembersByName(String name) {
        String q = name.toLowerCase().trim();
        return memberRegistry.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ================= Circulation / Transactions =================

    public Transaction borrowBook(String bookId, String memberId)
            throws BookNotFoundException, MemberNotFoundException,
                   BookNotAvailableException, BorrowLimitExceededException {

        Book book = getBook(bookId);
        Member member = getMember(memberId);

        if (!book.isAvailable()) {
            throw new BookNotAvailableException("No copies available for book: " + book.getTitle());
        }

        if (!member.canBorrow()) {
            throw new BorrowLimitExceededException("Member " + member.getName() +
                    " has reached maximum borrow limit (" + Member.MAX_BORROW_LIMIT + " books).");
        }

        // Perform borrow
        book.borrowCopy();
        member.incrementBorrowCount();

        String txId = "TX" + (nextTxIdNumber++);
        Transaction tx = new Transaction(txId, book.getBookId(), member.getMemberId(), LocalDate.now());
        transactionLog.put(txId, tx);

        persistAll();
        return tx;
    }

    public Transaction returnBook(String transactionId)
            throws BookNotFoundException, MemberNotFoundException {

        Transaction tx = transactionLog.get(transactionId.toUpperCase());
        if (tx == null) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }

        if (tx.getStatus() == Transaction.Status.RETURNED) {
            throw new IllegalStateException("Transaction " + transactionId + " was already returned.");
        }

        Book book = getBook(tx.getBookId());
        Member member = getMember(tx.getMemberId());

        book.returnCopy();
        member.decrementBorrowCount();
        tx.markReturned();

        persistAll();
        return tx;
    }

    public List<Transaction> getAllTransactions() {
        refreshAllTransactionStatuses();
        return new ArrayList<>(transactionLog.values());
    }

    public List<Transaction> getActiveTransactions() {
        refreshAllTransactionStatuses();
        return transactionLog.values().stream()
                .filter(t -> t.getStatus() != Transaction.Status.RETURNED)
                .collect(Collectors.toList());
    }

    public List<Transaction> getOverdueTransactions() {
        refreshAllTransactionStatuses();
        return transactionLog.values().stream()
                .filter(Transaction::isOverdue)
                .collect(Collectors.toList());
    }

    private void refreshAllTransactionStatuses() {
        for (Transaction t : transactionLog.values()) {
            t.refreshStatus();
        }
    }

    // ================= Analytics & Reporting =================

    public Map<String, Object> getSystemSummary() {
        refreshAllTransactionStatuses();
        Map<String, Object> summary = new LinkedHashMap<>();

        int totalBooks = bookCatalog.values().stream().mapToInt(Book::getTotalCopies).sum();
        int availableCopies = bookCatalog.values().stream().mapToInt(Book::getAvailableCopies).sum();
        int totalMembers = memberRegistry.size();
        long activeLoans = transactionLog.values().stream().filter(t -> t.getStatus() != Transaction.Status.RETURNED).count();
        long overdueLoans = transactionLog.values().stream().filter(Transaction::isOverdue).count();
        double totalOverdueFines = transactionLog.values().stream().mapToDouble(Transaction::calculateFine).sum();

        summary.put("Total Unique Book Titles", bookCatalog.size());
        summary.put("Total Physical Book Copies", totalBooks);
        summary.put("Available Copies in Shelf", availableCopies);
        summary.put("Total Registered Members", totalMembers);
        summary.put("Current Active Borrows", activeLoans);
        summary.put("Overdue Borrows Count", overdueLoans);
        summary.put("Total Overdue Fines (Rs.)", String.format("%.2f", totalOverdueFines));

        return summary;
    }
}
