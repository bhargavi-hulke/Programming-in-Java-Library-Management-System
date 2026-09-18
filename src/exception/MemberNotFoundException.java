package exception;

/**
 * Thrown when a requested library member cannot be found in the registry.
 */
public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
