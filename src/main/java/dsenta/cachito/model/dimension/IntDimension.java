package dsenta.cachito.model.dimension;

import dsenta.queryablemap.wgb.WhiteGreyBlackTree;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static dsenta.cachito.mapper.objectinstance.IntMapper.toLongInt;

public class IntDimension extends Dimension<Long> {
    private static final long serialVersionUID = -3726965892094482747L;

    public IntDimension() {
        super(new WhiteGreyBlackTree<>(), Long.class);
    }

    @Override
    public List<List<Long>> getBiggerThan(Object key, boolean asc) {
        return super.getBiggerThan(toLongInt(key), asc);
    }

    @Override
    public List<List<Long>> getBiggerThanOrEquals(Object key, boolean asc) {
        return super.getBiggerThanOrEquals(toLongInt(key), asc);
    }

    @Override
    public List<Long> getEquals(Object key) {
        return super.getEquals(toLongInt(key));
    }

    @Override
    public List<List<Long>> getNotEquals(Object key, boolean asc) {
        return super.getNotEquals(toLongInt(key), asc);
    }

    @Override
    public List<List<Long>> getLessThan(Object key, boolean asc) {
        return super.getLessThan(toLongInt(key), asc);
    }

    @Override
    public List<List<Long>> getLessThanOrEquals(Object key, boolean asc) {
        return super.getLessThanOrEquals(toLongInt(key), asc);
    }

    @Override
    public List<List<Long>> getBetween(Object low, Object high, boolean asc) {
        return super.getBetween(toLongInt(low), toLongInt(high), asc);
    }

    @Override
    public IntDimension toIntDimension() {
        return this;
    }

    @Override
    public FloatDimension toFloatDimension() {
        var dimension = new FloatDimension();
        this.valueIdsMap.forEach((key, value) -> value.forEach(id -> dimension.put(key.doubleValue(), id)));
        return dimension;
    }

    @Override
    public StringDimension toStringDimension() {
        var dimension = new StringDimension();
        this.valueIdsMap.forEach((key, value) -> value.forEach(id -> dimension.put(key.toString(), id)));
        return dimension;
    }

    @Override
    public DateDimension toDateDimension() {
        DateDimension dimension = new DateDimension();
        this.valueIdsMap.forEach((key, value) -> {
            var date = Date.from(Instant.ofEpochMilli(key));
            value.forEach(id -> dimension.put(date, id));
        });
        return dimension;
    }

    @Override
    public boolean isRelationship() {
        return false;
    }
}
