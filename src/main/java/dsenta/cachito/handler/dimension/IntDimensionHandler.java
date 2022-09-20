package dsenta.cachito.handler.dimension;

import dsenta.cachito.model.group.GroupResult;
import dsenta.cachito.model.dimension.IntDimension;
import dsenta.cachito.exception.attribute.AttributeValueTypeMismatchException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.groupby.GroupBy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntDimensionHandler {

    public static List<GroupResult> groupBy(IntDimension intDimension, Attribute attribute, GroupBy groupBy) {
        if (Objects.isNull(groupBy.getInterval())) {
            return DimensionHandler.groupByNoInterval(intDimension, groupBy);
        } else {
            return groupByInterval(intDimension, attribute, groupBy);
        }
    }

    private static List<GroupResult> groupByInterval(IntDimension intDimension, Attribute attribute, GroupBy groupBy) {
        try {
            long interval = Long.parseLong(groupBy.getInterval());
            long fromValue = Objects.isNull(groupBy.getFrom()) ?
                    intDimension.getValueIdsMap().getMin() :
                    Long.parseLong(groupBy.getFrom());
            long toValue = Objects.isNull(groupBy.getTo()) ?
                    intDimension.getValueIdsMap().getMax() :
                    Long.parseLong(groupBy.getTo());

            var result = new ArrayList<GroupResult>();

            while (fromValue < toValue) {
                long upperBound = fromValue + interval;
                upperBound = Math.min(upperBound, toValue);

                result.add(new GroupResult(fromValue, upperBound, String.format("%d-%d", fromValue, upperBound)));

                fromValue = upperBound;
            }

            return result
                    .stream()
                    .peek(groupResult -> groupResult.getIds().addAll(
                            intDimension
                                    .getBetween(groupResult.getFrom(), groupResult.getTo(), groupBy.isAsc())
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new AttributeValueTypeMismatchException(attribute, DataType.INTEGER);
        }
    }
}