package dsenta.cachito.handler.resource.get;

import dsenta.cachito.handler.dimension.DimensionHandler_Simple;
import dsenta.cachito.model.dimension.Dimensions;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.group.Group;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.pagination.Pagination;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.PaginationOfList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.filterFromDimensions;
import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.getDimensions;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper_Simple.toEntity;
import static dsenta.cachito.mapper.entity.EntryToEntityMapper_Simple.toEntityList;
import static dsenta.cachito.mapper.group.GroupPeriodMapper_Simple.fromGroupResult;

public final class ResourceGetHandler_Simple {

    public static List<Entity> get(Resource resource) {
        return toEntityList(resource.getObjectInstances().getDesc(), resource);
    }

    public static List<Entity> get(Resource resource, FieldsToDisplay fieldsToDisplay) {
        return toEntityList(resource.getObjectInstances().getDesc(), resource).stream()
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource, Pagination pagination) {
        return toEntityList(
                PaginationOfList.getPage(
                        resource.getObjectInstances().getDesc(),
                        pagination.getPageNo(),
                        pagination.getPerPage()
                ),
                resource
        );
    }

    public static List<Entity> get(Resource resource, Pagination pagination, FieldsToDisplay fieldsToDisplay) {
        var entries = PaginationOfList.getPage(
                resource.getObjectInstances().getDesc(),
                pagination.getPageNo(),
                pagination.getPerPage()
        );

        return toEntityList(entries, resource).stream()
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .collect(Collectors.toList());
    }

    public static Optional<Entity> getById(Resource resource, Long id) {
        var entry = resource.getObjectInstances().getByKey(id);
        return toEntity(entry, resource);
    }

    public static Optional<Entity> getById(Resource resource, Long id, FieldsToDisplay fieldsToDisplay) {
        var entry = resource.getObjectInstances().getByKey(id);
        return toEntity(entry, resource).map(entity -> entity.clonePartially(fieldsToDisplay));
    }

    public static List<Entity> get(Resource resource, Filter filter) {
        var dimensions = getDimensions(resource);

        return filterFromDimensions(filter, dimensions)
                .stream()
                .map(id -> getById(resource, id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static List<Entity> get(Resource resource, Filter filter, FieldsToDisplay fieldsToDisplay) {
        var dimensions = getDimensions(resource);

        return filterFromDimensions(filter, dimensions)
                .stream()
                .map(id -> getById(resource, id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(entity -> entity.clonePartially(fieldsToDisplay))
                .collect(Collectors.toList());
    }

    public static Group get(Resource resource,
                            GroupBy groupBy,
                            Filter filter,
                            FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource);
        List<Long> idsToRemain = filterFromDimensions(filter, dimensions);

        return new Group(
                DimensionHandler_Simple.groupFromDimensions(resource.getClazz(), groupBy, dimensions)
                        .stream()
                        .peek(groupResult -> groupResult.getIds().retainAll(idsToRemain))
                        .map(groupResult -> fromGroupResult(resource, groupResult, fieldsToDisplay))
                        .collect(Collectors.toList())
        );
    }

    public static List<Entity> get(Resource resource,
                                   Filter filter,
                                   Pagination pagination,
                                   FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource);
        List<Long> filteredIds = filterFromDimensions(filter, dimensions);

        return PaginationOfList.getPage(filteredIds, pagination.getPageNo(), pagination.getPerPage())
                .stream()
                .map(id -> getById(resource, id, fieldsToDisplay))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static Group get(Resource resource,
                            GroupBy groupBy,
                            FieldsToDisplay fieldsToDisplay) {
        Dimensions dimensions = getDimensions(resource);

        return new Group(
                DimensionHandler_Simple.groupFromDimensions(resource.getClazz(), groupBy, dimensions)
                        .stream()
                        .map(groupResult -> fromGroupResult(resource, groupResult, fieldsToDisplay))
                        .collect(Collectors.toList())
        );
    }

    public static List<Entity> get(Resource resource, Filter filter, Pagination pagination) {
        Dimensions dimensions = getDimensions(resource);
        List<Long> filteredIds = filterFromDimensions(filter, dimensions);

        return PaginationOfList
                .getPage(filteredIds, pagination.getPageNo(), pagination.getPerPage())
                .stream()
                .map(id -> getById(resource, id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}