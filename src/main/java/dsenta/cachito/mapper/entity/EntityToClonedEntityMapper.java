package dsenta.cachito.mapper.entity;

import dsenta.cachito.mapper.clazz.ClazzToClonedClazzMapper;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityToClonedEntityMapper {

    public static Entity clonePartially(Entity entity, FieldsToDisplay fieldsToDisplay) {
        var fieldNames = fieldsToDisplay.getFieldNames();
        if (isNull(fieldNames) || fieldNames.isEmpty()) {
            return entity;
        }

        var clonedEntity = new Entity();

        if (fieldNames.contains("id")) {
            clonedEntity.setId(entity.getId());
        }

        if (nonNull(entity.getParentEntity())) {
            clonedEntity.setParentEntity(entity.getParentEntity().clonePartially(fieldsToDisplay));
        }

        clonedEntity.setClazz(ClazzToClonedClazzMapper.clonePartially(entity.getClazz(), fieldsToDisplay));
        clonedEntity.setRelatedEntitiesByFieldIndex(cloneRelatedEntitiesByFieldIndexPartially(entity, fieldsToDisplay));
        clonedEntity.setObjectInstance(entity.getObjectInstance());

        return clonedEntity;
    }

    private static Map<Integer, List<Entity>> cloneRelatedEntitiesByFieldIndexPartially(Entity entity, FieldsToDisplay fieldsToDisplay) {
        return entity.getClazz().getAttributeCollection().stream()
                .filter(attribute -> fieldsToDisplay.getFieldNames().contains(attribute.getName()))
                .filter(attribute -> DataType.isRelationship(attribute.getDataType()))
                .map(attribute -> Map.entry(attribute.getPropertyIndex(), clonePartiallyRelatedEntity(entity, attribute, fieldsToDisplay)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static List<Entity> clonePartiallyRelatedEntity(Entity entity, Attribute attribute, FieldsToDisplay fieldsToDisplay) {
        var fieldsOfAttributeToDisplay = fieldsToDisplay.getFieldsOfAttributeToDisplay();
        if (!fieldsOfAttributeToDisplay.containsKey(attribute.getName())) {
            return entity.getRelatedEntities(attribute.getPropertyIndex());
        }

        var relatedFieldsToDisplay = fieldsOfAttributeToDisplay.get(attribute.getName());

        return entity.getRelatedEntities(attribute.getPropertyIndex()).stream()
                .map(relatedEntity -> clonePartially(relatedEntity, relatedFieldsToDisplay))
                .collect(Collectors.toList());
    }
}