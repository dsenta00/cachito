package dsenta.cachito.mapper.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.utils.DateIso8601;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityToStringMapper_Simple {

    public static String toString(List<Entity> entities) {
        return Optional.ofNullable(entities)
                .orElse(List.of())
                .stream()
                .map(EntityToStringMapper_Simple::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static String toString(Entity entity) {
        var stringBuilder = new StringBuilder("{\"id\":").append(entity.getId());
        entity.getClazz().getAttributeCollection().forEach(attribute -> toString(stringBuilder, entity, attribute));
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private static void toString(StringBuilder stringBuilder, Entity entity, Attribute attribute) {
        stringBuilder.append(",\"");
        stringBuilder.append(attribute.getName());
        stringBuilder.append("\":");
        stringBuilder.append(getValue(entity, attribute));
    }

    private static Object getValue(Entity entity, Attribute attribute) {
        switch (attribute.getDataType()) {
            case BOOLEAN:
            case INTEGER:
            case FLOAT:
                return entity.getAttributeValue(attribute.getName());
            case STRING:
                return "\"" + entity.getAttributeValue(attribute.getName()) + "\"";
            case DATE:
                return "\"" + DateIso8601.toString((Date) entity.getAttributeValue(attribute.getName())) + "\"";
            default:
                return "null";
        }
    }
}