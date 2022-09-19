package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.drop.ResourceDropHandler;
import dsenta.cachito.handler.resource.drop.ResourceDropHandler_Simple;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.persistence.Persistence;

public final class ResourceDrop {

    public void drop(Clazz clazz, Persistence persistence) {
        if (clazz.isSimple()) {
            ResourceDropHandler_Simple.drop(clazz, persistence);
        } else {
            ResourceDropHandler.drop(clazz, persistence);
        }
    }

    public void dropForce(Clazz clazz, Persistence persistence) {
        if (clazz.isSimple()) {
            ResourceDropHandler_Simple.dropForce(clazz, persistence);
        } else {
            ResourceDropHandler.dropForce(clazz, persistence);
        }
    }
}