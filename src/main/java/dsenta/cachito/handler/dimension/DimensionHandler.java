package dsenta.cachito.handler.dimension;

import dsenta.cachito.exception.FilterByNullValueException;
import dsenta.cachito.exception.RelationshipOneManyNoObjectException;
import dsenta.cachito.exception.RelationshipOneNoObjectException;
import dsenta.cachito.exception.attribute.AttributeDoesNotExistException;
import dsenta.cachito.exception.attribute.AttributeNotProvidedException;
import dsenta.cachito.exception.attribute.AttributeNotRelationshipException;
import dsenta.cachito.exception.attribute.UnsupportedDataTypeException;
import dsenta.cachito.exception.dimension.AddingNullToDimensionException;
import dsenta.cachito.exception.dimension.DimensionDoesNotExistException;
import dsenta.cachito.exception.resource.ResourceDoesNotMatchOnLeftJoinException;
import dsenta.cachito.exception.resource.ResourceNotFoundException;
import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.mapper.dimension.IdResultFlatMapper;
import dsenta.cachito.mapper.objectinstance.IntMapper;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.dimension.Dimension;
import dsenta.cachito.model.dimension.Dimensions;
import dsenta.cachito.model.dimension.RelationshipDimension;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.filter.AndWhere;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.group.GroupResult;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.leftjoin.LeftJoinWithClazz;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import dsenta.cachito.utils.CustomObjectMapper;
import dsenta.queryablemap.exception.ShouldNeverHappenException;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static dsenta.cachito.factory.dimension.DimensionFactory.convertDimension;
import static dsenta.cachito.factory.dimension.DimensionFactory.createDimension;
import static dsenta.cachito.handler.resource.post.ResourcePostHandler.post;
import static dsenta.cachito.model.attribute.DataType.isRelationship;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DimensionHandler {

    public static Dimensions getDimensions(Resource resource, Persistence persistence) {
        var dimensions = new Dimensions();

        while (true) {
            dimensions.putAll(
                    resource
                            .getDimensions()
                            .entrySet()
                            .stream()
                            .filter(dimensionEntry -> !dimensions.containsKey(dimensionEntry.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );

            if (isNull(resource.getClazz().getParentClazz())) {
                return dimensions;
            }

            resource = PersistableResource.get(resource.getClazz().getParentClazz(), persistence);
        }
    }

    public static List<GroupResult> groupByNoInterval(Dimension<?> dimension, GroupBy groupBy) {
        var valueIdsMap = dimension.getValueIdsMap();
        var entries = groupBy.isAsc() ? valueIdsMap.getAsc() : valueIdsMap.getDesc();

        return entries.stream()
                .map(e -> new GroupResult(e.getKey(), e.getKey(), e.getKey().toString(), new ArrayList<>(e.getValue())))
                .collect(Collectors.toList());
    }

    public static List<GroupResult> groupFromDimensions(Clazz clazz,
                                                        GroupBy groupBy,
                                                        Dimensions dimensions,
                                                        Persistence persistence,
                                                        FieldsToDisplay fieldsToDisplay) {
        int indexOfDot = groupBy.getAttribute().indexOf('.');

        if (indexOfDot == -1) {
            Optional<Attribute> attributeOptional = clazz.getAttribute(groupBy.getAttribute());

            if (attributeOptional.isEmpty()) {
                if (isNull(clazz.getParentClazz())) {
                    throw new AttributeDoesNotExistException(groupBy.getAttribute());
                }

                return groupFromDimensions(
                        clazz.getParentClazz(),
                        groupBy,
                        dimensions,
                        persistence,
                        fieldsToDisplay
                );
            }

            Attribute attribute = attributeOptional.get();
            if (!dimensions.containsKey(groupBy.getAttribute())) {
                throw new DimensionDoesNotExistException(attribute);
            }

            Dimension<?> dimension = dimensions.get(groupBy.getAttribute());
            List<GroupResult> groupResults = groupFromDimension(groupBy, attribute, dimension);

            if (dimension.isRelationship()) {
                var relatedResource = PersistableResource.get(attribute.getClazz(), persistence);
                groupResults = groupResults
                        .stream()
                        .peek(groupResult -> {
                            var id = (Long) groupResult.getFrom();
                            var fieldsOfAttributeToDisplay = fieldsToDisplay.getFieldsOfAttributeToDisplay().containsKey(attribute.getName()) ?
                                    fieldsToDisplay.getFieldsOfAttributeToDisplay().get(attribute.getName()) :
                                    FieldsToDisplay.all();

                            groupResult.setFrom(
                                    ResourceGetHandler
                                            .getById(relatedResource, id, persistence, fieldsOfAttributeToDisplay)
                                            .orElseThrow(() -> new ResourceNotFoundException(attribute.getClazz().getName(), id))
                            );
                        })
                        .collect(Collectors.toList());
            }

            return groupResults;
        } else {
            String field = groupBy.getAttribute().substring(0, indexOfDot);

            Clazz relatedClazz = clazz
                    .getAttribute(field)
                    .orElseThrow(() -> new AttributeDoesNotExistException(field))
                    .getClazz();

            Dimension<?> dimension = dimensions.get(field);

            if (isNull(dimension)) {
                throw new DimensionDoesNotExistException(field);
            }

            if (!dimension.isRelationship()) {
                throw new AttributeNotRelationshipException(field, dimension.getDataType());
            }

            groupBy.setAttribute(groupBy.getAttribute().substring(indexOfDot + 1));

            return groupFromDimensions(
                    relatedClazz,
                    groupBy,
                    PersistableResource.get(relatedClazz, persistence).getDimensions(),
                    persistence,
                    fieldsToDisplay.getFieldsOfAttributeToDisplay().get(field)
            )
                    .stream()
                    .peek(groupResult -> groupResult.setIds(
                                    dimension
                                            .toRelationshipZeroManyDimension()
                                            .getKeysListRelatedWithIds(groupResult.getIds())
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .distinct()
                                            .collect(Collectors.toList())
                            )
                    )
                    .collect(Collectors.toList());
        }
    }

    public static List<Long> leftJoinFromDimensions(Clazz clazz,
                                                    LeftJoinWithClazz leftJoin,
                                                    Persistence persistence) {
        Clazz leftJoinedClazz = leftJoin.getClazz();

        Attribute relationshipAttribute = leftJoinedClazz
                .getAttribute(leftJoin.getAttribute())
                .orElseThrow(() -> new AttributeDoesNotExistException(leftJoin.getAttribute()));

        if (isNull(relationshipAttribute.getClazz())) {
            throw new AttributeNotRelationshipException(relationshipAttribute);
        }

        if (relationshipAttribute.getClazz().compareTo(clazz) != 0) {
            throw new ResourceDoesNotMatchOnLeftJoinException(clazz, relationshipAttribute);
        }

        Resource leftJoinedResource = PersistableResource.get(leftJoinedClazz, persistence);
        Dimensions leftJoinedDimensions = getDimensions(leftJoinedResource, persistence);
        Dimension<?> relationshipDimension = leftJoinedDimensions.get(leftJoin.getAttribute());

        if (isNull(relationshipDimension)) {
            throw new DimensionDoesNotExistException(leftJoin.getAttribute());
        }

        if (!relationshipDimension.isRelationship()) {
            throw new AttributeNotRelationshipException(relationshipAttribute);
        }

        List<Long> leftJoinedResourceIds = filterFromDimensions(
                leftJoinedClazz,
                new Filter(leftJoin.getWhere()),
                leftJoinedDimensions,
                persistence
        );

        return leftJoinedResourceIds
                .stream()
                .map(relationshipDimension::getEquals)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static List<Long> filterFromDimensions(Clazz clazz,
                                                  Filter filter,
                                                  Dimensions dimensions,
                                                  Persistence persistence) {
        var queryResult = new LinkedList<List<List<Long>>>();

        for (var andWhere : filter.getWhere()) {
            int indexOfDot = andWhere.getAttribute().indexOf('.');

            if (indexOfDot == -1) {
                var attributeName = andWhere.getAttribute();
                var dimension = dimensions.get(attributeName);

                if (isNull(dimension)) {
                    throw new DimensionDoesNotExistException(attributeName);
                }

                queryResult.add(andWhereFromDimension(andWhere, dimension));
            } else {
                var attributeName = andWhere.getAttribute().substring(0, indexOfDot);
                var dimension = dimensions.get(attributeName);

                if (isNull(dimension)) {
                    throw new DimensionDoesNotExistException(attributeName);
                }

                if (!dimension.isRelationship()) {
                    throw new AttributeNotRelationshipException(attributeName, dimension.getDataType());
                }

                andWhere.setAttribute(andWhere.getAttribute().substring(indexOfDot + 1));

                var attribute = clazz
                        .getAttribute(attributeName)
                        .orElseThrow(() -> new AttributeDoesNotExistException(attributeName));

                var relatedFilter = new Filter();
                relatedFilter.getWhere().add(andWhere);
                queryResult.add(filterFromRelatedDimension(dimension, attribute, relatedFilter, persistence));
            }
        }

        return IdResultFlatMapper.flatMapIds(queryResult);
    }

    private static List<List<Long>> filterFromRelatedDimension(Dimension<?> dimension,
                                                               Attribute attribute,
                                                               Filter relatedFilter,
                                                               Persistence persistence) {
        var relatedClazz = attribute.getClazz();
        var relatedResource = PersistableResource.get(relatedClazz, persistence);
        List<Long> relatedIds = filterFromDimensions(
                relatedClazz,
                relatedFilter,
                getDimensions(relatedResource, persistence),
                persistence
        );

        if (dimension instanceof RelationshipDimension) {
            RelationshipDimension relationshipDimension = (RelationshipDimension) dimension;
            return relationshipDimension.getKeysListRelatedWithIds(relatedIds);
        } else {
            throw new ShouldNeverHappenException();
        }
    }

    private static List<GroupResult> groupFromDimension(GroupBy groupBy, Attribute attribute, Dimension<?> dimension) {
        switch (attribute.getDataType()) {
            case BOOLEAN:
                return BoolDimensionHandler.groupBy(dimension.toBoolDimension(), groupBy);
            case STRING:
                return StringDimensionHandler.groupBy(dimension.toStringDimension(), groupBy);
            case INTEGER:
                return IntDimensionHandler.groupBy(dimension.toIntDimension(), attribute, groupBy);
            case FLOAT:
                return FloatDimensionHandler.groupBy(dimension.toFloatDimension(), attribute, groupBy);
            case DATE:
                return DateDimensionHandler.groupBy(dimension.toDateDimension(), attribute, groupBy);
            case RELATIONSHIP_ZERO_ONE:
                return RelationshipDimensionHandler.groupBy(dimension.toRelationshipZeroOneDimension());
            case RELATIONSHIP_ONE:
                return RelationshipDimensionHandler.groupBy(dimension.toRelationshipOneDimension());
            case RELATIONSHIP_ZERO_MANY:
                return RelationshipDimensionHandler.groupBy(dimension.toRelationshipZeroManyDimension());
            case RELATIONSHIP_ONE_MANY:
                return RelationshipDimensionHandler.groupBy(dimension.toRelationshipOneManyDimension());
            default:
                throw new UnsupportedDataTypeException(attribute.getDataType());
        }
    }

    private static List<List<Long>> andWhereFromDimension(AndWhere andWhere, Dimension<?> dimension) {
        if (isNull(andWhere.getValue())) {
            throw new FilterByNullValueException(andWhere.getAttribute());
        }

        switch (andWhere.getOperator()) {
            case EQUALS:
                List<List<Long>> result = new LinkedList<>();
                result.add(dimension.getEquals(andWhere.getValue()));
                return result;
            case NOT_EQUALS:
                return dimension.getNotEquals(andWhere.getValue(), andWhere.isAsc());
            case BIGGER_THAN:
                return dimension.getBiggerThan(andWhere.getValue(), andWhere.isAsc());
            case BIGGER_THAN_EQUALS:
                return dimension.getBiggerThanOrEquals(andWhere.getValue(), andWhere.isAsc());
            case LESS_THAN:
                return dimension.getLessThan(andWhere.getValue(), andWhere.isAsc());
            case LESS_THAN_EQUALS:
                return dimension.getLessThanOrEquals(andWhere.getValue(), andWhere.isAsc());
            case BETWEEN:
                if (isNull(andWhere.getValue2())) {
                    throw new FilterByNullValueException(andWhere.getAttribute());
                }
                return dimension.getBetween(andWhere.getValue(), andWhere.getValue2(), andWhere.isAsc());
        }

        return new LinkedList<>();
    }

    private static void insertIdIntoDimension(Dimension<?> dimension,
                                              Map<String, Object> object,
                                              Long id,
                                              Attribute attribute) {
        if (!object.containsKey(attribute.getName())) {
            throw new AttributeNotProvidedException(attribute);
        }

        Object value = object.get(attribute.getName());

        if (isNull(value)) {
            throw new AddingNullToDimensionException(attribute);
        }

        dimension.put(value, id);
    }

    public static void insertIdIntoDimensions(Resource resource,
                                              Map<String, Object> object,
                                              Long id,
                                              Persistence persistence) {
        resource.streamDimensionalAttributes()
                .forEach(attribute -> {
                    var dimension = resource.getDimension(attribute.getName())
                            .orElseThrow(() -> new DimensionDoesNotExistException(attribute));

                    if (isRelationship(attribute.getDataType())) {
                        insertIdIntoRelatedDimension(dimension, object, id, attribute, persistence);
                    } else {
                        insertIdIntoDimension(dimension, object, id, attribute);
                    }
                });
    }

    public static void insertIdIntoDimensions(Resource resource,
                                              Map<String, Object> object,
                                              Long id,
                                              Persistence persistence,
                                              Set<String> dimensionNames) {
        var attributes = resource.streamDimensionalAttributes()
                .filter(attribute -> dimensionNames.contains(attribute.getName()))
                .collect(Collectors.toList());

        for (Attribute attribute : attributes) {
            Dimension<?> dimension = resource
                    .getDimension(attribute.getName())
                    .orElseThrow(() -> new DimensionDoesNotExistException(attribute));

            if (isRelationship(attribute.getDataType())) {
                insertIdIntoRelatedDimension(dimension, object, id, attribute, persistence);
            } else {
                insertIdIntoDimension(dimension, object, id, attribute);
            }
        }
    }

    public static void removeIdFromDimensions(Dimensions dimensions,
                                              Long id,
                                              ObjectInstance objectInstance,
                                              Collection<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            Dimension<?> dimension = dimensions.get(attribute.getName());

            if (isNull(dimension)) {
                throw new DimensionDoesNotExistException(attribute);
            }

            if (isRelationship(attribute.getDataType())) {
                dimension.remove(id);
            } else {
                dimension.remove(objectInstance.get(attribute), id);
            }
        }
    }

    public static void updateDimensionsByAttribute(Dimensions dimensions,
                                                   Attribute newAttribute,
                                                   Attribute oldAttribute) {
        if (oldAttribute.shouldHaveDimension()) {
            var dimension = dimensions.remove(oldAttribute.getName());

            if (isNull(dimension)) {
                throw new DimensionDoesNotExistException(oldAttribute);
            }

            if (newAttribute.shouldHaveDimension()) {
                dimensions.put(newAttribute.getName(), convertDimension(dimension, newAttribute.getDataType()));
            }
        } else if (newAttribute.shouldHaveDimension()) {
            dimensions.put(newAttribute.getName(), createDimension(newAttribute));
        }
    }

    private static void insertIdIntoRelatedDimension(Dimension<?> dimension,
                                                     Map<String, Object> object,
                                                     Long id,
                                                     Attribute relatedAttribute,
                                                     Persistence persistence) {
        if (!object.containsKey(relatedAttribute.getName())) {
            throw new AttributeNotProvidedException(relatedAttribute);
        }

        Object relatedObject = object.get(relatedAttribute.getName());

        switch (relatedAttribute.getDataType()) {
            case RELATIONSHIP_ZERO_ONE:
                insertRelationshipZeroOne(dimension, id, relatedAttribute, relatedObject, persistence);
                break;
            case RELATIONSHIP_ONE:
                insertRelationshipOne(dimension, id, relatedAttribute, relatedObject, persistence);
                break;
            case RELATIONSHIP_ZERO_MANY:
                insertRelationshipZeroMany(dimension, id, relatedAttribute, relatedObject, persistence);
                break;
            case RELATIONSHIP_ONE_MANY:
                insertRelationshipOneMany(dimension, id, relatedAttribute, relatedObject, persistence);
                break;
            default:
                break;
        }
    }

    private static void insertRelationshipZeroOne(Dimension<?> dimension,
                                                  Long id,
                                                  Attribute relatedAttribute,
                                                  Object relatedObject,
                                                  Persistence persistence) {
        if (isNull(relatedObject)) {
            dimension.put(id);
            return;
        }

        var relatedResource = PersistableResource.get(relatedAttribute.getClazz(), persistence);
        dimension.put(id, resolveIdFromRelatedObject(relatedResource, relatedObject, persistence));
    }

    private static void insertRelationshipOne(Dimension<?> dimension,
                                              Long id,
                                              Attribute relatedAttribute,
                                              Object relatedObject,
                                              Persistence persistence) {
        if (isNull(relatedObject)) {
            throw new RelationshipOneNoObjectException(relatedAttribute);
        }

        var relatedResource = PersistableResource.get(relatedAttribute.getClazz(), persistence);
        dimension.put(id, resolveIdFromRelatedObject(relatedResource, relatedObject, persistence));
    }

    private static void insertRelationshipZeroMany(Dimension<?> dimension,
                                                   Long id,
                                                   Attribute relatedAttribute,
                                                   Object relatedObject,
                                                   Persistence persistence) {
        if (isNull(relatedObject)) {
            dimension.put(id);
            return;
        }

        var relatedResource = PersistableResource.get(relatedAttribute.getClazz(), persistence);

        if (relatedObject instanceof Collection) {
            for (Object o : (Collection<?>) relatedObject) {
                dimension.put(id, resolveIdFromRelatedObject(relatedResource, o, persistence));
            }
        } else {
            dimension.put(id, resolveIdFromRelatedObject(relatedResource, relatedObject, persistence));
        }
    }

    private static void insertRelationshipOneMany(Dimension<?> dimension,
                                                  Long id,
                                                  Attribute relatedAttribute,
                                                  Object relatedObject,
                                                  Persistence persistence) {
        if (isNull(relatedObject)) {
            throw new RelationshipOneManyNoObjectException(relatedAttribute);
        }

        var relatedResource = PersistableResource.get(relatedAttribute.getClazz(), persistence);

        if (relatedObject instanceof Collection) {
            for (Object o : (Collection<?>) relatedObject) {
                dimension.put(id, resolveIdFromRelatedObject(relatedResource, o, persistence));
            }
        } else {
            dimension.put(id, resolveIdFromRelatedObject(relatedResource, relatedObject, persistence));
        }
    }

    private static Long resolveIdFromRelatedObject(Resource relatedResource,
                                                   Object relatedObject,
                                                   Persistence persistence) {
        if (relatedObject instanceof Integer || relatedObject instanceof Long) {
            return IntMapper.toLongInt(relatedObject);
        }

        var mappedObject = CustomObjectMapper.convert(relatedObject);

        if (mappedObject.containsKey("id") && nonNull(mappedObject.get("id"))) {
            return IntMapper.toLongInt(mappedObject.get("id"));
        }

        return post(relatedResource, mappedObject, persistence).getKey();
    }
}