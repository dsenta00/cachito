package dsenta.cachito.constants.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataTypeConstants {
    public static final String BOOL = "bool";
    public static final String BOOLEAN = "boolean";
    public static final String INT = "int";
    public static final String INTEGER = "integer";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String STRING = "string";
    public static final String DATE = "date";
    public static final String ZEROONE = "zeroone";
    public static final String ZEROMANY = "zeromany";
    public static final String ONEMANY = "onemany";
    public static final String ONE = "one";

    public static boolean isPrimitiveDataType(String s) {
        switch (s.toLowerCase()) {
            case BOOL:
            case BOOLEAN:
            case INT:
            case INTEGER:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case STRING:
            case DATE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRelationshipDataType(String s) {
        switch (s.toLowerCase()) {
            case ZEROMANY:
            case ZEROONE:
            case ONE:
            case ONEMANY:
                return true;
            default:
                return false;
        }
    }
}