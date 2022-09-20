package dsenta.cachito.exception;

import dsenta.cachito.model.resource.Resource;

public class LeftJoinOnSimpleResourceException extends RuntimeException {

    public LeftJoinOnSimpleResourceException(Resource resource) {
        super(
                "Resource " + resource.getClazz().getName() + " is simple and cannot have relationship." +
                        " Therefore you cannot left join another resource"
        );
    }
}