package dsenta.cachito.handler.dimension;

import dsenta.cachito.model.dimension.DateDimension;
import dsenta.cachito.model.group.GroupResult;
import dsenta.cachito.exception.AttributeValueTypeMismatchException;
import dsenta.cachito.exception.UnsupportedGroupIntervalException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.groupby.GroupByDate;
import dsenta.cachito.utils.DateIso8601;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateDimensionHandler {

    public static List<GroupResult> groupBy(DateDimension dateDimension, Attribute attribute, GroupBy groupBy) {
        if (isNull(groupBy.getInterval())) {
            return DimensionHandler.groupByNoInterval(dateDimension, groupBy);
        } else {
            return groupByWithInterval(dateDimension, attribute, groupBy);
        }
    }

    private static List<GroupResult> groupByWithInterval(DateDimension dateDimension, Attribute attribute, GroupBy groupBy) {
        try {
            GroupByDate interval = GroupByDate.valueOf(groupBy.getInterval());
            Date fromValue = isNull(groupBy.getFrom()) ?
                    dateDimension.getValueIdsMap().getMin() :
                    DateIso8601.fromString(groupBy.getFrom());
            Date toValue = isNull(groupBy.getTo()) ?
                    dateDimension.getValueIdsMap().getMax() :
                    DateIso8601.fromString(groupBy.getTo());

            LocalDateTime start = LocalDateTime.ofInstant(fromValue.toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(toValue.toInstant(), ZoneId.systemDefault());

            switch (interval) {
                case DATE:
                    return groupByDate(dateDimension, start, end, groupBy.isAsc());
                case YEAR:
                    return groupByYear(dateDimension, start, end, groupBy.isAsc());
                case HOURDAY:
                    return groupByHourDay(dateDimension, start, end, groupBy.isAsc());
                case DAYWEEK:
                    return groupDayWeek(dateDimension, start, end, groupBy.isAsc());
                case DAYMONTH:
                    return groupDayMonth(dateDimension, start, end, groupBy.isAsc());
                case DAYYEAR:
                    return groupDayYear(dateDimension, start, end, groupBy.isAsc());
                case WEEKYEAR:
                    return groupWeekYear(dateDimension, start, end, groupBy.isAsc());
                case MONTHYEAR:
                    return groupMonthYear(dateDimension, start, end, groupBy.isAsc());
                default:
                    throw new UnsupportedGroupIntervalException(interval.name());
            }
        } catch (NumberFormatException e) {
            throw new AttributeValueTypeMismatchException(attribute, DataType.DATE);
        }
    }

    private static List<GroupResult> groupMonthYear(DateDimension dateDimension,
                                                    LocalDateTime start,
                                                    LocalDateTime end,
                                                    boolean asc) {
        var monthYearList = IntStream
                .range(Month.JANUARY.getValue(), Month.DECEMBER.getValue() + 1)
                .boxed()
                .map(from -> new GroupResult(from, (from + 1) % (Month.DECEMBER.getValue() + 1), String.format("%s", Month.of(from))))
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(monthYearList);
        }

        return monthYearList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension.getByMonthOfYear((Integer) groupResult.getFrom(), start, end, asc)
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupWeekYear(DateDimension dateDimension,
                                                   LocalDateTime start,
                                                   LocalDateTime end,
                                                   boolean asc) {
        var weekYearList = IntStream
                .range(1, 54)
                .boxed()
                .map(from -> new GroupResult(from, (from + 1) % 54, String.format("%02d", from)))
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(weekYearList);
        }

        return weekYearList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension.getByWeekOfYear((Integer) groupResult.getFrom(), start, end, asc)
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupDayYear(DateDimension dateDimension,
                                                  LocalDateTime start,
                                                  LocalDateTime end,
                                                  boolean asc) {
        var dayYearList = IntStream
                .range(1, 367)
                .boxed()
                .map(from -> new GroupResult(from, (from + 1) % 367, String.format("%03d", from)))
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(dayYearList);
        }

        return dayYearList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension.getByDayOfYear((Integer) groupResult.getFrom(), start, end, asc)
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupDayMonth(DateDimension dateDimension,
                                                   LocalDateTime start,
                                                   LocalDateTime end,
                                                   boolean asc) {
        var dayMonthList = IntStream
                .range(1, 32)
                .boxed()
                .map(from -> new GroupResult(from, (from + 1) % 32, String.format("%02d", from)))
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(dayMonthList);
        }

        return dayMonthList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension.getByDayOfMonth((Integer) groupResult.getFrom(), start, end, asc)
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupDayWeek(DateDimension dateDimension,
                                                  LocalDateTime start,
                                                  LocalDateTime end,
                                                  boolean asc) {
        var dayWeekList = IntStream
                .range(DayOfWeek.MONDAY.getValue(), DayOfWeek.SUNDAY.getValue() + 1)
                .boxed()
                .map(from -> new GroupResult(from, (from + 1) % (DayOfWeek.SUNDAY.getValue() + 1), String.format("%s", DayOfWeek.of(from))))
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(dayWeekList);
        }

        return dayWeekList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension.getByDayOfWeek((Integer) groupResult.getFrom(), start, end, asc)
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupByHourDay(DateDimension dateDimension,
                                                    LocalDateTime start,
                                                    LocalDateTime end,
                                                    boolean asc) {
        var hourDayList = IntStream
                .range(0, 24)
                .boxed()
                .map(from -> new GroupResult(from, (from + 1) % 24, String.format("%02d", from)))
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(hourDayList);
        }

        return hourDayList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension.getByHour((Integer) groupResult.getFrom(), start, end, asc)
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupByYear(DateDimension dateDimension,
                                                 LocalDateTime start,
                                                 LocalDateTime end,
                                                 boolean asc) {
        start = start
                .withDayOfMonth(1)
                .withMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        if (end.getMonth().getValue() > 1 || end.getDayOfMonth() > 1) {
            end = end
                    .withMonth(12)
                    .withDayOfMonth(31)
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59);
        }

        var yearGroupList = start
                .toLocalDate()
                .datesUntil(end.toLocalDate(), Period.ofYears(1))
                .map(localDate -> {
                    Date from = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    Date to = Date.from(localDate.atStartOfDay().plusYears(1).atZone(ZoneId.systemDefault()).toInstant());

                    return new GroupResult(from, to, String.format("%04d", localDate.getYear()));
                })
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(yearGroupList);
        }

        return yearGroupList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension
                                .getBetween(groupResult.getFrom(), groupResult.getTo(), asc)
                                .stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    private static List<GroupResult> groupByDate(DateDimension dateDimension,
                                                 LocalDateTime start,
                                                 LocalDateTime end,
                                                 boolean asc) {
        start = start
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        if (end.getHour() > 0 || end.getMinute() > 0 || end.getSecond() > 0) {
            end = end
                    .plusDays(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);
        }

        var dateList = start
                .toLocalDate()
                .datesUntil(end.toLocalDate(), Period.ofDays(1))
                .map(localDate -> {
                    Date from = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    Date to = Date.from(localDate.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

                    return new GroupResult(from, to, String.format("%04d-%02d-%02d",
                            localDate.getYear(),
                            localDate.getMonth().getValue(),
                            localDate.getDayOfMonth()
                    ));
                })
                .collect(Collectors.toList());

        if (!asc) {
            Collections.reverse(dateList);
        }

        return dateList
                .stream()
                .peek(groupResult -> groupResult.getIds().addAll(
                        dateDimension
                                .getBetween(groupResult.getFrom(), groupResult.getTo(), asc)
                                .stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}