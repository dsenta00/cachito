package dsenta.cachito.factory.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.attribute.DataType;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttributeFactory {

    public static Attribute asInt(String name, Integer propertyIndex, Integer defaultValue) {
        return asPrimitive(DataType.INTEGER, name, propertyIndex, defaultValue);
    }

    public static Attribute asInt(String name, Integer propertyIndex, Long defaultValue) {
        return asPrimitive(DataType.INTEGER, name, propertyIndex, defaultValue);
    }

    public static Attribute asString(String name, Integer propertyIndex, String defaultValue) {
        return asPrimitive(DataType.STRING, name, propertyIndex, defaultValue);
    }

    public static Attribute asFilterableUniqueString(String name, Integer propertyIndex, String defaultValue) {
        return asPrimitive(DataType.STRING, name, propertyIndex, defaultValue, true, true);
    }

    public static Attribute asFilterableString(String name, Integer propertyIndex, String defaultValue) {
        return asPrimitive(DataType.STRING, name, propertyIndex, defaultValue, false, true);
    }

    public static Attribute asUniqueString(String name, Integer propertyIndex, String defaultValue) {
        return asPrimitive(DataType.STRING, name, propertyIndex, defaultValue, true, false);
    }

    public static Attribute asFloat(String name, Integer propertyIndex, Float defaultValue) {
        return asPrimitive(DataType.FLOAT, name, propertyIndex, defaultValue);
    }

    public static Attribute asFloat(String name, Integer propertyIndex, Double defaultValue) {
        return asPrimitive(DataType.FLOAT, name, propertyIndex, defaultValue);
    }

    public static Attribute asFilterableFloat(String name, Integer propertyIndex, Double defaultValue) {
        return asPrimitive(DataType.FLOAT, name, propertyIndex, defaultValue, false, true);
    }

    public static Attribute asBoolean(String name, Integer propertyIndex, Boolean defaultValue) {
        return asPrimitive(DataType.BOOLEAN, name, propertyIndex, defaultValue);
    }

    public static Attribute asDate(String name, Integer propertyIndex, Date defaultValue) {
        return asPrimitive(DataType.DATE, name, propertyIndex, defaultValue);
    }

    public static Attribute asFilterableDate(String name, Integer propertyIndex, Date defaultValue) {
        return asPrimitive(DataType.DATE, name, propertyIndex, defaultValue, false, true);
    }

    public static Attribute asRelationshipOne(String name, Integer propertyIndex, Clazz clazz) {
        Attribute attribute = new Attribute();

        attribute.setDataType(DataType.RELATIONSHIP_ONE);
        attribute.setName(name);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setClazz(clazz);
        attribute.setDefaultValue(null);

        return attribute;
    }

    public static Attribute asRelationshipOneMany(String name, Integer propertyIndex, Clazz clazz) {
        Attribute attribute = new Attribute();

        attribute.setDataType(DataType.RELATIONSHIP_ONE_MANY);
        attribute.setName(name);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setClazz(clazz);
        attribute.setDefaultValue(null);

        return attribute;
    }

    public static Attribute asRelationshipZeroMany(String name, Integer propertyIndex, Clazz clazz) {
        Attribute attribute = new Attribute();

        attribute.setDataType(DataType.RELATIONSHIP_ZERO_MANY);
        attribute.setName(name);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setClazz(clazz);
        attribute.setDefaultValue(null);

        return attribute;
    }

    public static Attribute asRelationshipZeroOne(String name, Integer propertyIndex, Clazz clazz) {
        Attribute attribute = new Attribute();

        attribute.setDataType(DataType.RELATIONSHIP_ZERO_ONE);
        attribute.setName(name);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setClazz(clazz);
        attribute.setDefaultValue(null);

        return attribute;
    }

    private static Attribute asPrimitive(DataType dataType, String name, Integer propertyIndex, Object defaultValue) {
        var attribute = new Attribute();

        attribute.setDataType(dataType);
        attribute.setName(name);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setClazz(null);
        attribute.setDefaultValue(defaultValue);

        return attribute;
    }

    private static Attribute asPrimitive(DataType dataType, String name, Integer propertyIndex, Object defaultValue, boolean unique, boolean filterable) {
        var attribute = new Attribute();

        attribute.setDataType(dataType);
        attribute.setName(name);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setClazz(null);
        attribute.setDefaultValue(defaultValue);
        attribute.setUnique(unique);
        attribute.setFilterable(filterable);

        return attribute;
    }
}