package dsenta.cachito.resource.util;

import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.filter.AndWhere;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.group.Group;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.leftjoin.LeftJoinWithClazz;
import dsenta.cachito.model.pagination.Pagination;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import dsenta.cachito.resource.testmodel.PersistenceImplTest;
import lombok.NoArgsConstructor;

import java.util.List;

import static dsenta.cachito.model.fields.FieldsToDisplay.all;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AssertUtil {

    private static final Persistence persistence = new PersistenceImplTest();

    public static void assertQuery(Pagination pagination,
                                   Clazz clazz,
                                   AssertCallbackEntityList assertCallbackEntityList) {
        Resource resource = PersistableResource.get(clazz, persistence);
        List<Entity> entities = ResourceGetHandler.get(resource, pagination, persistence, all());
        assertCallbackEntityList.assertEntityList(entities);
    }

    public static void assertQuery(List<AndWhere> andWhereList,
                                   Pagination pagination,
                                   Clazz clazz,
                                   AssertCallbackEntityList assertCallbackEntityList) {
        Resource resource = PersistableResource.get(clazz, persistence);
        List<Entity> entities = ResourceGetHandler.get(resource, new Filter(andWhereList), pagination, persistence, all());
        assertCallbackEntityList.assertEntityList(entities);
    }

    public static void assertQuery(List<AndWhere> andWhereList,
                                   Clazz clazz,
                                   AssertCallbackEntityList assertCallbackEntityList) {
        Resource resource = PersistableResource.get(clazz, persistence);
        List<Entity> entities = ResourceGetHandler.get(resource, new Filter(andWhereList), persistence, all());
        assertCallbackEntityList.assertEntityList(entities);
    }

    public static void assertQuery(List<AndWhere> andWhereList,
                                   LeftJoinWithClazz leftJoin,
                                   Clazz clazz,
                                   AssertCallbackEntityList assertCallbackEntityList) {
        Resource resource = PersistableResource.get(clazz, persistence);
        List<Entity> entities = ResourceGetHandler.get(resource, new Filter(andWhereList), leftJoin, persistence, all());
        assertCallbackEntityList.assertEntityList(entities);
    }

    public static void assertQuery(GroupBy groupBy,
                                   Clazz clazz,
                                   AssertCallbackEntityGroup assertCallbackEntityGroup) {
        Resource resource = PersistableResource.get(clazz, persistence);
        Group group = ResourceGetHandler.get(resource, groupBy, persistence, all());
        assertCallbackEntityGroup.assertGroup(group);
    }

    public static void assertQuery(GroupBy groupBy,
                                   List<AndWhere> andWhereList,
                                   Clazz clazz,
                                   AssertCallbackEntityGroup assertCallbackEntityGroup) {
        Resource resource = PersistableResource.get(clazz, persistence);
        Group group = ResourceGetHandler.get(resource, groupBy, new Filter(andWhereList), persistence, all());
        assertCallbackEntityGroup.assertGroup(group);
    }
}