package dsenta.cachito.mapper.group;

import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.group.GroupPeriod;
import dsenta.cachito.model.group.GroupResult;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.stream.Collectors;

import static dsenta.cachito.handler.resource.get.ResourceGetHandler_Simple.getById;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GroupPeriodMapper_Simple {

    public static GroupPeriod fromGroupResult(Resource resource,
                                              GroupResult groupResult,
                                              FieldsToDisplay fieldsToDisplay) {
        return new GroupPeriod(
                groupResult.getFrom(),
                groupResult.getTo(),
                groupResult.getPeriodName(),
                groupResult.getIds().stream()
                        .map(id -> getById(resource, id, fieldsToDisplay))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }
}