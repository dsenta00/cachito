package dsenta.cachito.handler.resource.delete;

import dsenta.cachito.handler.attribute.AttributeHandler_Simple;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceDeleteHandler_Simple {

    public static void delete(Resource resource, Long id) {
        var entry = resource.getObjectInstances().getByKey(id);

        if (isNull(entry)) {
            return;
        }

        AttributeHandler_Simple.removeIdFromResourceDimensions(resource, id, entry.getValue());
        resource.getObjectInstances().remove(id);
    }
}