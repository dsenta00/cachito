package dsenta.cachito.handler.dimension;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.dimension.BoolDimension;
import dsenta.cachito.model.group.GroupResult;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BoolDimensionHandler {

    public static List<GroupResult> groupBy(BoolDimension boolDimension, GroupBy groupBy) {
        return DimensionHandler.groupByNoInterval(boolDimension, groupBy);
    }
}