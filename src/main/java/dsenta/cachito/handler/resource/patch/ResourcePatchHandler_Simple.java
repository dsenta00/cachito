package dsenta.cachito.handler.resource.patch;

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
public final class ResourcePatchHandler_Simple {

    public static <T> Entity patch(Resource resource, T object) {
        return patch(resource, CustomObjectMapper.convert(object));
    }

    public static <T> Entity patch(Resource resource, T object, FieldsToDisplay fieldsToDisplay) {
        return patch(resource, CustomObjectMapper.convert(object), fieldsToDisplay);
    }

    public static Entity patch(Resource resource, Map<String, Object> object) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        long id = Long.parseLong(object.get("id").toString());

        return toEntity(patch(resource, object, id), resource)
                .orElseThrow(FailedToFetchEntityException::new);
    }

    public static Entity patch(Resource resource, Map<String, Object> object, FieldsToDisplay fieldsToDisplay) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        long id = Long.parseLong(object.get("id").toString());

        return toEntity(patch(resource, object, id), resource)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .orElseThrow(FailedToFetchEntityException::new);
    }

    private static Entry<Long, ObjectInstance> patch(Resource resource, Map<String, Object> object, Long id) {
        var clazz = resource.getClazz();
        var entry = resource.getObjectInstances().getByKey(id);
        var objectInstance = resource.getObjectInstances().getByKey(id).getValue();

        var patchedObjectInstance = ObjectInstanceFactory.patch(objectInstance, clazz, object);

        removeIdFromResourceDimensions(resource, id, entry.getValue(), object.keySet());
        entry.getValue().setProperties(patchedObjectInstance.getProperties().toArray());
        insertIdIntoDimensions(resource, object, id, object.keySet());

        return entry;
    }
}