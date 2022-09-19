package dsenta.cachito.model.persistence;

import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.model.resource.info.ResourceInfo;

public class NoPersistence implements Persistence {

    @Override
    public Resource read(ResourceInfo resourceInfo) {
        return null;
    }

    @Override
    public void delete(ResourceInfo resourceInfo) {
    }

    @Override
    public void save(Resource resource) {
    }
}