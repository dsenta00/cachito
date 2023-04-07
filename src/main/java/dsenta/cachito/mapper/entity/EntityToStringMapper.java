package dsenta.cachito.mapper.entity;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.group.Group;
import dsenta.cachito.model.group.GroupPeriod;
import dsenta.cachito.utils.DateIso8601;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityToStringMapper {

    public static String toString(List<Entity> entities) {
        return Optional.ofNullable(entities)
                .orElse(List.of())
                .stream()
                .map(EntityToStringMapper::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static String toString(Entity entity) {
        var stringBuilder = new StringBuilder("{\"id\":").append(entity.getId());
        toStringFields(stringBuilder, entity);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public static String toString(Group group) {
        return Optional.ofNullable(group)
                .map(Group::getGroupPeriods)
                .orElse(List.of())
                .stream()
                .map(EntityToStringMapper::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static void toStringFields(StringBuilder stringBuilder, Entity entity) {
        var written = new HashSet<String>();
        for (var it = entity; nonNull(it); it = it.getParentEntity()) {
            toStringFieldsForCurrentEntity(stringBuilder, it, written);
        }
    }

    private static void toStringFieldsForCurrentEntity(StringBuilder stringBuilder, Entity entity, Set<String> written) {
        entity.getClazz().getAttributeCollection().stream()
                .filter(attribute -> !written.contains(attribute.getName()))
                .forEach(attribute -> toString(stringBuilder, entity, attribute, written));
    }

    private static void toString(StringBuilder stringBuilder, Entity entity, Attribute attribute, Set<String> written) {
        stringBuilder.append(",\"");
        stringBuilder.append(attribute.getName());
        stringBuilder.append("\":");
        stringBuilder.append(getValue(entity, attribute));
        written.add(attribute.getName());
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
            case RELATIONSHIP_ZERO_ONE:
            case RELATIONSHIP_ONE: {
                List<Entity> relatedEntities = entity.getRelatedEntitiesByFieldIndex().get(attribute.getPropertyIndex());

                if (isNull(relatedEntities) || relatedEntities.isEmpty()) {
                    return "null";
                } else {
                    return toString(relatedEntities.get(0));
                }
            }
            case RELATIONSHIP_ZERO_MANY:
            case RELATIONSHIP_ONE_MANY: {
                List<Entity> relatedEntities = entity.getRelatedEntitiesByFieldIndex().get(attribute.getPropertyIndex());
                return toString(relatedEntities);
            }
            default:
                return "null";
        }
    }

    private static String toString(GroupPeriod groupPeriod) {
        return "{\"periodName\":\"" + groupPeriod.getPeriodName() +
                "\",\"from\":" + toJsonString(groupPeriod.getFrom()) +
                ",\"to\":" + toJsonString(groupPeriod.getTo()) +
                ",\"records\":" + toString(groupPeriod.getRecords()) +
                '}';
    }

    private static String toJsonString(Object object) {
        if (isNull(object)) {
            return "null";
        }

        if (object instanceof String) {
            return String.format("\"%s\"", object);
        }

        if (object instanceof Date) {
            return String.format("\"%s\"", DateIso8601.toString((Date) object));
        }

        if (object instanceof Entity) {
            return toString((Entity) object);
        }

        return object.toString();
    }
}