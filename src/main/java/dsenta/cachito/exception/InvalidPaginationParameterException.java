package dsenta.cachito.exception;

public class InvalidPaginationParameterException extends IllegalArgumentException {
    public InvalidPaginationParameterException(String paramName, int value) {
        super(String.format("Invalid pagination parameter \"%s\" = %d", paramName, value));
    }
}