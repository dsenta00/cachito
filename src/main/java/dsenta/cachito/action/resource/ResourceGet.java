package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.handler.resource.get.ResourceGetHandler_Simple;

public class ResourceGet {

    private final ResourceGetHandler handler = new ResourceGetHandler();
    private final ResourceGetHandler_Simple simple = new ResourceGetHandler_Simple();

    public ResourceGetHandler stream() {
        return handler;
    }

    public ResourceGetHandler_Simple simpleStream() {
        return simple;
    }
}