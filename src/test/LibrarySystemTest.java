package test;

import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.BorrowLimitExceededException;
import exception.MemberNotFoundException;
import model.Book;
import model.Member;
import model.Transaction;
import service.LibraryService;

import java.io.File;
import java.time.LocalDate;

/**
 * Automated Validation and Unit Test Suite for the Library Management System.
 * Verifies core business rules, domain invariants, and exception handling.
 */
public class LibrarySystemTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  RUNNING AUTOMATED LIBRARY SYSTEM TEST SUITE    ");
        System.out.println("=================================================");

        String testDataDir = "test_data_" + System.currentTimeMillis();
        new File(testDataDir).mkdirs();

        try {
            testBookInventory();
            testMemberBorrowLimit();
            testBorrowAndReturnFlow(testDataDir);
            testBookNotAvailableException(testDataDir);
            testMemberBorrowLimitException(testDataDir);
            testInvalidBookOrMemberException(testDataDir);
            testOverdueFineCalculation();
            testPersistenceIntegrity(testDataDir);

            System.out.println("=================================================");
            System.out.println("  TEST RESULTS: " + testsPassed + " PASSED, " + testsFailed + " FAILED");
            System.out.println("=================================================");

            if (testsFailed > 0) {
                System.exit(1);
            }
        } finally {
            // Clean up test temp files
            deleteDir(new File(testDataDir));
        }
    }

    private static void assertTrue(String testName, boolean condition, String message) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("[FAIL] " + testName + " - " + message);
            testsFailed++;
        }
    }

    private static void testBookInventory() {
        Book b = new Book("T1", "Java Concurrency", "Brian Goetz", "Tech", 2, 2);
        assertTrue("Book initialization available copies", b.getAvailableCopies() == 2, "Expected 2 copies");
        b.borrowCopy();
        assertTrue("Book borrow decrements available", b.getAvailableCopies() == 1, "Expected 1 copy remaining");
        b.borrowCopy();
        assertTrue("Book second borrow decrements", b.getAvailableCopies() == 0, "Expected 0 copies remaining");
        assertTrue("Book isAvailable returns false when 0", !b.isAvailable(), "Should not be available");
        b.returnCopy();
        assertTrue("Book return increments available", b.getAvailableCopies() == 1, "Expected 1 copy after return");
    }

    private static void testMemberBorrowLimit() {
        Member m = new Member("M99", "Test Member", "test@vit.ac.in", "9999999999");
        assertTrue("New member can borrow", m.canBorrow(), "New member should be allowed to borrow");
        m.incrementBorrowCount();
        m.incrementBorrowCount();
        m.incrementBorrowCount();
        assertTrue("Member at limit (3) cannot borrow", !m.canBorrow(), "Member at max limit should not borrow");
        m.decrementBorrowCount();
        assertTrue("Member below limit can borrow again", m.canBorrow(), "Should be allowed after return");
    }

    private static void testBorrowAndReturnFlow(String testDir) {
        LibraryService svc = new LibraryService(testDir);
        Book b = new Book("B901", "Algorithms in Java", "Sedgewick", "CS", 1);
        Member m = new Member("M901", "John Doe", "john@example.com", "1234567890");
        svc.addBook(b);
        svc.registerMember(m);

        try {
            Transaction tx = svc.borrowBook("B901", "M901");
            assertTrue("Borrow creates active transaction", tx.getStatus() == Transaction.Status.ACTIVE, "Status should be ACTIVE");
            assertTrue("Book available count decremented to 0", svc.getBook("B901").getAvailableCopies() == 0, "Available copies should be 0");
            assertTrue("Member borrow count incremented to 1", svc.getMember("M901").getActiveBorrowCount() == 1, "Borrow count should be 1");

            Transaction retTx = svc.returnBook(tx.getTransactionId());
            assertTrue("Return marks transaction RETURNED", retTx.getStatus() == Transaction.Status.RETURNED, "Status should be RETURNED");
            assertTrue("Book available count restored to 1", svc.getBook("B901").getAvailableCopies() == 1, "Available copies should be 1");
            assertTrue("Member borrow count decremented to 0", svc.getMember("M901").getActiveBorrowCount() == 0, "Borrow count should be 0");
        } catch (Exception e) {
            assertTrue("Borrow/Return flow without exception", false, "Unexpected exception: " + e.getMessage());
        }
    }

    private static void testBookNotAvailableException(String testDir) {
        LibraryService svc = new LibraryService(testDir);
        Book b = new Book("B902", "Single Copy Book", "Author A", "CS", 1, 0); // 0 available
        Member m = new Member("M902", "Jane Doe", "jane@example.com", "1234567890");
        svc.addBook(b);
        svc.registerMember(m);

        boolean caught = false;
        try {
            svc.borrowBook("B902", "M902");
        } catch (BookNotAvailableException e) {
            caught = true;
        } catch (Exception e) {
            caught = false;
        }
        assertTrue("Throws BookNotAvailableException when 0 copies available", caught, "Expected BookNotAvailableException");
    }

    private static void testMemberBorrowLimitException(String testDir) {
        LibraryService svc = new LibraryService(testDir);
        Book b1 = new Book("B903", "Book 1", "Author", "CS", 5);
        Member m = new Member("M903", "Heavy Reader", "reader@example.com", "1234567890", 3); // already 3
        svc.addBook(b1);
        svc.registerMember(m);

        boolean caught = false;
        try {
            svc.borrowBook("B903", "M903");
        } catch (BorrowLimitExceededException e) {
            caught = true;
        } catch (Exception e) {
            caught = false;
        }
        assertTrue("Throws BorrowLimitExceededException when member hits limit", caught, "Expected BorrowLimitExceededException");
    }

    private static void testInvalidBookOrMemberException(String testDir) {
        LibraryService svc = new LibraryService(testDir);
        boolean caughtBook = false;
        try {
            svc.getBook("NON_EXISTENT");
        } catch (BookNotFoundException e) {
            caughtBook = true;
        }
        assertTrue("Throws BookNotFoundException for unknown ID", caughtBook, "Expected BookNotFoundException");

        boolean caughtMember = false;
        try {
            svc.getMember("NON_EXISTENT");
        } catch (MemberNotFoundException e) {
            caughtMember = true;
        }
        assertTrue("Throws MemberNotFoundException for unknown ID", caughtMember, "Expected MemberNotFoundException");
    }

    private static void testOverdueFineCalculation() {
        LocalDate borrowDate = LocalDate.now().minusDays(20); // 20 days ago, due in 14 days => 6 days overdue
        Transaction tx = new Transaction("TX999", "B1", "M1", borrowDate);
        tx.refreshStatus();
        assertTrue("Transaction older than 14 days is OVERDUE", tx.isOverdue(), "Should be overdue");
        long overdueDays = tx.getDaysOverdue();
        assertTrue("Overdue days calculated correctly (6 days)", overdueDays == 6, "Expected 6 days overdue, got " + overdueDays);
        double fine = tx.calculateFine();
        assertTrue("Fine calculated correctly (6 * 5.0 = 30.0)", Math.abs(fine - 30.0) < 0.01, "Expected Rs. 30.0 fine, got " + fine);
    }

    private static void testPersistenceIntegrity(String testDir) {
        LibraryService svc1 = new LibraryService(testDir);
        svc1.addBook(new Book("PERSIST1", "Persistent Book", "Test Author", "Tech", 10, 10));
        svc1.registerMember(new Member("PM1", "Persistent Member", "pm@vit.ac.in", "1231231234"));

        // Load new service instance from same directory
        LibraryService svc2 = new LibraryService(testDir);
        try {
            Book loadedBook = svc2.getBook("PERSIST1");
            Member loadedMember = svc2.getMember("PM1");
            assertTrue("Persistence reloaded book accurately", loadedBook != null && loadedBook.getTitle().equals("Persistent Book"), "Book should match");
            assertTrue("Persistence reloaded member accurately", loadedMember != null && loadedMember.getName().equals("Persistent Member"), "Member should match");
        } catch (Exception e) {
            assertTrue("Persistence reload succeeded", false, "Failed to reload persisted data: " + e.getMessage());
        }
    }

    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) deleteDir(f);
            }
        }
        dir.delete();
    }
}
