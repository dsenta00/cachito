package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.DataType;

public class UnsupportedDataTypeException extends RuntimeException {

    public UnsupportedDataTypeException(DataType type) {
        super(String.format("Unsupported data type %s", type));
    }

    public UnsupportedDataTypeException(String type) {
        super(String.format("Unsupported data type %s", type));
    }
}