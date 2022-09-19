package dsenta.cachito.model.entity;

import dsenta.cachito.exception.AttributeDoesNotExistException;
import dsenta.cachito.mapper.entity.EntityToClonedEntityMapper;
import dsenta.cachito.mapper.entity.EntityToStringMapper;
import dsenta.cachito.mapper.entity.EntityToStringMapper_Simple;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import lombok.Data;

import java.util.*;

@Data
public class Entity implements Comparable<Entity> {

    private Long id;
    private ObjectInstance objectInstance;
    private Clazz clazz;
    private Entity parentEntity;
    private Map<Integer, List<Entity>> relatedEntitiesByFieldIndex = new HashMap<>();

    public List<Entity> getRelatedEntities(String fieldName) {
        Integer propertyIndex = clazz.getAttribute(fieldName)
                .orElseThrow(() -> new AttributeDoesNotExistException(fieldName))
                .getPropertyIndex();

        return getRelatedEntities(propertyIndex);
    }

    public List<Entity> getRelatedEntities(Integer propertyIndex) {
        return relatedEntitiesByFieldIndex.getOrDefault(propertyIndex, List.of());
    }

    public Object getAttributeValue(String fieldName) {
        Optional<Attribute> optionalAttribute = clazz.getAttribute(fieldName);

        if (optionalAttribute.isEmpty()) {
            if (Objects.isNull(parentEntity)) {
                throw new AttributeDoesNotExistException(fieldName);
            }

            return parentEntity.getAttributeValue(fieldName);
        }

        Attribute attribute = optionalAttribute.get();

        switch (attribute.getDataType()) {
            case RELATIONSHIP_ZERO_ONE:
                List<Entity> entities = getRelatedEntities(attribute.getPropertyIndex());
                return entities.isEmpty() ? null : entities.get(0);
            case RELATIONSHIP_ONE:
                return getRelatedEntities(attribute.getPropertyIndex()).get(0);
            case RELATIONSHIP_ZERO_MANY:
            case RELATIONSHIP_ONE_MANY:
                return getRelatedEntities(attribute.getPropertyIndex());
            case BOOLEAN:
            case INTEGER:
            case FLOAT:
            case STRING:
            case DATE:
            default:
                return objectInstance.get(attribute);
        }
    }

    @Override
    public int compareTo(Entity o) {
        int cmp = this.getId().compareTo(o.getId());
        return cmp == 0 ? this.getClazz().compareTo(o.getClazz()) : cmp;
    }

    @Override
    public String toString() {
        if (clazz.isSimple()) {
            return EntityToStringMapper_Simple.toString(this);
        } else {
            return EntityToStringMapper.toString(this);
        }
    }


    public Entity clonePartially(FieldsToDisplay fieldsToDisplay) {
        return EntityToClonedEntityMapper.clonePartially(this, fieldsToDisplay);
    }
}