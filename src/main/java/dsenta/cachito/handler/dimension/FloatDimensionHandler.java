package dsenta.cachito.handler.dimension;

import dsenta.cachito.model.dimension.FloatDimension;
import dsenta.cachito.model.group.GroupResult;
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
public final class FloatDimensionHandler {

    public static List<GroupResult> groupBy(FloatDimension floatDimension, Attribute attribute, GroupBy groupBy) {
        if (Objects.isNull(groupBy.getInterval())) {
            return groupByNoInterval(floatDimension, groupBy);
        } else {
            return groupByInterval(floatDimension, attribute, groupBy);
        }
    }

    private static List<GroupResult> groupByInterval(FloatDimension floatDimension, Attribute attribute, GroupBy groupBy) {
        try {
            double interval = Double.parseDouble(groupBy.getInterval());
            double fromValue = Objects.isNull(groupBy.getFrom()) ?
                    floatDimension.getValueIdsMap().getMin() :
                    Double.parseDouble(groupBy.getFrom());
            double toValue = Objects.isNull(groupBy.getTo()) ?
                    floatDimension.getValueIdsMap().getMax() :
                    Double.parseDouble(groupBy.getTo());

            var result = new ArrayList<GroupResult>();

            while (fromValue < toValue) {
                double upperBound = fromValue + interval;
                upperBound = Math.min(upperBound, toValue);

                result.add(new GroupResult(fromValue, upperBound, String.format("%f-%f", fromValue, upperBound)));

                fromValue = upperBound;
            }

            return result
                    .stream()
                    .peek(groupResult -> groupResult.getIds().addAll(
                            floatDimension
                                    .getBetween(groupResult.getFrom(), groupResult.getTo(), groupBy.isAsc())
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new AttributeValueTypeMismatchException(attribute, DataType.FLOAT);
        }
    }

    private static List<GroupResult> groupByNoInterval(FloatDimension floatDimension, GroupBy groupBy) {
        var valueIdsMap = floatDimension.getValueIdsMap();
        var entries = groupBy.isAsc() ? valueIdsMap.getAsc() : valueIdsMap.getDesc();

        return entries.stream()
                .map(e -> new GroupResult(e.getKey(), e.getKey(), e.getKey().toString(), new ArrayList<>(e.getValue())))
                .collect(Collectors.toList());
    }
}