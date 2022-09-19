package dsenta.cachito.mapper.entity;

import dsenta.cachito.factory.entity.EntityFactory;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EntryToEntityMapper_Simple {

    public static Optional<Entity> toEntity(Entry<Long, ObjectInstance> entry, Resource resource) {
        if (isNull(entry)) {
            return Optional.empty();
        }

        var entity = EntityFactory.create(entry.getKey(), entry.getValue(), resource.getClazz());
        return Optional.of(entity);
    }

    public static List<Entity> toEntityList(List<Entry<Long, ObjectInstance>> entries, Resource resource) {
        return entries.stream()
                .map(entry -> toEntity(entry, resource))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}