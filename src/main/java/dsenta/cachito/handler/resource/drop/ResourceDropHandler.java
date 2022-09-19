package dsenta.cachito.handler.resource.drop;

import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.exception.CannotDropParentResourceException;
import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.NoArgsConstructor;

import java.util.Map.Entry;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceDropHandler {

    public static void drop(Clazz clazz, Persistence persistence) {
        var resource = PersistableResource.get(clazz, persistence);

        if (ClazzCache.stream().hasChildClazzes(clazz)) {
            throw new CannotDropParentResourceException(clazz.getName());
        }

        resource.getObjectInstances()
                .getAsc()
                .stream()
                .map(Entry::getKey)
                .forEach(id -> ResourceDeleteHandler.delete(resource, id, persistence));

        PersistableResource.drop(clazz, persistence);
        ClazzCache.stream().delete(clazz);
    }

    public static void dropForce(Clazz clazz, Persistence persistence) {
        PersistableResource.dropForce(clazz, persistence);
        ClazzCache.stream().delete(clazz.getResourceInfo());
    }
}