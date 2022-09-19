package dsenta.cachito.handler.attribute;

import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.exception.AttributeDoesNotExistException;
import dsenta.cachito.exception.DimensionDoesNotExistException;
import dsenta.cachito.handler.dimension.DimensionHandler;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.dimension.Dimension;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import dsenta.cachito.utils.DateIso8601;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dsenta.cachito.factory.dimension.DimensionFactory.createDimension;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AttributeHandler {

    public static void addAttributes(Resource resource, List<Attribute> attributes) {
        attributes.forEach(attribute -> addAttribute(resource, attribute));
    }

    public static void updateAttributes(Resource resource, Map<String, Attribute> attributesToUpdate) {
        attributesToUpdate.forEach((name, attribute) -> updateAttribute(resource, name, attribute));
    }

    public static void deleteAttributes(Resource resource, List<String> fieldsToDelete) {
        fieldsToDelete.forEach(fieldToDelete -> deleteAttribute(resource, fieldToDelete));
    }

    public static void removeIdFromRelatedResourceDimensions(Resource resource, Long id, Persistence persistence) {
        for (Clazz clazz : ClazzCache.stream().getRelatedClazzes(resource.getClazz())) {
            var dimensions = PersistableResource.get(clazz, persistence).getDimensions();

            clazz.getAttributeCollection().stream()
                    .filter(attribute -> DataType.isRelationship(attribute.getDataType()))
                    .filter(attribute -> resource.getClazz().compareTo(attribute.getClazz()) == 0)
                    .forEach(attribute -> {
                        Dimension<?> dimension = dimensions.getOrDefault(attribute.getName(), null);
                        if (isNull(dimension)) {
                            throw new DimensionDoesNotExistException(attribute);
                        }
                        dimension.getAsc().forEach(ids -> ids.remove(id));
                    });
        }
    }

    public static void removeIdFromResourceDimensions(Resource resource, Long id, ObjectInstance objectInstance) {
        DimensionHandler.removeIdFromDimensions(
                resource.getDimensions(),
                id,
                objectInstance,
                resource.streamDimensionalAttributes().collect(Collectors.toList())
        );
    }

    public static void removeIdFromResourceDimensions(Resource resource, Long id, ObjectInstance objectInstance, Set<String> dimensionNames) {
        var attributes = resource.streamDimensionalAttributes()
                .filter(attribute -> dimensionNames.contains(attribute.getName()))
                .collect(Collectors.toList());

        DimensionHandler.removeIdFromDimensions(
                resource.getDimensions(),
                id,
                objectInstance,
                attributes
        );
    }

    private static void addAttribute(Resource resource, Attribute attribute) {
        var clazz = resource.getClazz();
        attribute.setPropertyIndex(clazz.getAttributeCollection().size());
        clazz.getAttributes().put(attribute.getName(), attribute);

        if (attribute.shouldHaveDimension()) {
            resource.getDimensions().put(attribute.getName(), createDimension(attribute));
        }

        resource.getObjectInstances().getAsc().forEach(wgbData -> {
            List<Object> properties = wgbData.getValue().getProperties();
            properties.add(attribute.getDefaultValue());
            wgbData.getValue().setProperties(properties.toArray());
        });
    }

    private static void updateAttribute(Resource resource, String name, Attribute newAttribute) {
        var clazz = resource.getClazz();
        var attributes = clazz.getAttributes();

        var oldAttribute = attributes.get(name);
        if (Objects.isNull(oldAttribute)) {
            throw new AttributeDoesNotExistException(name);
        }

        DimensionHandler.updateDimensionsByAttribute(resource.getDimensions(), newAttribute, oldAttribute);
        convertAttributeDataType(resource, newAttribute, oldAttribute);

        newAttribute.setPropertyIndex(oldAttribute.getPropertyIndex());

        attributes.remove(name);
        attributes.put(newAttribute.getName(), newAttribute);
    }

    @SuppressWarnings("unchecked")
    private static <T> void convert(Resource resource, int propertyIndex, Class<T> oldClass, Function<T, Object> converter) {
        resource
                .getObjectInstances()
                .getAsc()
                .forEach(wgbData -> {
                    List<Object> properties = wgbData.getValue().getProperties();
                    T value = (T) properties.get(propertyIndex);
                    properties.set(propertyIndex, converter.apply(value));
                    wgbData.getValue().setProperties(properties.toArray());
                });
    }

    private static void convertAttributeDataType(Resource resource, Attribute newAttribute, Attribute oldAttribute) {
        int propertyIndex = oldAttribute.getPropertyIndex();

        switch (oldAttribute.getDataType()) {
            case BOOLEAN:
                switch (newAttribute.getDataType()) {
                    case INTEGER:
                        convert(resource, propertyIndex, Boolean.class, value -> value ? 1 : 0);
                        break;
                    case FLOAT:
                        convert(resource, propertyIndex, Boolean.class, value -> value ? 1.0 : 0.0);
                        break;
                    case STRING:
                        convert(resource, propertyIndex, Boolean.class, value -> value ? "true" : "false");
                        break;
                }
                break;
            case INTEGER:
                switch (newAttribute.getDataType()) {
                    case FLOAT:
                        convert(resource, propertyIndex, Long.class, Long::doubleValue);
                        break;
                    case STRING:
                        convert(resource, propertyIndex, Long.class, Object::toString);
                        break;
                    case DATE:
                        convert(resource, propertyIndex, Long.class, value -> Date.from(Instant.ofEpochMilli(value)));
                        break;
                }
                break;
            case FLOAT:
                if (newAttribute.getDataType() == DataType.STRING) {
                    convert(resource, propertyIndex, Double.class, Object::toString);
                }
                break;
            case DATE:
                switch (newAttribute.getDataType()) {
                    case INTEGER:
                        convert(resource, propertyIndex, Date.class, Date::getTime);
                        break;
                    case STRING:
                        convert(resource, propertyIndex, Date.class, DateIso8601::toString);
                        break;
                }
                break;
        }
    }

    private static void deleteAttribute(Resource resource, String fieldToDelete) {
        var clazz = resource.getClazz();
        var attribute = clazz.getAttribute(fieldToDelete).orElse(null);

        if (Objects.isNull(attribute)) {
            return;
        }

        resource.getDimensions().remove(attribute.getName());
        resource.getObjectInstances()
                .getAsc()
                .stream()
                .map(Map.Entry::getValue)
                .forEach(objectInstance -> {
                    List<Object> properties = objectInstance.getProperties();
                    properties.remove(attribute.getPropertyIndex());
                    objectInstance.setProperties(properties.toArray());
                });

        var attributes = clazz.getAttributeCollection();
        attributes.remove(attribute);
        attributes
                .stream()
                .filter(f -> f.getPropertyIndex() > attribute.getPropertyIndex())
                .forEach(f -> f.setPropertyIndex(f.getPropertyIndex() - 1));
    }
}
