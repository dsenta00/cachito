package dsenta.cachito.exception.resource;

import dsenta.cachito.model.clazz.Clazz;

import java.util.List;
import java.util.stream.Collectors;

public class CannotDropResource_RelationshipFromOtherResourceException extends IllegalArgumentException {

    public CannotDropResource_RelationshipFromOtherResourceException(Clazz clazz, List<Clazz> clazzes) {
        super(String.format("Resources %s has relationship to resource %s. Therefore, you cannot drop resource %s.",
                clazzes.stream().map(Clazz::getName).collect(Collectors.toList()),
                clazz.getName(),
                clazz.getName())
        );
    }
}