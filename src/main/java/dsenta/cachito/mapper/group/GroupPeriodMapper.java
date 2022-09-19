package dsenta.cachito.mapper.group;

import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.group.GroupPeriod;
import dsenta.cachito.model.group.GroupResult;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.model.resource.Resource;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GroupPeriodMapper {

    public static GroupPeriod fromGroupResultWithParent(Resource resource,
                                                        GroupResult groupResult,
                                                        Persistence persistence,
                                                        FieldsToDisplay fieldsToDisplay) {
        return new GroupPeriod(
                groupResult.getFrom(),
                groupResult.getTo(),
                groupResult.getPeriodName(),
                groupResult.getIds().stream()
                        .map(id -> ResourceGetHandler.getById(resource, id, persistence, fieldsToDisplay))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }
}