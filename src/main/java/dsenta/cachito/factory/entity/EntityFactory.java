package dsenta.cachito.factory.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.objectinstance.ObjectInstance;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityFactory {

    public static Entity create(Long id, ObjectInstance objectInstance, Clazz clazz, Entity parentEntity) {
        Entity entity = new Entity();

        entity.setId(id);
        entity.setObjectInstance(objectInstance);
        entity.setClazz(clazz);
        entity.setParentEntity(parentEntity);

        return entity;
    }

    public static Entity create(Long id, ObjectInstance objectInstance, Clazz clazz) {
        Entity entity = new Entity();

        entity.setId(id);
        entity.setObjectInstance(objectInstance);
        entity.setClazz(clazz);

        return entity;
    }
}