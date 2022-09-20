package dsenta.cachito.assertions.resource;

import dsenta.cachito.exception.resource.UniqueConstraintException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.filter.AndWhere;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

import static dsenta.cachito.handler.resource.get.ResourceGetHandler.get;
import static dsenta.cachito.mapper.attribute.AndWhereMapper.toAndWhereEqualsForConstraint;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ResourceAssert {

    public static void assertUniqueConstraint(Resource resource,
                                              ObjectInstance objectInstance,
                                              Long id,
                                              Persistence persistence) {
        Clazz clazz = resource.getClazz();

        var andWhereEqualsList = clazz.getAttributeCollection().stream()
                .filter(Attribute::isUnique)
                .map(attribute -> toAndWhereEqualsForConstraint(attribute, objectInstance))
                .collect(Collectors.toList());

        if (andWhereEqualsList.isEmpty()) {
            return;
        }

        var filter = new Filter(andWhereEqualsList);

        get(resource, filter, persistence)
                .stream()
                .map(Entity::getId)
                .filter(o -> !o.equals(id))
                .findFirst()
                .ifPresent(o -> {
                    throw new UniqueConstraintException(
                            clazz.getName(),
                            andWhereEqualsList.stream().map(AndWhere::getAttribute).collect(Collectors.toSet())
                    );
                });
    }
}