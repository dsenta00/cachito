package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.put.ResourcePutHandler;
import dsenta.cachito.handler.resource.put.ResourcePutHandler_Simple;

public final class ResourcePut {
    
    private final ResourcePutHandler handler = new ResourcePutHandler();
    private final ResourcePutHandler_Simple simple = new ResourcePutHandler_Simple();

    public ResourcePutHandler stream() {
        return handler;
    }

    public ResourcePutHandler_Simple simpleStream() {
        return simple;
    }
}