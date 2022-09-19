package dsenta.cachito.model.dimension;

import dsenta.queryablemap.wgb.WhiteGreyBlackTree;

public class BoolDimension extends Dimension<Boolean> {
    private static final long serialVersionUID = -8660408567101136442L;

    public BoolDimension() {
        super(new WhiteGreyBlackTree<>(), Boolean.class);
    }

    @Override
    public BoolDimension toBoolDimension() {
        return this;
    }

    @Override
    public IntDimension toIntDimension() {
        IntDimension dimension = new IntDimension();
        this.getEquals(false).forEach(id -> dimension.put(0, id));
        this.getEquals(true).forEach(id -> dimension.put(1, id));
        return dimension;
    }

    @Override
    public FloatDimension toFloatDimension() {
        FloatDimension dimension = new FloatDimension();
        this.getEquals(false).forEach(id -> dimension.put(0.0, id));
        this.getEquals(true).forEach(id -> dimension.put(1.0, id));
        return dimension;
    }

    @Override
    public StringDimension toStringDimension() {
        StringDimension dimension = new StringDimension();
        this.getEquals(false).forEach(id -> dimension.put("false", id));
        this.getEquals(true).forEach(id -> dimension.put("true", id));
        return dimension;
    }

    @Override
    public boolean isRelationship() {
        return false;
    }
}