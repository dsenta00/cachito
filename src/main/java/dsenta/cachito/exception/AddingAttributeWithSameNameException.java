package dsenta.cachito.exception;

public class AddingAttributeWithSameNameException extends IllegalArgumentException {
    public AddingAttributeWithSameNameException(String attribute) {
        super(String.format("Adding already existing attribute with the same name -> %s", attribute));
    }
}