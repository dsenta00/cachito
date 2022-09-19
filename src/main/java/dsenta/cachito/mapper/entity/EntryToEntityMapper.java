package dsenta.cachito.mapper.entity;

import dsenta.cachito.action.resource.ResourceAction;
import dsenta.cachito.exception.DimensionDoesNotExistException;
import dsenta.cachito.exception.ResourceNotFoundException;
import dsenta.cachito.factory.entity.EntityFactory;
import dsenta.cachito.mapper.clazz.ClazzToClonedClazzMapper;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@NoArgsConstructor
public final class EntryToEntityMapper {

    public static List<Entity> toEntityList(List<Entry<Long, ObjectInstance>> entries,
                                            Resource resource,
                                            Persistence persistence,
                                            FieldsToDisplay fieldsToDisplay) {
        return entries.stream()
                .map(entry -> toEntity(entry, resource, persistence))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .collect(Collectors.toList());
    }

    public static Optional<Entity> toEntity(Entry<Long, ObjectInstance> entry,
                                            Resource resource,
                                            Persistence persistence) {
        var inserted = new HashSet<Entry<Long, ObjectInstance>>();
        return toEntity(entry, resource, persistence, inserted);
    }

    private static Optional<Entity> toEntity(Entry<Long, ObjectInstance> entry,
                                             Resource resource,
                                             Persistence persistence,
                                             Set<Entry<Long, ObjectInstance>> inserted) {
        if (isNull(entry)) {
            return Optional.empty();
        }

        if (inserted.contains(entry)) {
            return Optional.of(EntityFactory.create(
                    entry.getKey(),
                    entry.getValue(),
                    ClazzToClonedClazzMapper.clonePartially(resource.getClazz(), FieldsToDisplay.idOnly())
            ));
        }

        inserted.add(entry);

        var parentClazz = resource.getClazz().getParentClazz();
        if (isNull(parentClazz)) {
            var entity = EntityFactory.create(entry.getKey(), entry.getValue(), resource.getClazz());
            fetchWithRelatedEntities(entity, resource, persistence, inserted);
            return Optional.of(entity);
        }

        var parentEntity = ResourceAction.get().stream()
                .getById(PersistableResource.get(parentClazz, persistence), entry.getKey(), persistence)
                .orElseThrow(() -> new ResourceNotFoundException(parentClazz.getName(), entry.getKey()));

        var entity = EntityFactory.create(entry.getKey(), entry.getValue(), resource.getClazz(), parentEntity);

        fetchWithRelatedEntities(entity, resource, persistence, inserted);

        return Optional.of(entity);
    }

    private static void fetchWithRelatedEntities(Entity entity,
                                                 Resource resource,
                                                 Persistence persistence,
                                                 Set<Entry<Long, ObjectInstance>> inserted) {
        var relatedAttributes = resource.getClazz()
                .getAttributeCollection()
                .stream()
                .filter(clazzField -> DataType.isRelationship(clazzField.getDataType()))
                .collect(Collectors.toList());

        for (var relatedAttribute : relatedAttributes) {
            var relatedIds = resource.getDimension(relatedAttribute.getName())
                    .orElseThrow(() -> new DimensionDoesNotExistException(relatedAttribute))
                    .getEquals(entity.getId());

            var relatedEntitiesByFieldIndex = entity.getRelatedEntitiesByFieldIndex();
            if (isNull(relatedIds)) {
                relatedEntitiesByFieldIndex.put(relatedAttribute.getPropertyIndex(), List.of());
                continue;
            }

            var relatedResource = PersistableResource.get(relatedAttribute.getClazz(), persistence);
            var objectInstances = relatedResource.getObjectInstances();
            var relatedEntities = relatedIds.stream()
                    .map(objectInstances::getByKey)
                    .map(wgbData -> toEntity(wgbData, relatedResource, persistence, inserted))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            relatedEntitiesByFieldIndex.put(relatedAttribute.getPropertyIndex(), relatedEntities);
        }
    }
}