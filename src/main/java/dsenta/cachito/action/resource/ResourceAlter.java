package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.alter.ResourceAlterHandler;
import dsenta.cachito.handler.resource.alter.ResourceAlterHandler_Simple;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import dsenta.cachito.model.persistence.Persistence;

public final class ResourceAlter {

    public void alter(Clazz clazz, ClazzAlter clazzAlter, Persistence persistence) {
        if (clazz.isSimple()) {
            ResourceAlterHandler_Simple.alter(clazz, clazzAlter, persistence);
        } else {
            ResourceAlterHandler.alter(clazz, clazzAlter, persistence);
        }
    }
}