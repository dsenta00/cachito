package dsenta.cachito.handler.resource.alter;

import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.NoArgsConstructor;

import static dsenta.cachito.assertions.clazz.ClazzAssert_Simple.canAlter;
import static dsenta.cachito.handler.attribute.AttributeHandler.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceAlterHandler_Simple {

    public static void alter(Clazz clazz, ClazzAlter clazzAlter, Persistence persistence) {
        canAlter(clazz, clazzAlter);

        var resource = PersistableResource.get(clazz, persistence);

        deleteAttributes(resource, clazzAlter.getFieldsToDelete());
        updateAttributes(resource, clazzAlter.getFieldsToUpdate());
        addAttributes(resource, clazzAlter.getFieldsToAdd());
    }
}