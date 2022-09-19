package dsenta.cachito.exception;

public class AttributeDoesNotExistException extends IllegalArgumentException {
    public AttributeDoesNotExistException(String attribute) {
        super(String.format("Attribute %s does not exist", attribute));
    }
}
