package dsenta.cachito.model.dimension;

import java.io.Serializable;

public class RelationshipOneManyDimension extends RelationshipDimension implements Serializable {
    private static final long serialVersionUID = -2354879240212022199L;

    @Override
    public RelationshipOneManyDimension toRelationshipOneManyDimension() {
        return this;
    }

    @Override
    public RelationshipZeroManyDimension toRelationshipZeroManyDimension() {
        RelationshipZeroManyDimension dimension = new RelationshipZeroManyDimension();
        dimension.valueIdsMap = this.valueIdsMap;
        return dimension;
    }
}