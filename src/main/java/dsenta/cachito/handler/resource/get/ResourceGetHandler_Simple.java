package dsenta.cachito.handler.resource.get;

import dsenta.cachito.handler.dimension.DimensionHandler_Simple;
import dsenta.cachito.mapper.entity.EntryToEntityMapper_Simple;
import dsenta.cachito.model.dimension.Dimensions;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.filter.Filter;
import dsenta.cachito.model.pagination.Pagination;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.utils.PaginationOfList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.getDimensions;
import static dsenta.cachito.handler.dimension.DimensionHandler_Simple.filterFromDimensions;

public class ResourceGetHandler_Simple {

    public List<Entity> get(Resource resource) {
        return EntryToEntityMapper_Simple.toEntityList(resource.getObjectInstances().getDesc(), resource);
    }

    public List<Entity> get(Resource resource, Pagination pagination) {
        return EntryToEntityMapper_Simple.toEntityList(
                PaginationOfList.getPage(
                        resource.getObjectInstances().getDesc(),
                        pagination.getPageNo(),
                        pagination.getPerPage()
                ),
                resource
        );
    }

    public Optional<Entity> getById(Resource resource, Long id) {
        var entry = resource.getObjectInstances().getByKey(id);
        return EntryToEntityMapper_Simple.toEntity(entry, resource);
    }

    public List<Entity> get(Resource resource, Filter filter) {
        Dimensions dimensions = DimensionHandler_Simple.getDimensions(resource);

        return filterFromDimensions(filter, dimensions)
                .stream()
                .map(id -> getById(resource, id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Entity> get(Resource resource, Filter filter, Pagination pagination) {
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