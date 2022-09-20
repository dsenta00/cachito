package dsenta.cachito.cache.resource;

import dsenta.cachito.factory.resource.ResourceFactory;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.function.Function2;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.queryablemap.trie.Trie;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@NoArgsConstructor
public class ResourceCache {

    protected final Map<Clazz, Resource> resourceMap = new Trie<>();

    public Resource create(Clazz clazz,
                           boolean ifNotExists,
                           Persistence persistence,
                           Consumer<Clazz> assertions) {
        return create(
                clazz,
                ifNotExists,
                persistence,
                (clazz1, persistence1) -> assertions.accept(clazz1)
        );
    }

    public Resource create(Clazz clazz,
                           boolean ifNotExists,
                           Persistence persistence,
                           Function2<Clazz, Persistence> assertions) {
        if (ifNotExists) {
            var resource = get(clazz);

            if (nonNull(resource)) {
                return resource;
            }
        }

        return create(clazz, persistence, assertions);
    }

    public Resource create(Clazz clazz,
                           Consumer<Clazz> assertions) {
        assertions.accept(clazz);

        var resource = ResourceFactory.create(clazz);
        resourceMap.put(clazz, resource);

        return resource;
    }

    public Resource create(Clazz clazz,
                           Persistence persistence,
                           Function2<Clazz, Persistence> assertions) {
        assertions.apply(clazz, persistence);

        var resource = ResourceFactory.create(clazz);
        resourceMap.put(clazz, resource);

        return resource;
    }

    public void delete(Clazz clazz) {
        resourceMap.remove(clazz);
    }

    public void drop(Clazz clazz) {
        var schema = clazz.getSchema();
        if (nonNull(schema)) {
            schema.getClazzMap().remove(clazz.getName());
        }

        resourceMap.remove(clazz);
    }

    public Resource get(Clazz clazz) {
        return resourceMap.getOrDefault(clazz, null);
    }

    public void put(Clazz clazz, Resource resource) {
        resourceMap.put(clazz, resource);
    }

    public Stream<Resource> streamResources() {
        return resourceMap.values().stream();
    }
}