package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.post.ResourcePostHandler;
import dsenta.cachito.handler.resource.post.ResourcePostHandler_Simple;

public final class ResourcePost {
    
    private final ResourcePostHandler handler = new ResourcePostHandler();
    private final ResourcePostHandler_Simple simple = new ResourcePostHandler_Simple();

    public ResourcePostHandler stream() {
        return handler;
    }

    public ResourcePostHandler_Simple simpleStream() {
        return simple;
    }
}