package dsenta.cachito.exception.attribute;

public class AddingAttributeWithSameNameException extends IllegalArgumentException {
    public AddingAttributeWithSameNameException(String attribute) {
        super(String.format("Adding already existing attribute with the same name -> %s", attribute));
    }
}