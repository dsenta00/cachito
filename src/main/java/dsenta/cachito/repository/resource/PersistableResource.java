package dsenta.cachito.repository.resource;

import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.cache.resource.ResourceCache;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.function.Function2;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class PersistableResource {

    private static final ResourceCache resourceCache = new ResourceCache();

    public static Resource create(Clazz clazz,
                                  boolean ifNotExists,
                                  Function2<Clazz, Persistence> assertions,
                                  Persistence persistence) {
        ClazzCache.create(clazz);
        var resource = resourceCache.create(clazz, ifNotExists, persistence, assertions);
        if (!clazz.isCache()) {
            persistence.save(resource);
        }
        return resource;
    }

    public static Resource create(Clazz clazz,
                                  Function2<Clazz, Persistence> assertions,
                                  Persistence persistence) {
        ClazzCache.create(clazz);
        var resource = resourceCache.create(clazz, persistence, assertions);
        if (!clazz.isCache()) {
            persistence.save(resource);
        }
        return resource;
    }

    public static void dropForce(Clazz clazz, Persistence persistence) {
        ClazzCache.delete(clazz);
        resourceCache.delete(clazz);
        clazz.getSchema().getClazzMap().remove(clazz.getName());
        if (!clazz.isCache()) {
            persistence.delete(clazz.getResourceInfo());
        }
    }

    public static void drop(Clazz clazz, Persistence persistence) {
        ClazzCache.delete(clazz);
        resourceCache.drop(clazz);
        if (!clazz.isCache()) {
            persistence.delete(clazz.getResourceInfo());
        }
    }

    public static void commit(Persistence persistence) {
        resourceCache.streamResources()
                .filter(resource -> !resource.getClazz().isCache())
                .forEach(persistence::save);
    }

    public static Resource get(Clazz clazz, Persistence persistence) {
        var resource = resourceCache.get(clazz);

        if (nonNull(resource) || clazz.isSimple()) {
            return resource;
        }

        readFromAllRelatives(clazz, persistence);

        return resourceCache.get(clazz);
    }

    public static Stream<Resource> stream() {
        return resourceCache.streamResources();
    }

    private static void readFromAllRelatives(Clazz inputClazz, Persistence persistence) {
        for (var clazz = inputClazz; nonNull(clazz); clazz = clazz.getParentClazz()) {
            var resource = resourceCache.get(clazz);

            if (isNull(resource)) {
                resource = persistence.read(clazz.getResourceInfo());
                resourceCache.put(clazz, resource);
            }
        }

        ClazzCache.stream().getChildClazzes(inputClazz).forEach(o -> PersistableResource.get(o, persistence));
        ClazzCache.stream().getRelatedClazzes(inputClazz).forEach(o -> PersistableResource.get(o, persistence));
    }
}