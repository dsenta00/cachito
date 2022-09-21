package dsenta.cachito.factory.resource;

import dsenta.cachito.exception.entity.EntityNotFoundException;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.NonPersistableResource;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.NoArgsConstructor;

import java.util.Map.Entry;
import java.util.Stack;

import static dsenta.cachito.utils.StackUtils.cloneReversedStack;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceStackFactory {

    public static Stack<Entry<Long, ObjectInstance>> createObjectInstanceWithIdStack(Stack<Resource> resourceStack,
                                                                                     Long id) {
        var objectStack = new Stack<Entry<Long, ObjectInstance>>();
        var reversedResourceStack = cloneReversedStack(resourceStack);

        do {
            var resource = reversedResourceStack.pop();
            var entry = resource.getObjectInstances().getByKey(id);
            if (isNull(entry)) {
                throw new EntityNotFoundException(id);
            }

            objectStack.push(entry);
        } while (!reversedResourceStack.isEmpty());

        return objectStack;
    }

    public static Stack<Resource> createResourceStack(Resource resource, Persistence persistence) {
        Stack<Resource> resources = new Stack<>();
        resources.push(resource);

        for (Clazz parentClazz = resource.getClazz().getParentClazz();
             nonNull(parentClazz);
             parentClazz = parentClazz.getParentClazz()) {
            resources.push(PersistableResource.get(parentClazz, persistence));
        }

        return resources;
    }

    public static Stack<Resource> createResourceStack(Resource resource) {
        Stack<Resource> resources = new Stack<>();
        resources.push(resource);

        for (Clazz parentClazz = resource.getClazz().getParentClazz();
             nonNull(parentClazz);
             parentClazz = parentClazz.getParentClazz()) {
            resources.push(NonPersistableResource.get(parentClazz));
        }

        return resources;
    }
}