package dsenta.cachito.assertions.attribute;

import dsenta.cachito.exception.AddingAttributeWithSameNameException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AttributeDuplicationAssert {

    public static void assertDuplications(Clazz clazz, List<Attribute> attributesToAdd) {
        List<Attribute> attributes = new LinkedList<>(clazz.getAttributeCollection());
        attributes.addAll(attributesToAdd);

        attributes.stream()
                .filter(attribute -> Collections.frequency(attributes, attribute) > 1)
                .map(Attribute::getName)
                .findFirst()
                .ifPresent(attributeName -> {
                    throw new AddingAttributeWithSameNameException(attributeName);
                });
    }
}