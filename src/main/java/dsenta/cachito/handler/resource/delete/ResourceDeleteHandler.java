package dsenta.cachito.handler.resource.delete;

import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import static dsenta.cachito.assertions.clazz.ClazzAssert.idDoesNotExistInChildTable;
import static dsenta.cachito.factory.resource.ResourceStackFactory.createResourceStack;
import static dsenta.cachito.handler.attribute.AttributeHandler.removeIdFromRelatedResourceDimensions;
import static dsenta.cachito.handler.attribute.AttributeHandler.removeIdFromResourceDimensions;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceDeleteHandler {

    public static void delete(Resource resource, Long id, Persistence persistence) {
        idDoesNotExistInChildTable(resource.getClazz(), id, persistence);
        var stack = createResourceStack(resource, persistence);

        while (!stack.isEmpty()) {
            deleteFromResource(stack.pop(), id, persistence);
        }
    }

    public static void delete(Resource resource, Long id) {
        idDoesNotExistInChildTable(resource.getClazz(), id);
        var stack = createResourceStack(resource);

        while (!stack.isEmpty()) {
            deleteFromResource(stack.pop(), id);
        }
    }

    private static void deleteFromResource(Resource resource, Long id, Persistence persistence) {
        var entry = resource.getObjectInstances().getByKey(id);

        if (isNull(entry)) {
            return;
        }

        removeIdFromResourceDimensions(resource, id, entry.getValue());
        removeIdFromRelatedResourceDimensions(resource, id, persistence);
        resource.getObjectInstances().remove(id);
    }

    private static void deleteFromResource(Resource resource, Long id) {
        var entry = resource.getObjectInstances().getByKey(id);

        if (isNull(entry)) {
            return;
        }

        removeIdFromResourceDimensions(resource, id, entry.getValue());
        removeIdFromRelatedResourceDimensions(resource, id);
        resource.getObjectInstances().remove(id);
    }
}