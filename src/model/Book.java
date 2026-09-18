package model;

/**
 * Represents a Book in the Library Management System.
 * Demonstrates encapsulation, data validation, and OOP principles.
 */
public class Book {

    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    // Constructor with default available = total
    public Book(String bookId, String title, String author, String genre, int totalCopies) {
        this(bookId, title, author, genre, totalCopies, totalCopies);
    }

    // Full Constructor
    public Book(String bookId, String title, String author, String genre, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = Math.max(0, totalCopies);
        this.availableCopies = Math.max(0, Math.min(availableCopies, this.totalCopies));
    }

    // Getters and Setters
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
        if (this.availableCopies > totalCopies) {
            this.availableCopies = totalCopies;
        }
    }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = Math.max(0, Math.min(availableCopies, this.totalCopies));
    }

    /**
     * Checks if the book has at least one available copy.
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Decrements available copies when a book is borrowed.
     */
    public void borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    /**
     * Increments available copies when a book is returned.
     */
    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    /**
     * Converts the book entity into CSV format for persistence.
     */
    public String toCsv() {
        return String.format("%s,%s,%s,%s,%d,%d",
            escapeCsv(bookId),
            escapeCsv(title),
            escapeCsv(author),
            escapeCsv(genre),
            totalCopies,
            availableCopies
        );
    }

    /**
     * Parses a CSV row to construct a Book object.
     */
    public static Book fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length >= 6) {
            String id = unescapeCsv(parts[0]);
            String title = unescapeCsv(parts[1]);
            String author = unescapeCsv(parts[2]);
            String genre = unescapeCsv(parts[3]);
            int total = Integer.parseInt(parts[4].trim());
            int available = Integer.parseInt(parts[5].trim());
            return new Book(id, title, author, genre, total, available);
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
            "Book[ID=%-6s | Title=%-30s | Author=%-20s | Genre=%-12s | Available=%d/%d]",
            bookId, title, author, genre, availableCopies, totalCopies
        );
    }
}
