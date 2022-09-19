package dsenta.cachito.model.dimension;

import dsenta.cachito.utils.DateIso8601;
import dsenta.queryablemap.wgb.WhiteGreyBlackTree;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static dsenta.cachito.mapper.objectinstance.DateMapper.toDate;

public class DateDimension extends Dimension<Date> {
    private static final long serialVersionUID = 1181958386925328541L;

    public DateDimension() {
        super(new WhiteGreyBlackTree<>(), Date.class);
    }

    @Override
    public List<List<Long>> getBiggerThan(Object key, boolean asc) {
        return super.getBiggerThan(toDate(key), asc);
    }

    @Override
    public List<List<Long>> getBiggerThanOrEquals(Object key, boolean asc) {
        return super.getBiggerThanOrEquals(toDate(key), asc);
    }

    @Override
    public List<Long> getEquals(Object key) {
        return super.getEquals(toDate(key));
    }

    @Override
    public List<List<Long>> getNotEquals(Object key, boolean asc) {
        return super.getNotEquals(toDate(key), asc);
    }

    @Override
    public List<List<Long>> getLessThan(Object key, boolean asc) {
        return super.getLessThan(toDate(key), asc);
    }

    @Override
    public List<List<Long>> getLessThanOrEquals(Object key, boolean asc) {
        return super.getLessThanOrEquals(toDate(key), asc);
    }

    @Override
    public List<List<Long>> getBetween(Object low, Object high, boolean asc) {
        return super.getBetween(toDate(low), toDate(high), asc);
    }

    @Override
    public void put(Object key) {
        super.put(toDate(key));
    }

    @Override
    public void put(Object key, Long id) {
        super.put(toDate(key), id);
    }

    @Override
    public void remove(Object key, Long id) {
        super.remove(toDate(key), id);
    }

    @Override
    public void remove(Object key) {
        super.remove(toDate(key));
    }

    @Override
    public IntDimension toIntDimension() {
        var dimension = new IntDimension();
        this.valueIdsMap.forEach((key, value) -> value.forEach(id -> dimension.put(key.getTime(), id)));
        return dimension;
    }

    @Override
    public StringDimension toStringDimension() {
        var dimension = new StringDimension();
        this.valueIdsMap.forEach((key, value) -> value.forEach(id -> dimension.put(DateIso8601.toString(key), id)));
        return dimension;
    }

    @Override
    public DateDimension toDateDimension() {
        return this;
    }

    @Override
    public boolean isRelationship() {
        return false;
    }

    public List<Long> getByHour(int hour, LocalDateTime start, LocalDateTime end, boolean asc) {
        return groupRangeFilter(start, end, asc)
                .stream()
                .filter(date -> LocalDateTime.ofInstant(date.getKey().toInstant(), ZoneId.systemDefault()).getHour() == hour)
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Long> getByDayOfWeek(int dayOfWeek, LocalDateTime start, LocalDateTime end, boolean asc) {
        return groupRangeFilter(start, end, asc)
                .stream()
                .filter(date -> LocalDateTime.ofInstant(date.getKey().toInstant(), ZoneId.systemDefault()).getDayOfWeek().getValue() == dayOfWeek)
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Long> getByDayOfMonth(int dayOfMonth, LocalDateTime start, LocalDateTime end, boolean asc) {
        return groupRangeFilter(start, end, asc)
                .stream()
                .filter(date -> LocalDateTime.ofInstant(date.getKey().toInstant(), ZoneId.systemDefault()).getDayOfMonth() == dayOfMonth)
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Long> getByDayOfYear(int dayOfYear, LocalDateTime start, LocalDateTime end, boolean asc) {
        return groupRangeFilter(start, end, asc)
                .stream()
                .filter(date -> LocalDateTime.ofInstant(date.getKey().toInstant(), ZoneId.systemDefault()).getDayOfYear() == dayOfYear)
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Long> getByWeekOfYear(int weekOfYear, LocalDateTime start, LocalDateTime end, boolean asc) {
        return groupRangeFilter(start, end, asc)
                .stream()
                .filter(date -> LocalDate
                        .ofInstant(date.getKey().toInstant(), ZoneId.systemDefault())
                        .get(WeekFields.ISO.weekOfWeekBasedYear()) == weekOfYear)
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Long> getByMonthOfYear(int monthOfYear, LocalDateTime start, LocalDateTime end, boolean asc) {
        return groupRangeFilter(start, end, asc)
                .stream()
                .filter(date -> LocalDate
                        .ofInstant(date.getKey().toInstant(), ZoneId.systemDefault())
                        .getMonthValue() == monthOfYear)
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Entry<Date, ArrayList<Long>>> groupRangeFilter(LocalDateTime start, LocalDateTime end, boolean asc) {
        start = start
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        end = end
                .plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        Date from = Date.from(start.toInstant(ZoneOffset.UTC));
        Date to = Date.from(end.toInstant(ZoneOffset.UTC));

        return asc ?
                this.valueIdsMap.getBetweenAsc(from, to) :
                this.valueIdsMap.getBetweenDesc(from, to);
    }
}