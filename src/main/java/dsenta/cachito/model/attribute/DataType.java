package dsenta.cachito.model.attribute;

import dsenta.cachito.constants.attribute.DataTypeConstants;
import dsenta.cachito.exception.UnsupportedDataTypeException;
import dsenta.queryablemap.exception.ShouldNeverHappenException;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Objects.isNull;

public enum DataType {
    BOOLEAN,
    INTEGER,
    FLOAT,
    STRING,
    DATE,
    RELATIONSHIP_ZERO_ONE,
    RELATIONSHIP_ONE,
    RELATIONSHIP_ZERO_MANY,
    RELATIONSHIP_ONE_MANY;

    private static final Map<String, DataType> PRIMITIVE_CLASS_TO_DATA_TYPE = Map.ofEntries(
            entry(Boolean.class.getSimpleName(), BOOLEAN),
            entry(boolean.class.getSimpleName(), BOOLEAN),
            entry(Integer.class.getSimpleName(), INTEGER),
            entry(Long.class.getSimpleName(), INTEGER),
            entry(int.class.getSimpleName(), INTEGER),
            entry(long.class.getSimpleName(), INTEGER),
            entry(Float.class.getSimpleName(), FLOAT),
            entry(Double.class.getSimpleName(), FLOAT),
            entry(float.class.getSimpleName(), FLOAT),
            entry(double.class.getSimpleName(), FLOAT),
            entry(String.class.getSimpleName(), STRING),
            entry(Date.class.getSimpleName(), DATE),
            entry(Instant.class.getSimpleName(), DATE)
    );

    public static boolean isRelationship(DataType dataType) {
        switch (dataType) {
            case RELATIONSHIP_ZERO_ONE:
            case RELATIONSHIP_ONE:
            case RELATIONSHIP_ZERO_MANY:
            case RELATIONSHIP_ONE_MANY:
                return true;
            default:
                return false;
        }
    }

    public static DataType fromString(String token) {
        switch (token.toLowerCase()) {
            case DataTypeConstants.BOOL:
            case DataTypeConstants.BOOLEAN:
                return BOOLEAN;
            case DataTypeConstants.INT:
            case DataTypeConstants.INTEGER:
            case DataTypeConstants.LONG:
                return INTEGER;
            case DataTypeConstants.FLOAT:
            case DataTypeConstants.DOUBLE:
                return FLOAT;
            case DataTypeConstants.STRING:
                return STRING;
            case DataTypeConstants.DATE:
                return DATE;
            case DataTypeConstants.ZEROONE:
                return RELATIONSHIP_ZERO_ONE;
            case DataTypeConstants.ZEROMANY:
                return RELATIONSHIP_ZERO_MANY;
            case DataTypeConstants.ONEMANY:
                return RELATIONSHIP_ONE_MANY;
            case DataTypeConstants.ONE:
                return RELATIONSHIP_ONE;
            default:
                throw new UnsupportedDataTypeException(token);
        }
    }

    public static DataType fromClass(Class<?> aClass) {
        var typeName = aClass.getSimpleName();
        var dataType = PRIMITIVE_CLASS_TO_DATA_TYPE.getOrDefault(typeName, null);

        if (isNull(dataType)) {
            throw new ShouldNeverHappenException();
        }

        return dataType;
    }
}
