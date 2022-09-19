package dsenta.cachito.repository.resource;


import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.cache.resource.ResourceCache;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.persistence.NoPersistence;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NonPersistableResource {

    private static final ResourceCache resourceCache = new ResourceCache();
    private static final Persistence persistence = new NoPersistence();

    public static Resource create(Clazz clazz, boolean ifNotExists, Consumer<Clazz> assertions) {
        ClazzCache.create(clazz);
        return resourceCache.create(clazz, ifNotExists, persistence, assertions);
    }

    public static Resource create(Clazz clazz, Consumer<Clazz> assertions) {
        ClazzCache.create(clazz);
        return resourceCache.create(clazz, assertions);
    }

    public static void drop(Clazz clazz) {
        ClazzCache.delete(clazz);
        resourceCache.drop(clazz);
    }

    public static Resource get(Clazz clazz) {
        return resourceCache.get(clazz);
    }
}