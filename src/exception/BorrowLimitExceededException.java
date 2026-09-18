package exception;

/**
 * Thrown when a member has reached the maximum allowed borrowed books limit.
 */
public class BorrowLimitExceededException extends LibraryException {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
