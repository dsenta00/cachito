package dsenta.cachito.factory.dimension;

import dsenta.cachito.exception.attribute.UnsupportedDataTypeException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.dimension.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DimensionFactory {

    public static Dimensions createDimensions(Clazz clazz) {
        var dimensions = new Dimensions();

        clazz.getAttributeCollection().stream()
                .filter(Attribute::shouldHaveDimension)
                .forEach(attribute -> dimensions.put(attribute.getName(), createDimension(attribute)));

        return dimensions;
    }

    public static Dimension<?> createDimension(Attribute attribute) {
        switch (attribute.getDataType()) {
            case BOOLEAN:
                return new BoolDimension();
            case INTEGER:
                return new IntDimension();
            case FLOAT:
                return new FloatDimension();
            case STRING:
                return new StringDimension();
            case DATE:
                return new DateDimension();
            case RELATIONSHIP_ZERO_ONE:
                return new RelationshipZeroOneDimension();
            case RELATIONSHIP_ONE:
                return new RelationshipOneDimension();
            case RELATIONSHIP_ZERO_MANY:
                return new RelationshipZeroManyDimension();
            case RELATIONSHIP_ONE_MANY:
                return new RelationshipOneManyDimension();
            default:
                throw new UnsupportedDataTypeException(attribute.getDataType());
        }
    }

    public static Dimension<?> convertDimension(Dimension<?> dimension, DataType dataType) {
        switch (dataType) {
            case BOOLEAN:
                return dimension.toBoolDimension();
            case INTEGER:
                return dimension.toIntDimension();
            case FLOAT:
                return dimension.toFloatDimension();
            case STRING:
                return dimension.toStringDimension();
            case DATE:
                return dimension.toDateDimension();
            case RELATIONSHIP_ZERO_ONE:
                return dimension.toRelationshipZeroOneDimension();
            case RELATIONSHIP_ONE:
                return dimension.toRelationshipOneDimension();
            case RELATIONSHIP_ZERO_MANY:
                return dimension.toRelationshipZeroManyDimension();
            case RELATIONSHIP_ONE_MANY:
                return dimension.toRelationshipOneManyDimension();
            default:
                throw new UnsupportedDataTypeException(dataType);
        }
    }
}