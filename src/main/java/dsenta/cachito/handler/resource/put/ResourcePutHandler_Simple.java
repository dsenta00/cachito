package dsenta.cachito.handler.resource.put;

import dsenta.cachito.exception.entity.FailedToFetchEntityException;
import dsenta.cachito.exception.resource.IdNotProvidedException;
import dsenta.cachito.factory.objectinstance.ObjectInstanceFactory;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.CustomObjectMapper;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Map.Entry;

import static dsenta.cachito.handler.attribute.AttributeHandler_Simple.removeIdFromResourceDimensions;
import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.insertIdIntoDimensions;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper_Simple.toEntity;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourcePutHandler_Simple {

    public static <T> Entity put(Resource resource, T object) {
        return put(resource, CustomObjectMapper.convert(object));
    }

    public static <T> Entity put(Resource resource, T object, FieldsToDisplay fieldsToDisplay) {
        return put(resource, CustomObjectMapper.convert(object), fieldsToDisplay);
    }

    public static Entity put(Resource resource, Map<String, Object> object) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        Long id = Long.valueOf(object.get("id").toString());

        return toEntity(put(resource, object, id), resource)
                .orElseThrow(FailedToFetchEntityException::new);
    }

    public static Entity put(Resource resource, Map<String, Object> object, FieldsToDisplay fieldsToDisplay) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        Long id = Long.valueOf(object.get("id").toString());

        return toEntity(put(resource, object, id), resource)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .orElseThrow(FailedToFetchEntityException::new);
    }

    public static Entry<Long, ObjectInstance> put(Resource resource, Map<String, Object> object, Long id) {
        var entry = resource.getObjectInstances().getByKey(id);
        var updatedObjectInstance = ObjectInstanceFactory.create(resource.getClazz(), object);

        removeIdFromResourceDimensions(resource, id, entry.getValue());
        entry.getValue().setProperties(updatedObjectInstance.getProperties().toArray());
        insertIdIntoDimensions(resource, object, id);

        return entry;
    }
}