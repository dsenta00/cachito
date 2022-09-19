package dsenta.cachito.action.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceAction {

    private static final ResourceAlter alter = new ResourceAlter();
    private static final ResourceDelete delete = new ResourceDelete();
    private static final ResourceDrop drop = new ResourceDrop();
    private static final ResourceGet get = new ResourceGet();
    private static final ResourcePatch patch = new ResourcePatch();
    private static final ResourcePost post = new ResourcePost();
    private static final ResourcePut put = new ResourcePut();

    public static ResourceAlter alter() {
        return alter;
    }

    public static ResourceDelete delete() {
        return delete;
    }

    public static ResourceDrop drop() {
        return drop;
    }

    public static ResourceGet get() {
        return get;
    }

    public static ResourcePatch patch() {
        return patch;
    }

    public static ResourcePost post() {
        return post;
    }

    public static ResourcePut put() {
        return put;
    }
}