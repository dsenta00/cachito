package dsenta.cachito.resource.testmodel;

import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.model.resource.info.ResourceInfo;
import dsenta.queryablemap.trie.Trie;

import java.util.Map;

import static java.util.Collections.synchronizedMap;

public class PersistenceImplTest implements Persistence {

    private final Map<ResourceInfo, Resource> disk = synchronizedMap(new Trie<ResourceInfo, Resource>());

    @Override
    public Resource read(ResourceInfo resourceInfo) {
        return disk.get(resourceInfo);
    }

    @Override
    public void delete(ResourceInfo resourceInfo) {
        disk.remove(resourceInfo);
    }

    @Override
    public void save(Resource resource) {
        disk.put(resource.getClazz().getResourceInfo(), resource);
    }
}
