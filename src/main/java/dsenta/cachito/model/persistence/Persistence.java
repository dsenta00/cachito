package dsenta.cachito.model.persistence;

import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.model.resource.info.ResourceInfo;

public interface Persistence {
    public Resource read(ResourceInfo resourceInfo);
    public void delete(ResourceInfo resourceInfo);
    public void save(Resource resource);
}