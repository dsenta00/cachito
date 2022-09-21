package dsenta.cachito.assertions.clazz;

import dsenta.cachito.Cachito;
import dsenta.cachito.assertions.attribute.AttributeAssert;
import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.exception.attribute.CannotOverrideDataTypeException;
import dsenta.cachito.exception.resource.IdAlreadyExistsInChildResourceException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import dsenta.cachito.model.persistence.Persistence;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

import static dsenta.cachito.assertions.attribute.AttributeAssert.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ClazzAssert {

    public static void idDoesNotExistInChildTable(Clazz clazz, Long id) {
        ClazzCache.stream().getChildClazzes(clazz).forEach(childClazz ->
                Cachito.clazz(childClazz)
                        .getById(id)
                        .ifPresent(entity -> {
                            throw new IdAlreadyExistsInChildResourceException(id, entity.getClazz().getName());
                        })
        );
    }

    public static void idDoesNotExistInChildTable(Clazz clazz, Long id, Persistence persistence) {
        ClazzCache.stream().getChildClazzes(clazz).forEach(childClazz ->
                Cachito.clazz(childClazz)
                        .persistable(persistence)
                        .getById(id)
                        .ifPresent(entity -> {
                            throw new IdAlreadyExistsInChildResourceException(id, entity.getClazz().getName());
                        })
        );
    }

    public static void canCreate(Clazz clazz) {
        checkAttributeWithSameNameInParentResources(clazz);

        for (Attribute attributeToAdd : clazz.getAttributeCollection()) {
            AttributeAssert.canAddAttribute(clazz, attributeToAdd);
        }
    }

    public static void canCreate(Clazz clazz, Persistence persistence) {
        checkAttributeWithSameNameInParentResources(clazz);

        for (Attribute attributeToAdd : clazz.getAttributeCollection()) {
            AttributeAssert.canAddAttribute(clazz, attributeToAdd, persistence);
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

    private static void checkAttributeWithSameNameInParentResources(Clazz inputClazz) {
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
    }
}