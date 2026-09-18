package model;

/**
 * Represents a Member (library patron) in the Library Management System.
 * Demonstrates encapsulation, validation, and domain modeling.
 */
public class Member {

    private String memberId;
    private String name;
    private String email;
    private String phone;
    private int activeBorrowCount;

    // Maximum books allowed to be checked out simultaneously
    public static final int MAX_BORROW_LIMIT = 3;

    // Standard constructor
    public Member(String memberId, String name, String email, String phone) {
        this(memberId, name, email, phone, 0);
    }

    // Full constructor
    public Member(String memberId, String name, String email, String phone, int activeBorrowCount) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.activeBorrowCount = Math.max(0, activeBorrowCount);
    }

    // Getters and Setters
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getActiveBorrowCount() { return activeBorrowCount; }
    public void setActiveBorrowCount(int activeBorrowCount) {
        this.activeBorrowCount = Math.max(0, activeBorrowCount);
    }

    /**
     * Checks if the member is eligible to borrow more books.
     */
    public boolean canBorrow() {
        return activeBorrowCount < MAX_BORROW_LIMIT;
    }

    /**
     * Increments active borrow count when a book is checked out.
     */
    public void incrementBorrowCount() {
        activeBorrowCount++;
    }

    /**
     * Decrements active borrow count when a book is returned.
     */
    public void decrementBorrowCount() {
        if (activeBorrowCount > 0) {
            activeBorrowCount--;
        }
    }

    /**
     * Converts member entity to CSV string for persistence.
     */
    public String toCsv() {
        return String.format("%s,%s,%s,%s,%d",
            escapeCsv(memberId),
            escapeCsv(name),
            escapeCsv(email),
            escapeCsv(phone),
            activeBorrowCount
        );
    }

    /**
     * Parses a CSV row into a Member object.
     */
    public static Member fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 5) {
            String id = unescapeCsv(parts[0]);
            String name = unescapeCsv(parts[1]);
            String email = unescapeCsv(parts[2]);
            String phone = unescapeCsv(parts[3]);
            int count = Integer.parseInt(parts[4].trim());
            return new Member(id, name, email, phone, count);
        }
        return null;
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace(";", " ").replace(",", " ");
    }

    private static String unescapeCsv(String val) {
        return val == null ? "" : val.trim();
    }

    @Override
    public String toString() {
        return String.format(
            "Member[ID=%-6s | Name=%-20s | Email=%-25s | Phone=%-12s | Borrowed=%d/%d]",
            memberId, name, email, phone, activeBorrowCount, MAX_BORROW_LIMIT
        );
    }
}
