package dsenta.cachito.handler.dimension;

import dsenta.cachito.mapper.dimension.IdResultFlatMapper;
import dsenta.cachito.model.dimension.Dimension;
import dsenta.cachito.model.dimension.Dimensions;
import dsenta.cachito.exception.AddingNullToDimensionException;
import dsenta.cachito.exception.AttributeNotProvidedException;
import dsenta.cachito.exception.DimensionDoesNotExistException;
import dsenta.cachito.exception.FilterByNullValueException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.filter.AndWhere;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static dsenta.cachito.factory.dimension.DimensionFactory.convertDimension;
import static dsenta.cachito.factory.dimension.DimensionFactory.createDimension;
import static dsenta.cachito.model.attribute.DataType.isRelationship;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DimensionHandler_Simple {

    public static Dimensions getDimensions(Resource resource) {
        return resource.getDimensions();
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

    public static void updateDimensionsByAttribute(Dimensions dimensions, Attribute newAttribute, Attribute oldAttribute) {
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

    public static List<Long> filterFromDimensions(Filter filter, Dimensions dimensions) {
        var queryResult = new LinkedList<List<List<Long>>>();

        for (var andWhere : filter.getWhere()) {
            var attributeName = andWhere.getAttribute();
            var dimension = dimensions.get(attributeName);

            if (isNull(dimension)) {
                throw new DimensionDoesNotExistException(attributeName);
            }

            queryResult.add(andWhereFromDimension(andWhere, dimension));
        }

        return IdResultFlatMapper.flatMapIds(queryResult);
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

    public static void insertIdIntoDimensions(Resource resource, Map<String, Object> object, Long id) {
        var attributes = resource.streamDimensionalAttributes().collect(Collectors.toList());

        for (Attribute attribute : attributes) {
            Dimension<?> dimension = resource
                    .getDimension(attribute.getName())
                    .orElseThrow(() -> new DimensionDoesNotExistException(attribute));

            insertIdIntoDimension(dimension, object, id, attribute);
        }
    }

    public static void insertIdIntoDimensions(Resource resource, Map<String, Object> object, Long id, Set<String> dimensionNames) {
        var attributes = resource.streamDimensionalAttributes()
                .filter(attribute -> dimensionNames.contains(attribute.getName()))
                .collect(Collectors.toList());

        for (Attribute attribute : attributes) {
            Dimension<?> dimension = resource
                    .getDimension(attribute.getName())
                    .orElseThrow(() -> new DimensionDoesNotExistException(attribute));

            insertIdIntoDimension(dimension, object, id, attribute);
        }
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
}