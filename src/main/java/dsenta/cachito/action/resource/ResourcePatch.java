package dsenta.cachito.action.resource;

import dsenta.cachito.handler.resource.patch.ResourcePatchHandler;
import dsenta.cachito.handler.resource.patch.ResourcePatchHandler_Simple;

public final class ResourcePatch {

    private final ResourcePatchHandler handler = new ResourcePatchHandler();
    private final ResourcePatchHandler_Simple simple = new ResourcePatchHandler_Simple();

    public ResourcePatchHandler stream() {
        return handler;
    }

    public ResourcePatchHandler_Simple simpleStream() {
        return simple;
    }
}