package exception;

/**
 * Thrown when a requested book cannot be found in the catalog.
 */
public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
