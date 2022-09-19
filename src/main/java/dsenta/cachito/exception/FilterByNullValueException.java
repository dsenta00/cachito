package dsenta.cachito.exception;

public class FilterByNullValueException extends IllegalArgumentException {
    public FilterByNullValueException(String attribute) {
        super(String.format("Cannot filter attribute %s by null", attribute));
    }
}