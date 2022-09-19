package dsenta.cachito.handler.resource.post;

import dsenta.cachito.action.resource.ResourceAction;
import dsenta.cachito.exception.FailedToFetchEntityException;
import dsenta.cachito.exception.FailedToPersistEntityException;
import dsenta.cachito.exception.PostForceIdException;
import dsenta.cachito.factory.objectinstance.ObjectInstanceFactory;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import dsenta.cachito.utils.CustomObjectMapper;
import dsenta.cachito.utils.UUIDConverter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import static dsenta.cachito.assertions.resource.ResourceAssert.assertUniqueConstraint;
import static dsenta.cachito.factory.resource.ResourceStackFactory.createResourceStack;
import static dsenta.cachito.handler.dimension.DimensionHandler.insertIdIntoDimensions;
import static dsenta.cachito.handler.resource.delete.ResourceDeleteHandler.delete;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper.toEntity;
import static dsenta.cachito.utils.StackUtils.cloneReversedStack;
import static java.util.Map.entry;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class ResourcePostHandler {

    public <T> Entity post(Resource resource, T object, Persistence persistence) {
        return post(resource, CustomObjectMapper.convert(object), persistence, FieldsToDisplay.all());
    }

    public synchronized Entity post(Resource resource,
                                    Map<String, Object> object,
                                    Persistence persistence,
                                    FieldsToDisplay fieldsToDisplay) {
        return toEntity(post(resource, object, persistence), resource, persistence)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .orElseThrow(FailedToFetchEntityException::new);
    }

    public Entry<Long, ObjectInstance> post(Resource resource,
                                            Map<String, Object> object,
                                            Persistence persistence) {
        var resourceStack = createResourceStack(resource, persistence);
        var objectInstanceStack = createObjectInstanceStack(resourceStack, object);

        Entry<Long, ObjectInstance> objectInstanceWithId = null;

        do {
            var currentResource = resourceStack.pop();
            var objectInstance = objectInstanceStack.pop();

            if (isNull(objectInstanceWithId)) {
                objectInstanceWithId = postSingle(
                        currentResource,
                        object,
                        objectInstance,
                        persistence
                );
            } else {
                objectInstanceWithId = postSingle(
                        objectInstanceWithId.getKey(),
                        currentResource,
                        object,
                        objectInstance,
                        persistence
                );
            }
        } while (!resourceStack.isEmpty());

        return objectInstanceWithId;
    }

    private Entry<Long, ObjectInstance> postSingle(Long id,
                                                   Resource resource,
                                                   Map<String, Object> object,
                                                   ObjectInstance objectInstance,
                                                   Persistence persistence) {
        var clazz = resource.getClazz();
        var parentClazz = clazz.getParentClazz();
        var parentResource = PersistableResource.get(parentClazz, persistence);

        try {
            assertUniqueConstraint(resource, objectInstance, id, persistence);
        } catch (Exception e) {
            delete(parentResource, id, persistence);
            throw e;
        }

        var persistedInstance = resource.getObjectInstances().put(id, objectInstance);
        if (isNull(persistedInstance)) {
            delete(parentResource, id, persistence);
            throw new FailedToPersistEntityException();
        }

        insertIdIntoDimensions(resource, object, id, persistence);

        return entry(id, persistedInstance);
    }

    private Entry<Long, ObjectInstance> postSingle(Resource resource,
                                                   Map<String, Object> object,
                                                   ObjectInstance objectInstance,
                                                   Persistence persistence) {
        var idObj = object.getOrDefault("id", null);

        if (nonNull(idObj)) {
            long relatedId = UUIDConverter.asLong(idObj.toString());
            return ResourceAction.get().stream()
                    .getById(resource, relatedId, persistence)
                    .map(entity -> entry(entity.getId(), entity.getObjectInstance()))
                    .orElseThrow(() -> new PostForceIdException(relatedId, resource.getClazz().getResourceInfo().getName()));
        }

        Long id = resource.getObjectInstances().getMax();
        id = nonNull(id) ? id + 1 : 1L;

        assertUniqueConstraint(resource, objectInstance, id, persistence);
        object.put("id", id);

        var persistedInstance = resource.getObjectInstances().put(id, objectInstance);
        if (isNull(persistedInstance)) {
            throw new FailedToPersistEntityException();
        }

        insertIdIntoDimensions(resource, object, id, persistence);

        return entry(id, persistedInstance);
    }

    private Stack<ObjectInstance> createObjectInstanceStack(Stack<Resource> resourceStack,
                                                            Map<String, Object> object) {
        var objectInstanceStack = new Stack<ObjectInstance>();
        var reversedResourceStack = cloneReversedStack(resourceStack);

        do {
            var resource = reversedResourceStack.pop();
            var clazz = resource.getClazz();
            var objectInstance = ObjectInstanceFactory.create(clazz, object);
            objectInstanceStack.push(objectInstance);
        } while (!reversedResourceStack.isEmpty());

        return objectInstanceStack;
    }
}