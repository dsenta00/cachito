package dsenta.cachito.handler.dimension;

import dsenta.cachito.model.dimension.RelationshipDimension;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.group.GroupResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RelationshipDimensionHandler {

    public static List<GroupResult> groupBy(RelationshipDimension relationshipDimension) {
        Map<Long, List<Long>> result = new HashMap<>();

        relationshipDimension
                .getValueIdsMap()
                .getAsc()
                .forEach(e -> e.getValue().forEach(relatedId -> {
                    if (!result.containsKey(relatedId)) {
                        result.put(relatedId, new LinkedList<>());
                    }

                    result.get(relatedId).add(e.getKey());
                }));

        return result.entrySet()
                .stream()
                .map(e -> new GroupResult(e.getKey(), e.getKey(), e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
    }
}