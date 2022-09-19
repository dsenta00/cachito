package dsenta.cachito.factory.resource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.factory.dimension.DimensionFactory;
import dsenta.cachito.model.resource.Resource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceFactory {

    public static Resource create(Clazz clazz) {
        Resource resource = new Resource(clazz);
        resource.setDimensions(DimensionFactory.createDimensions(clazz));
        return resource;
    }
}