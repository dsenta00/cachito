package dsenta.cachito.model.dimension;

import java.io.Serializable;

public class RelationshipOneDimension extends RelationshipDimension implements Serializable {
    private static final long serialVersionUID = -7090053200073761440L;

    @Override
    public RelationshipOneDimension toRelationshipOneDimension() {
        return this;
    }

    @Override
    public RelationshipOneManyDimension toRelationshipOneManyDimension() {
        RelationshipOneManyDimension dimension = new RelationshipOneManyDimension();
        dimension.valueIdsMap = this.valueIdsMap;
        return dimension;
    }

    @Override
    public RelationshipZeroManyDimension toRelationshipZeroManyDimension() {
        RelationshipZeroManyDimension dimension = new RelationshipZeroManyDimension();
        dimension.valueIdsMap = this.valueIdsMap;
        return dimension;
    }

    @Override
    public RelationshipZeroOneDimension toRelationshipZeroOneDimension() {
        RelationshipZeroOneDimension dimension = new RelationshipZeroOneDimension();
        dimension.valueIdsMap = this.valueIdsMap;
        return dimension;
    }
}