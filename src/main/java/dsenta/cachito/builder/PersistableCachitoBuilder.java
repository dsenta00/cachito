package dsenta.cachito.builder;

import dsenta.cachito.assertions.clazz.ClazzAssert;
import dsenta.cachito.assertions.clazz.ClazzAssert_Simple;
import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.exception.LeftJoinOnSimpleResourceException;
import dsenta.cachito.handler.resource.alter.ResourceAlterHandler;
import dsenta.cachito.handler.resource.alter.ResourceAlterHandler_Simple;
import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler;
import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler_Simple;
import dsenta.cachito.handler.resource.drop.ResourceDropHandler;
import dsenta.cachito.handler.resource.drop.ResourceDropHandler_Simple;
import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.handler.resource.get.ResourceGetHandler_Simple;
import dsenta.cachito.handler.resource.patch.ResourcePatchHandler;
import dsenta.cachito.handler.resource.patch.ResourcePatchHandler_Simple;
import dsenta.cachito.handler.resource.post.ResourcePostHandler;
import dsenta.cachito.handler.resource.post.ResourcePostHandler_Simple;
import dsenta.cachito.handler.resource.put.ResourcePutHandler;
import dsenta.cachito.handler.resource.put.ResourcePutHandler_Simple;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.group.Group;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.leftjoin.LeftJoinWithClazz;
import dsenta.cachito.model.pagination.Pagination;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.NonPersistableResource;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static dsenta.cachito.model.fields.FieldsToDisplay.all;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersistableCachitoBuilder {

    private Persistence persistence;
    private Clazz clazz;
    private Resource resource;

    public PersistableCachitoBuilder clazz(Clazz clazz) {
        setClazz(clazz);
        return this;
    }

    public PersistableCachitoBuilder persistable(Persistence persistence) {
        setPersistence(persistence);
        return this;
    }

    public PersistableCachitoBuilder meanwhile(Consumer<Resource> consumer) {
        fetchResource();
        consumer.accept(resource);
        return this;
    }

    public PersistableCachitoBuilder create(Clazz clazz, boolean ifNotExist) {
        setClazz(clazz);

        if (clazz.isCache()) {
            if (clazz.isSimple()) {
                resource = NonPersistableResource.create(clazz, ifNotExist, ClazzAssert_Simple::canCreate);
                ClazzCache.simpleStream().put(clazz.getResourceInfo(), clazz);
            } else {
                resource = NonPersistableResource.create(clazz, ifNotExist, ClazzAssert::canCreate);
                ClazzCache.stream().put(clazz.getResourceInfo(), clazz);
            }
        } else {
            resource = PersistableResource.create(clazz, ifNotExist, ClazzAssert::canCreate, persistence);
        }

        if (clazz.isSimple()) {
            ClazzCache.simpleStream().put(clazz.getResourceInfo(), clazz);

            if (clazz.isCache()) {
                resource = NonPersistableResource.create(clazz, ifNotExist, ClazzAssert_Simple::canCreate);
            } else {
                resource = PersistableResource.create(clazz, ifNotExist, ClazzAssert::canCreate, persistence);
            }
        } else {
            ClazzCache.stream().put(clazz.getResourceInfo(), clazz);

            if (clazz.isCache()) {
                resource = NonPersistableResource.create(clazz, ifNotExist, ClazzAssert::canCreate);
            } else {
                resource = PersistableResource.create(clazz, ifNotExist, ClazzAssert::canCreate, persistence);
            }
        }

        return this;
    }

    public PersistableCachitoBuilder create(Clazz clazz) {
        setClazz(clazz);

        if (clazz.isCache()) {
            if (clazz.isSimple()) {
                resource = NonPersistableResource.create(clazz, ClazzAssert_Simple::canCreate);
                ClazzCache.simpleStream().put(clazz.getResourceInfo(), clazz);
            } else {
                resource = NonPersistableResource.create(clazz, ClazzAssert::canCreate);
                ClazzCache.stream().put(clazz.getResourceInfo(), clazz);
            }
        } else {
            resource = PersistableResource.create(clazz, ClazzAssert::canCreate, persistence);
        }

        if (clazz.isSimple()) {
            ClazzCache.simpleStream().put(clazz.getResourceInfo(), clazz);

            if (clazz.isCache()) {
                resource = NonPersistableResource.create(clazz, ClazzAssert_Simple::canCreate);
            } else {
                resource = PersistableResource.create(clazz, ClazzAssert::canCreate, persistence);
            }
        } else {
            ClazzCache.stream().put(clazz.getResourceInfo(), clazz);

            if (clazz.isCache()) {
                resource = NonPersistableResource.create(clazz, ClazzAssert::canCreate);
            } else {
                resource = PersistableResource.create(clazz, ClazzAssert::canCreate, persistence);
            }
        }

        return this;
    }

    public PersistableCachitoBuilder alter(ClazzAlter clazzAlter) {
        fetchResource();

        if (clazz.isSimple()) {
            ResourceAlterHandler_Simple.alter(clazz, clazzAlter, persistence);
        } else {
            ResourceAlterHandler.alter(clazz, clazzAlter, persistence);
        }

        return this;
    }

    public PersistableCachitoBuilder delete(Long id) {
        fetchResource();

        if (resource.getClazz().isSimple()) {
            ResourceDeleteHandler_Simple.delete(resource, id);
        } else {
            ResourceDeleteHandler.delete(resource, id, persistence);
        }

        return this;
    }

    public PersistableCachitoBuilder drop() {
        fetchResource();

        if (nonNull(resource)) {
            if (clazz.isSimple()) {
                if (clazz.isCache()) {
                    ResourceDropHandler_Simple.drop(clazz);
                } else {
                    ResourceDropHandler_Simple.drop(clazz, persistence);
                }
            } else {
                if (clazz.isCache()) {
                    ResourceDropHandler.drop(clazz);
                } else {
                    ResourceDropHandler.drop(clazz, persistence);
                }
            }
        }

        return this;
    }

    public PersistableCachitoBuilder dropForce() {
        fetchResource();

        if (nonNull(resource)) {
            if (clazz.isSimple()) {
                ResourceDropHandler_Simple.dropForce(clazz, persistence);
            } else {
                ResourceDropHandler.dropForce(clazz, persistence);
            }
        }

        return this;
    }

    public List<Entity> get() {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource);
        } else {
            return ResourceGetHandler.get(resource, persistence);
        }
    }

    public List<Entity> get(FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, fieldsToDisplay);
        } else {
            return ResourceGetHandler.get(resource, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(Pagination pagination, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, pagination, fieldsToDisplay);
        } else {
            return ResourceGetHandler.get(resource, pagination, persistence, fieldsToDisplay);
        }
    }

    public Optional<Entity> getById(Long id) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.getById(resource, id);
        } else {
            return ResourceGetHandler.getById(resource, id, persistence);
        }
    }

    public Optional<Entity> getById(Long id, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.getById(resource, id, fieldsToDisplay);
        } else {
            return ResourceGetHandler.getById(resource, id, persistence, fieldsToDisplay);
        }
    }

    public Group get(GroupBy groupBy, Filter filter, LeftJoinWithClazz leftJoin, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            throw new LeftJoinOnSimpleResourceException(resource);
        } else {
            return ResourceGetHandler.get(resource, groupBy, filter, leftJoin, persistence, fieldsToDisplay);
        }
    }

    public Group get(GroupBy groupBy, LeftJoinWithClazz leftJoin, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            throw new LeftJoinOnSimpleResourceException(resource);
        } else {
            return ResourceGetHandler.get(resource, groupBy, leftJoin, persistence, fieldsToDisplay);
        }
    }

    public Group get(GroupBy groupBy, Filter filter, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, groupBy, filter, fieldsToDisplay);
        } else {
            return ResourceGetHandler.get(resource, groupBy, filter, persistence, fieldsToDisplay);
        }
    }

    public Group get(GroupBy groupBy, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, groupBy, fieldsToDisplay);
        } else {
            return ResourceGetHandler.get(resource, groupBy, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(Filter filter) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, filter);
        } else {
            return ResourceGetHandler.get(resource, filter, persistence);
        }
    }

    public List<Entity> get(Filter filter, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, filter, fieldsToDisplay);
        } else {
            return ResourceGetHandler.get(resource, filter, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(Filter filter,
                            LeftJoinWithClazz leftJoin,
                            FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            throw new LeftJoinOnSimpleResourceException(resource);
        } else {
            return ResourceGetHandler.get(resource, filter, leftJoin, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(Filter filter,
                            LeftJoinWithClazz leftJoin,
                            Pagination pagination,
                            FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            throw new LeftJoinOnSimpleResourceException(resource);
        } else {
            return ResourceGetHandler.get(resource, filter, leftJoin, pagination, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(LeftJoinWithClazz leftJoin, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            throw new LeftJoinOnSimpleResourceException(resource);
        } else {
            return ResourceGetHandler.get(resource, leftJoin, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(LeftJoinWithClazz leftJoin, Pagination pagination, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            throw new LeftJoinOnSimpleResourceException(resource);
        } else {
            return ResourceGetHandler.get(resource, leftJoin, pagination, persistence, fieldsToDisplay);
        }
    }

    public List<Entity> get(Filter filter, Pagination pagination, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourceGetHandler_Simple.get(resource, filter, pagination, fieldsToDisplay);
        } else {
            return ResourceGetHandler.get(resource, filter, pagination, persistence, fieldsToDisplay);
        }
    }

    public <T> Entity patch(T object) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePatchHandler_Simple.patch(resource, object);
        } else {
            return ResourcePatchHandler.patch(resource, object, persistence);
        }
    }

    public <T> Entity patch(T object, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePatchHandler_Simple.patch(resource, object, fieldsToDisplay);
        } else {
            return ResourcePatchHandler.patch(resource, object, persistence, fieldsToDisplay);
        }
    }

    public Entity patch(Map<String, Object> object) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePatchHandler_Simple.patch(resource, object);
        } else {
            return ResourcePatchHandler.patch(resource, object, persistence);
        }
    }

    public Entity patch(Map<String, Object> object, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePatchHandler_Simple.patch(resource, object, fieldsToDisplay);
        } else {
            return ResourcePatchHandler.patch(resource, object, persistence, fieldsToDisplay);
        }
    }

    public <T> Entity post(T object) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePostHandler_Simple.post(resource, object);
        } else {
            return ResourcePostHandler.post(resource, object, persistence);
        }
    }

    public <T> Entity post(T object, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePostHandler_Simple.post(resource, object, fieldsToDisplay);
        } else {
            return ResourcePostHandler.post(resource, object, persistence, fieldsToDisplay);
        }
    }

    public Entity post(Map<String, Object> object) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePostHandler_Simple.post(resource, object);
        } else {
            return ResourcePostHandler.post(resource, object, persistence, all());
        }
    }

    public Entity post(Map<String, Object> object, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePostHandler_Simple.post(resource, object, fieldsToDisplay);
        } else {
            return ResourcePostHandler.post(resource, object, persistence, fieldsToDisplay);
        }
    }

    public <T> Entity put(T object) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePutHandler_Simple.put(resource, object);
        } else {
            return ResourcePutHandler.put(resource, object, persistence);
        }
    }

    public <T> Entity put(T object, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePutHandler_Simple.put(resource, object, fieldsToDisplay);
        } else {
            return ResourcePutHandler.put(resource, object, persistence, fieldsToDisplay);
        }
    }

    public Entity put(Map<String, Object> object) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePutHandler_Simple.put(resource, object);
        } else {
            return ResourcePutHandler.put(resource, object, persistence);
        }
    }

    public Entity put(Map<String, Object> object, FieldsToDisplay fieldsToDisplay) {
        fetchResource();

        if (clazz.isSimple()) {
            return ResourcePutHandler_Simple.put(resource, object, fieldsToDisplay);
        } else {
            return ResourcePutHandler.put(resource, object, persistence, fieldsToDisplay);
        }
    }

    private void fetchResource() {
        if (isNull(persistence)) {
            resource = NonPersistableResource.get(clazz);
        } else {
            resource = PersistableResource.get(clazz, persistence);
        }
    }
}