package dsenta.cachito.handler.resource.post;

import dsenta.cachito.exception.FailedToFetchEntityException;
import dsenta.cachito.exception.FailedToPersistEntityException;
import dsenta.cachito.factory.objectinstance.ObjectInstanceFactory;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.CustomObjectMapper;
import dsenta.cachito.utils.UUIDConverter;

import java.util.Map;

import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.insertIdIntoDimensions;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper_Simple.toEntity;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ResourcePostHandler_Simple {

    public <T> Entity post(Resource resource, T object) {
        return post(resource, CustomObjectMapper.convert(object));
    }

    public synchronized Entity post(Resource resource, Map<String, Object> object) {
        var clazz = resource.getClazz();
        var objectInstance = ObjectInstanceFactory.create(clazz, object);
        long id = calculateId(resource, object);

        var persistedInstance = resource.getObjectInstances().put(id, objectInstance);
        if (isNull(persistedInstance)) {
            throw new FailedToPersistEntityException();
        }

        insertIdIntoDimensions(resource, object, id);

        return toEntity(Map.entry(id, persistedInstance), resource).orElseThrow(FailedToFetchEntityException::new);
    }

    private long calculateId(Resource resource, Map<String, Object> object) {
        if (object.containsKey("id")) {
            Object idObject = object.get("id");

            if (nonNull(idObject)) {
                return UUIDConverter.asLong(idObject.toString());
            }
        }

        Long id = resource.getObjectInstances().getMax();
        return nonNull(id) ? id + 1 : 1L;
    }
}