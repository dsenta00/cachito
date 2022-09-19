package dsenta.cachito.model.dimension;

import dsenta.queryablemap.wgb.WhiteGreyBlackTree;

public class FloatDimension extends Dimension<Double> {
    private static final long serialVersionUID = 4201453539360737735L;

    public FloatDimension() {
        super(new WhiteGreyBlackTree<>(), Double.class);
    }

    @Override
    public synchronized FloatDimension toFloatDimension() {
        return this;
    }

    @Override
    public synchronized StringDimension toStringDimension() {
        var dimension = new StringDimension();
        this.valueIdsMap.forEach((key, value) -> value.forEach(id -> dimension.put(key.toString(), id)));
        return dimension;
    }

    @Override
    public boolean isRelationship() {
        return false;
    }
}
