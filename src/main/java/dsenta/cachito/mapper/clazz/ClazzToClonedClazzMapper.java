package dsenta.cachito.mapper.clazz;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.fields.FieldsToDisplay;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClazzToClonedClazzMapper {

    public static Clazz clonePartially(Clazz clazz, FieldsToDisplay fieldsToDisplay) {
        var clonedClazz = new Clazz();
        clonedClazz.setResourceInfo(clazz.getResourceInfo());

        if (nonNull(clonedClazz.getParentClazz())) {
            clonedClazz.setParentClazz(clonedClazz.getParentClazz().clonePartially(fieldsToDisplay));
        }

        var attributes = fieldsToDisplay.getFieldNames().stream()
                .map(clazz::getAttribute)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(attribute -> {
                    var fieldsOfAttributeToDisplay = fieldsToDisplay.getFieldsOfAttributeToDisplay();
                    return attribute.clonePartially(fieldsOfAttributeToDisplay.get(attribute.getName()));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        clonedClazz.setAttributeList(attributes);
        clonedClazz.setSchema(clazz.getSchema());

        return clonedClazz;
    }
}
