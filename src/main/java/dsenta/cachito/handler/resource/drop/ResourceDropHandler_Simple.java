package dsenta.cachito.handler.resource.drop;

import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler_Simple;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map.Entry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceDropHandler_Simple {
    public static void drop(Clazz clazz, Persistence persistence) {
        Resource resource = PersistableResource.get(clazz, persistence);

        resource.getObjectInstances()
                .getAsc()
                .stream()
                .map(Entry::getKey)
                .forEach(id -> ResourceDeleteHandler_Simple.delete(resource, id));

        PersistableResource.drop(clazz, persistence);
        ClazzCache.delete(clazz);
    }

    public static void dropForce(Clazz clazz, Persistence persistence) {
        PersistableResource.dropForce(clazz, persistence);
        ClazzCache.delete(clazz);
    }
}