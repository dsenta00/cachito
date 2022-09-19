package dsenta.cachito.handler.resource.put;

import dsenta.cachito.exception.FailedToFetchEntityException;
import dsenta.cachito.exception.IdNotProvidedException;
import dsenta.cachito.factory.objectinstance.ObjectInstanceFactory;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.CustomObjectMapper;

import java.util.Map;
import java.util.Map.Entry;

import static dsenta.cachito.handler.attribute.AttributeHandler_Simple.removeIdFromResourceDimensions;
import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.insertIdIntoDimensions;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper_Simple.toEntity;

public final class ResourcePutHandler_Simple {

    public <T> Entity put(Resource resource, T object) {
        return put(resource, CustomObjectMapper.convert(object));
    }

    public Entity put(Resource resource, Map<String, Object> object) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        Long id = Long.valueOf(object.get("id").toString());

        return toEntity(put(resource, object, id), resource)
                .orElseThrow(FailedToFetchEntityException::new);
    }

    public Entry<Long, ObjectInstance> put(Resource resource, Map<String, Object> object, Long id) {
        var entry = resource.getObjectInstances().getByKey(id);
        var updatedObjectInstance = ObjectInstanceFactory.create(resource.getClazz(), object);

        removeIdFromResourceDimensions(resource, id, entry.getValue());
        entry.getValue().setProperties(updatedObjectInstance.getProperties().toArray());
        insertIdIntoDimensions(resource, object, id);

        return entry;
    }
}