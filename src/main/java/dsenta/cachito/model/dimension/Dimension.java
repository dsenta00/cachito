package dsenta.cachito.model.dimension;

import dsenta.cachito.exception.AttributeValueTypeMismatchException;
import dsenta.cachito.exception.CannotConvertDimensionTypeException;
import dsenta.cachito.model.attribute.DataType;
import dsenta.queryablemap.QueryableMap;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Getter
public abstract class Dimension<T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 6812656717738865652L;
    protected QueryableMap<T, ArrayList<Long>> valueIdsMap;
    protected final DataType dataType;

    public Dimension(QueryableMap<T, ArrayList<Long>> valueIdsMap, Class<T> tClass) {
        this.valueIdsMap = valueIdsMap;
        this.dataType = DataType.fromClass(tClass);
    }

    public BoolDimension toBoolDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.BOOLEAN);
    }

    public IntDimension toIntDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.INTEGER);
    }

    public FloatDimension toFloatDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.FLOAT);
    }

    public StringDimension toStringDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.STRING);
    }

    public DateDimension toDateDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.DATE);
    }

    public RelationshipOneDimension toRelationshipOneDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.RELATIONSHIP_ONE);
    }

    public RelationshipZeroOneDimension toRelationshipZeroOneDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.RELATIONSHIP_ZERO_ONE);
    }

    public RelationshipOneManyDimension toRelationshipOneManyDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.RELATIONSHIP_ONE_MANY);
    }

    public RelationshipZeroManyDimension toRelationshipZeroManyDimension() {
        throw new CannotConvertDimensionTypeException(this.dataType, DataType.RELATIONSHIP_ZERO_MANY);
    }

    @SuppressWarnings("unchecked")
    public synchronized void put(Object key) {
        DataType dataType = DataType.fromClass(key.getClass());

        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }

        if (isNull(valueIdsMap.get(key))) {
            valueIdsMap.put((T) key, new ArrayList<>());
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void put(Object key, Long id) {
        DataType dataType = DataType.fromClass(key.getClass());

        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }

        ArrayList<Long> ids = valueIdsMap.get(key);

        if (isNull(ids)) {
            ids = new ArrayList<>();
            valueIdsMap.put((T) key, ids);
        }

        ids.add(id);
    }

    public synchronized void remove(Object key, Long id) {
        if (isNull(key)) {
            return;
        }
        
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        ArrayList<Long> ids = valueIdsMap.get(key);

        if (isNull(ids)) {
            return;
        }

        ids.remove(id);
    }

    public synchronized void remove(Object key) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        valueIdsMap.remove(key);
    }

    public synchronized List<List<Long>> getAsc() {
        return toIdList(valueIdsMap.getAsc());
    }

    public synchronized List<List<Long>> getDesc() {
        return toIdList(valueIdsMap.getDesc());
    }

    @SuppressWarnings("unchecked")
    public synchronized List<List<Long>> getBiggerThan(Object key, boolean asc) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        return toIdList(asc ? valueIdsMap.getBiggerThanAsc((T) key) : valueIdsMap.getBiggerThanDesc((T) key));
    }

    @SuppressWarnings("unchecked")
    public synchronized List<List<Long>> getBiggerThanOrEquals(Object key, boolean asc) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        return toIdList(asc ? valueIdsMap.getBiggerThanEqualsAsc((T) key) : valueIdsMap.getBiggerThanEqualsDesc((T) key));
    }

    public synchronized List<Long> getEquals(Object key) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        List<Long> ids = valueIdsMap.get(key);
        return Objects.nonNull(ids) ? ids : List.of();
    }

    @SuppressWarnings("unchecked")
    public synchronized List<List<Long>> getNotEquals(Object key, boolean asc) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        return toIdList(asc ? valueIdsMap.getNotEqualsAsc((T) key) : valueIdsMap.getNotEqualsDesc((T) key));
    }

    @SuppressWarnings("unchecked")
    public synchronized List<List<Long>> getLessThan(Object key, boolean asc) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        return toIdList(asc ? valueIdsMap.getLessThanAsc((T) key) : valueIdsMap.getLessThanDesc((T) key));
    }

    @SuppressWarnings("unchecked")
    public synchronized List<List<Long>> getLessThanOrEquals(Object key, boolean asc) {
        DataType dataType = DataType.fromClass(key.getClass());
        if (this.dataType != dataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        return toIdList(asc ? valueIdsMap.getLessThanEqualsAsc((T) key) : valueIdsMap.getLessThanEqualsDesc((T) key));
    }

    @SuppressWarnings("unchecked")
    public synchronized List<List<Long>> getBetween(Object low, Object high, boolean asc) {
        DataType lowDataType = DataType.fromClass(low.getClass());
        if (this.dataType != lowDataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        DataType highDataType = DataType.fromClass(high.getClass());
        if (this.dataType != highDataType) {
            throw new AttributeValueTypeMismatchException(this.dataType, dataType);
        }
        return toIdList(asc ? valueIdsMap.getBetweenAsc((T) low, (T) high) : valueIdsMap.getBetweenDesc((T) low, (T) high));
    }

    public synchronized List<List<T>> getKeysListRelatedWithIds(List<Long> relatedIds) {
        var entryList = valueIdsMap.getAsc();

        final List<Entry<T, ArrayList<Long>>> finalEntryList = entryList
                .stream()
                .filter(wgbData -> wgbData.getValue().stream().anyMatch(relatedIds::contains))
                .collect(Collectors.toList());

        return relatedIds
                .stream()
                .map(relatedId -> finalEntryList
                        .stream()
                        .filter(entry -> entry.getValue().contains(relatedId))
                        .map(Entry::getKey)
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

    }

    public abstract boolean isRelationship();

    private synchronized List<List<Long>> toIdList(List<Entry<T, ArrayList<Long>>> entries) {
        return entries.stream().map(Entry::getValue).collect(Collectors.toList());
    }
}