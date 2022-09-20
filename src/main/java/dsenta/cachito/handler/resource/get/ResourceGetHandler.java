package dsenta.cachito.handler.resource.get;

import dsenta.cachito.mapper.dimension.IdResultFlatMapper;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.dimension.Dimensions;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.group.Group;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.leftjoin.LeftJoinWithClazz;
import dsenta.cachito.model.pagination.Pagination;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.PaginationOfList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dsenta.cachito.handler.dimension.DimensionHandler.*;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper.toEntity;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper.toEntityList;
import static dsenta.cachito.mapper.group.GroupPeriodMapper.fromGroupResultWithParent;
import static dsenta.cachito.model.fields.FieldsToDisplay.all;

public final class ResourceGetHandler {

    public static List<Entity> get(Resource resource, Persistence persistence) {
        return get(resource, persistence, all());
    }

    public static List<Entity> get(Resource resource,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        return toEntityList(
                resource.getObjectInstances().getDesc(),
                resource,
                persistence,
                fieldsToDisplay
        );
    }

    public static List<Entity> get(Resource resource,
                                   Pagination pagination,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        return toEntityList(
                PaginationOfList.getPage(
                        resource.getObjectInstances().getDesc(),
                        pagination.getPageNo(),
                        pagination.getPerPage()
                ),
                resource,
                persistence,
                fieldsToDisplay
        );
    }

    public static Optional<Entity> getById(Resource resource, Long id, Persistence persistence) {
        var entry = resource.getObjectInstances().getByKey(id);
        return toEntity(entry, resource, persistence);
    }

    public static Optional<Entity> getById(Resource resource, Long id, Persistence persistence, FieldsToDisplay fieldsToDisplay) {
        return getById(resource, id, persistence).map(entity -> entity.clonePartially(fieldsToDisplay));
    }

    public static Group get(Resource resource,
                            GroupBy groupBy,
                            Filter filter,
                            LeftJoinWithClazz leftJoin,
                            Persistence persistence,
                            FieldsToDisplay fieldsToDisplay) {
        Clazz clazz = resource.getClazz();
        List<Long> idsFromLeftJoin = leftJoinFromDimensions(clazz, leftJoin, persistence);

        Dimensions dimensions = getDimensions(resource, persistence);
        List<Long> idsFromFilter = filterFromDimensions(clazz, filter, dimensions, persistence);
        List<Long> filteredIds = IdResultFlatMapper.intersect(idsFromFilter, idsFromLeftJoin);

        return new Group(
                groupFromDimensions(resource.getClazz(), groupBy, dimensions, persistence, fieldsToDisplay)
                        .stream()
                        .peek(groupResult -> groupResult.getIds().retainAll(filteredIds))
                        .map(groupResult -> fromGroupResultWithParent(resource, groupResult, persistence, fieldsToDisplay))
                        .collect(Collectors.toList())
        );
    }

    public static Group get(Resource resource,
                            GroupBy groupBy,
                            LeftJoinWithClazz leftJoin,
                            Persistence persistence,
                            FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource, persistence);
        List<Long> idsToRemain = leftJoinFromDimensions(resource.getClazz(), leftJoin, persistence);

        return new Group(
                groupFromDimensions(resource.getClazz(), groupBy, dimensions, persistence, fieldsToDisplay)
                        .stream()
                        .peek(groupResult -> groupResult.getIds().retainAll(idsToRemain))
                        .map(groupResult -> fromGroupResultWithParent(resource, groupResult, persistence, fieldsToDisplay))
                        .collect(Collectors.toList())
        );
    }

    public static Group get(Resource resource,
                            GroupBy groupBy,
                            Filter filter,
                            Persistence persistence,
                            FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource, persistence);
        List<Long> idsToRemain = filterFromDimensions(resource.getClazz(), filter, dimensions, persistence);

        return new Group(
                groupFromDimensions(resource.getClazz(), groupBy, dimensions, persistence, fieldsToDisplay)
                        .stream()
                        .peek(groupResult -> groupResult.getIds().retainAll(idsToRemain))
                        .map(groupResult -> fromGroupResultWithParent(resource, groupResult, persistence, fieldsToDisplay))
                        .collect(Collectors.toList())
        );
    }

    public static Group get(Resource resource,
                            GroupBy groupBy,
                            Persistence persistence,
                            FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource, persistence);

        return new Group(
                groupFromDimensions(resource.getClazz(), groupBy, dimensions, persistence, fieldsToDisplay)
                        .stream()
                        .map(groupResult -> fromGroupResultWithParent(resource, groupResult, persistence, fieldsToDisplay))
                        .collect(Collectors.toList())
        );
    }

    public static List<Entity> get(Resource resource,
                                   Filter filter,
                                   Persistence persistence) {
        return get(resource, filter, persistence, all());
    }

    public static List<Entity> get(Resource resource,
                                   Filter filter,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource, persistence);

        return filterFromDimensions(resource.getClazz(), filter, dimensions, persistence)
                .stream()
                .map(id -> getById(resource, id, persistence, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource,
                                   Filter filter,
                                   LeftJoinWithClazz leftJoin,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        Clazz clazz = resource.getClazz();
        List<Long> idsFromLeftJoin = leftJoinFromDimensions(clazz, leftJoin, persistence);
        Dimensions dimensions = getDimensions(resource, persistence);
        List<Long> idsFromFilter = filterFromDimensions(clazz, filter, dimensions, persistence);

        return IdResultFlatMapper.intersect(idsFromFilter, idsFromLeftJoin)
                .stream()
                .map(id -> getById(resource, id, persistence, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource,
                                   Filter filter,
                                   LeftJoinWithClazz leftJoin,
                                   Pagination pagination,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        Clazz clazz = resource.getClazz();
        List<Long> idsFromLeftJoin = leftJoinFromDimensions(clazz, leftJoin, persistence);
        Dimensions dimensions = getDimensions(resource, persistence);
        List<Long> idsFromFilter = filterFromDimensions(clazz, filter, dimensions, persistence);
        List<Long> filteredIds = IdResultFlatMapper.intersect(idsFromFilter, idsFromLeftJoin);

        return PaginationOfList.getPage(filteredIds, pagination.getPageNo(), pagination.getPerPage())
                .stream()
                .map(id -> getById(resource, id, persistence, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource,
                                   LeftJoinWithClazz leftJoin,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        Clazz clazz = resource.getClazz();

        return leftJoinFromDimensions(clazz, leftJoin, persistence)
                .stream()
                .map(id -> getById(resource, id, persistence, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource,
                                   LeftJoinWithClazz leftJoin,
                                   Pagination pagination,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        Clazz clazz = resource.getClazz();
        List<Long> filteredIds = leftJoinFromDimensions(clazz, leftJoin, persistence);

        return PaginationOfList.getPage(filteredIds, pagination.getPageNo(), pagination.getPerPage())
                .stream()
                .map(id -> getById(resource, id, persistence, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource,
                                   Filter filter,
                                   Pagination pagination,
                                   Persistence persistence,
                                   FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource, persistence);
        List<Long> filteredIds = filterFromDimensions(resource.getClazz(), filter, dimensions, persistence);

        return PaginationOfList.getPage(filteredIds, pagination.getPageNo(), pagination.getPerPage())
                .stream()
                .map(id -> getById(resource, id, persistence, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}