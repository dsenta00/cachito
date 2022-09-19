package dsenta.cachito.handler.dimension;

import dsenta.cachito.model.group.GroupResult;
import dsenta.cachito.model.dimension.StringDimension;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.groupby.GroupBy;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringDimensionHandler {

    public static List<GroupResult> groupBy(StringDimension stringDimension, GroupBy groupBy) {
        return DimensionHandler.groupByNoInterval(stringDimension, groupBy);
    }
}