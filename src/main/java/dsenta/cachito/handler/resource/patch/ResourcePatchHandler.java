package dsenta.cachito.handler.resource.patch;

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
import static dsenta.cachito.utils.StackUtils.cloneReversedStack;

public final class ResourcePatchHandler {

    public <T> Entity patch(Resource resource, T object, Persistence persistence) {
        return patch(resource, CustomObjectMapper.convert(object), persistence, FieldsToDisplay.all());
    }

    public Entity patch(Resource resource,
                        Map<String, Object> object,
                        Persistence persistence,
                        FieldsToDisplay fieldsToDisplay) {
        if (!object.containsKey("id")) {
            throw new IdNotProvidedException();
        }

        Long id = Long.valueOf(object.get("id").toString());
        idDoesNotExistInChildTable(resource.getClazz(), id, persistence);

        return toEntity(patch(resource, object, id, persistence), resource, persistence)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .orElseThrow(FailedToFetchEntityException::new);
    }

    private static Entry<Long, ObjectInstance> patch(Resource resource,
                                                     Map<String, Object> object,
                                                     Long id,
                                                     Persistence persistence) {
        var resourceStack = createResourceStack(resource, persistence);
        var objectStack = createObjectInstanceWithIdStack(resourceStack, id);
        var patchedObjectInstanceStack = createPatchedObjectInstanceStack(resourceStack, objectStack, object, persistence);
        Entry<Long, ObjectInstance> entry;

        do {
            var currentResource = resourceStack.pop();
            entry = objectStack.pop();
            var patchedObjectInstance = patchedObjectInstanceStack.pop();

            removeIdFromResourceDimensions(currentResource, id, entry.getValue(), object.keySet());
            entry.getValue().setProperties(patchedObjectInstance.getProperties().toArray());
            insertIdIntoDimensions(currentResource, object, id, persistence, object.keySet());
        } while (!resourceStack.isEmpty());

        return entry;
    }

    private static Stack<ObjectInstance> createPatchedObjectInstanceStack(Stack<Resource> resourceStack,
                                                                          Stack<Entry<Long, ObjectInstance>> objectStack,
                                                                          Map<String, Object> object,
                                                                          Persistence persistence) {
        var objectInstanceStack = new Stack<ObjectInstance>();
        var reversedResourceStack = cloneReversedStack(resourceStack);
        var reversedObjectStack = cloneReversedStack(objectStack);

        do {
            var resource = reversedResourceStack.pop();
            var clazz = resource.getClazz();

            var wgbData = reversedObjectStack.pop();
            var id = wgbData.getKey();
            var objectInstance = wgbData.getValue();

            var patchedObjectInstance = ObjectInstanceFactory.patch(objectInstance, clazz, object);
            assertUniqueConstraint(resource, patchedObjectInstance, id, persistence);

            objectInstanceStack.push(patchedObjectInstance);
        } while (!reversedResourceStack.isEmpty());

        return objectInstanceStack;
    }
}