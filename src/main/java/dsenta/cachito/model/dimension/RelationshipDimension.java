package dsenta.cachito.model.dimension;

import java.io.Serializable;

import dsenta.queryablemap.wgb.WhiteGreyBlackTree;

public abstract class RelationshipDimension extends Dimension<Long> implements Serializable {
    private static final long serialVersionUID = 7160596439241755408L;

    protected RelationshipDimension() {
        super(new WhiteGreyBlackTree<>(), Long.class);
    }

    @Override
    public boolean isRelationship() {
        return true;
    }
}