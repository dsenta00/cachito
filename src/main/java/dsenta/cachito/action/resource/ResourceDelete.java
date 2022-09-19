package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler;
import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler_Simple;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;

public class ResourceDelete {

    public void delete(Resource resource, Long id, Persistence persistence) {
        if (resource.getClazz().isSimple()) {
            ResourceDeleteHandler_Simple.delete(resource, id);
        } else {
            ResourceDeleteHandler.delete(resource, id, persistence);
        }
    }
}