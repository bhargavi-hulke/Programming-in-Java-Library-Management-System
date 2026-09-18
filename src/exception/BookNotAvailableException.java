package exception;

/**
 * Thrown when all copies of a book are currently checked out.
 */
public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
