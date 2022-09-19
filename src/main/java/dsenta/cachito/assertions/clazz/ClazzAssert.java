package dsenta.cachito.assertions.clazz;

import dsenta.cachito.assertions.attribute.AttributeAssert;
import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.exception.CannotOverrideDataTypeException;
import dsenta.cachito.exception.IdAlreadyExistsInChildResourceException;
import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

import static dsenta.cachito.assertions.attribute.AttributeAssert.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ClazzAssert {

    public static void idDoesNotExistInChildTable(Clazz clazz, Long id, Persistence persistence) {
        ClazzCache.stream().getChildClazzes(clazz).stream()
                .map(childClazz -> PersistableResource.get(childClazz, persistence))
                .forEach(childResource -> {
                    if (ResourceGetHandler.getById(childResource, id, persistence).isPresent()) {
                        throw new IdAlreadyExistsInChildResourceException(id, childResource.getClazz().getName());
                    }
                });
    }

    public static void canCreate(Clazz inputClazz, Persistence persistence) {
        for (Clazz clazz = inputClazz.getParentClazz(); Objects.nonNull(clazz); clazz = clazz.getParentClazz()) {
            clazz.getAttributeCollection().forEach(parentAttribute -> {
                Optional<Attribute> attributeOptional = inputClazz.getAttribute(parentAttribute.getName());
                if (attributeOptional.isPresent()) {
                    Attribute attribute = attributeOptional.get();
                    if (!attribute.getDataType().equals(parentAttribute.getDataType())) {
                        throw new CannotOverrideDataTypeException(parentAttribute, attribute.getDataType());
                    }
                }
            });
        }

        for (Attribute attributeToAdd : inputClazz.getAttributeCollection()) {
            AttributeAssert.canAddAttribute(inputClazz, attributeToAdd, persistence);
        }
    }

    public static void canAlter(Clazz clazz, ClazzAlter alter, Persistence persistence) {
        var fieldsToUpdate = alter.getFieldsToUpdate();
        var fieldsToDelete = alter.getFieldsToDelete();
        var fieldsToAdd = alter.getFieldsToAdd();

        canAlterChildClazzes(clazz, fieldsToUpdate, fieldsToDelete, fieldsToAdd);
        canAlterParentClazz(clazz.getParentClazz(), fieldsToAdd, fieldsToUpdate);
        canUpdateAttributes(clazz, fieldsToUpdate, persistence);
        canDeleteAttributes(clazz, fieldsToDelete);
        canAddAttributes(clazz, fieldsToAdd, persistence);
    }
}