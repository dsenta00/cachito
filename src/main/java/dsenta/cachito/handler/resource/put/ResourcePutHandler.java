package dsenta.cachito.handler.resource.put;

import dsenta.cachito.exception.FailedToFetchEntityException;
import dsenta.cachito.exception.IdNotProvidedException;
import dsenta.cachito.factory.objectinstance.ObjectInstanceFactory;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.CustomObjectMapper;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import static dsenta.cachito.assertions.clazz.ClazzAssert.idDoesNotExistInChildTable;
import static dsenta.cachito.assertions.resource.ResourceAssert.assertUniqueConstraint;
import static dsenta.cachito.factory.resource.ResourceStackFactory.createObjectInstanceWithIdStack;
import static dsenta.cachito.factory.resource.ResourceStackFactory.createResourceStack;
import static dsenta.cachito.handler.attribute.AttributeHandler.removeIdFromResourceDimensions;
import static dsenta.cachito.handler.dimension.DimensionHandler.insertIdIntoDimensions;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper.toEntity;
import static dsenta.cachito.model.fields.FieldsToDisplay.all;
import static dsenta.cachito.utils.StackUtils.cloneReversedStack;

public final class ResourcePutHandler {

    public static <T> Entity put(Resource resource, T object, Persistence persistence) {
        return put(resource, CustomObjectMapper.convert(object), persistence);
    }

    public static Entity put(Resource resource, Map<String, Object> object, Persistence persistence) {
        return put(resource, CustomObjectMapper.convert(object), persistence, all());
    }

    public static Entity put(Resource resource, Map<String, Object> object, Persistence persistence, FieldsToDisplay fieldsToDisplay) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        Long id = Long.valueOf(object.get("id").toString());
        idDoesNotExistInChildTable(resource.getClazz(), id, persistence);

        return toEntity(put(resource, object, id, persistence), resource, persistence)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .orElseThrow(FailedToFetchEntityException::new);
    }

    private static Entry<Long, ObjectInstance> put(Resource resource,
                                                   Map<String, Object> object,
                                                   Long id,
                                                   Persistence persistence) {
        var resourceStack = createResourceStack(resource, persistence);
        var objectStack = createObjectInstanceWithIdStack(resourceStack, id);
        var updatedObjectInstanceStack = createUpdatedObjectInstanceStack(resourceStack, objectStack, object, persistence);
        Entry<Long, ObjectInstance> entry;

        do {
            var currentResource = resourceStack.pop();
            entry = objectStack.pop();
            var updatedObjectInstance = updatedObjectInstanceStack.pop();

            removeIdFromResourceDimensions(currentResource, id, entry.getValue());
            entry.getValue().setProperties(updatedObjectInstance.getProperties().toArray());
            insertIdIntoDimensions(currentResource, object, id, persistence);
        } while (!resourceStack.isEmpty());

        return entry;
    }

    private static Stack<ObjectInstance> createUpdatedObjectInstanceStack(Stack<Resource> resourceStack,
                                                                          Stack<Entry<Long, ObjectInstance>> objectStack,
                                                                          Map<String, Object> object,
                                                                          Persistence persistence) {
        var objectInstanceStack = new Stack<ObjectInstance>();
        var reversedResourceStack = cloneReversedStack(resourceStack);
        var reversedObjectStack = cloneReversedStack(objectStack);

        do {
            var resource = reversedResourceStack.pop();
            var clazz = resource.getClazz();
            var id = reversedObjectStack.pop().getKey();

            var updatedObjectInstance = ObjectInstanceFactory.create(clazz, object);
            assertUniqueConstraint(resource, updatedObjectInstance, id, persistence);
            objectInstanceStack.push(updatedObjectInstance);
        } while (!reversedResourceStack.isEmpty());

        return objectInstanceStack;
    }
}