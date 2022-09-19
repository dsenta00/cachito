package dsenta.cachito.assertions.clazz;

import dsenta.cachito.assertions.attribute.AttributeAssert_Simple;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import lombok.NoArgsConstructor;

import static dsenta.cachito.assertions.attribute.AttributeAssert_Simple.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ClazzAssert_Simple {

    public static void canCreate(Clazz inputClazz) {
        inputClazz.getAttributeCollection().forEach(AttributeAssert_Simple::canAddAttribute);
    }

    public static void canAlter(Clazz clazz, ClazzAlter alter) {
        var fieldsToUpdate = alter.getFieldsToUpdate();
        var fieldsToDelete = alter.getFieldsToDelete();
        var fieldsToAdd = alter.getFieldsToAdd();

        canUpdateAttributes(clazz, fieldsToUpdate);
        canDeleteAttributes(clazz, fieldsToDelete);
        canAddAttributes(clazz, fieldsToAdd);
    }
}