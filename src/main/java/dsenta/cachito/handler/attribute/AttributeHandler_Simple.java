package dsenta.cachito.handler.attribute;

import dsenta.cachito.handler.dimension.DimensionHandler_Simple;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.resource.Resource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeHandler_Simple {

    public static void removeIdFromResourceDimensions(Resource resource,
                                                      Long id,
                                                      ObjectInstance objectInstance) {
        DimensionHandler_Simple.removeIdFromDimensions(
                resource.getDimensions(),
                id,
                objectInstance,
                resource.streamDimensionalAttributes().collect(Collectors.toList())
        );
    }

    public static void removeIdFromResourceDimensions(Resource resource,
                                                      Long id,
                                                      ObjectInstance objectInstance,
                                                      Set<String> dimensionNames) {
        var attributes = resource.streamDimensionalAttributes()
                .filter(attribute -> dimensionNames.contains(attribute.getName()))
                .collect(Collectors.toList());

        DimensionHandler_Simple.removeIdFromDimensions(
                resource.getDimensions(),
                id,
                objectInstance,
                attributes
        );
    }
}